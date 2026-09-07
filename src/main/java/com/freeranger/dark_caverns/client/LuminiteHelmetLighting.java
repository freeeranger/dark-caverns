package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.config.ClientConfig;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class LuminiteHelmetLighting {
    public static final int LIGHT_LEVEL = 15;

    private static final int NEARBY_VISIBILITY_DISTANCE = 24;
    private static final LuminiteHelmetLighting INSTANCE = new LuminiteHelmetLighting();

    private volatile Set<Long> sources = Set.of();

    private LuminiteHelmetLighting() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(INSTANCE::onClientTick);
        gameBus.addListener(INSTANCE::onLogout);
    }

    public static boolean isSource(long packedPos) {
        return INSTANCE.sources.contains(packedPos);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || !ClientConfig.enableDynamicLighting()) {
            clear(minecraft.level);
            return;
        }

        Set<Long> nextSources = new HashSet<>();
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && shouldGlow(minecraft, living)) {
                nextSources.add(
                        BlockPos.containing(living.getX(), living.getEyeY(), living.getZ())
                                .asLong());
            }
        }
        update(level, nextSources);
    }

    private void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        clear(Minecraft.getInstance().level);
    }

    private static boolean shouldGlow(Minecraft minecraft, LivingEntity entity) {
        if (!entity.getItemBySlot(EquipmentSlot.HEAD).is(CustomEquipment.LUMINITE_HELMET.get())) {
            return false;
        }

        double distanceSquared = minecraft.player.distanceToSqr(entity);
        int maximumDistance = ClientConfig.maxDynamicLightDistance();
        if (distanceSquared > maximumDistance * maximumDistance) {
            return false;
        }
        if (distanceSquared < NEARBY_VISIBILITY_DISTANCE * NEARBY_VISIBILITY_DISTANCE) {
            return true;
        }

        Vec3 playerEye = minecraft.player.getEyePosition();
        Vec3 entityEye = entity.getEyePosition();
        return minecraft
                        .level
                        .clip(
                                new ClipContext(
                                        playerEye,
                                        entityEye,
                                        ClipContext.Block.VISUAL,
                                        ClipContext.Fluid.NONE,
                                        entity))
                        .getType()
                == HitResult.Type.MISS;
    }

    private void update(ClientLevel level, Set<Long> nextSources) {
        Set<Long> changed = new HashSet<>(sources);
        changed.addAll(nextSources);
        Set<Long> unchanged = new HashSet<>(sources);
        unchanged.retainAll(nextSources);
        changed.removeAll(unchanged);

        sources = Set.copyOf(nextSources);
        changed.stream()
                .map(BlockPos::of)
                .forEach(level.getChunkSource().getLightEngine()::checkBlock);
    }

    private void clear(ClientLevel level) {
        if (sources.isEmpty()) {
            return;
        }
        Set<Long> previous = sources;
        sources = Set.of();
        if (level != null) {
            previous.stream()
                    .map(BlockPos::of)
                    .forEach(level.getChunkSource().getLightEngine()::checkBlock);
        }
    }
}
