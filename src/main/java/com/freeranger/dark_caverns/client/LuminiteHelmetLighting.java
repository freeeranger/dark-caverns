package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.CustomItems;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = DarkCaverns.MOD_ID, value = Dist.CLIENT)
public final class LuminiteHelmetLighting {
    private static Set<BlockPos> sources = Set.of();

    private LuminiteHelmetLighting() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || !DarkCavernsConfig.CLIENT.enableDynamicLighting.get()) {
            clear(minecraft.level);
            return;
        }

        Set<BlockPos> nextSources = new HashSet<>();
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && shouldGlow(minecraft, living)) {
                nextSources.add(BlockPos.containing(living.getX(), living.getEyeY(), living.getZ()));
            }
        }
        update(level, nextSources);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        clear(Minecraft.getInstance().level);
    }

    public static boolean isSource(BlockPos pos) {
        return sources.contains(pos);
    }

    private static boolean shouldGlow(Minecraft minecraft, LivingEntity entity) {
        if (!entity.getItemBySlot(EquipmentSlot.HEAD).is(CustomItems.LUMINITE_HELMET.get())) {
            return false;
        }

        double distanceSquared = minecraft.player.distanceToSqr(entity);
        int maximumDistance = DarkCavernsConfig.CLIENT.maxDynamicLightDistance.get();
        if (distanceSquared > maximumDistance * maximumDistance) {
            return false;
        }
        if (distanceSquared < 24 * 24) {
            return true;
        }

        Vec3 playerEye = minecraft.player.getEyePosition();
        Vec3 entityEye = entity.getEyePosition();
        return minecraft.level.clip(new ClipContext(
                playerEye,
                entityEye,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                entity
        )).getType() == HitResult.Type.MISS;
    }

    private static void update(ClientLevel level, Set<BlockPos> nextSources) {
        Set<BlockPos> changed = new HashSet<>(sources);
        changed.addAll(nextSources);
        Set<BlockPos> unchanged = new HashSet<>(sources);
        unchanged.retainAll(nextSources);
        changed.removeAll(unchanged);

        sources = Set.copyOf(nextSources);
        changed.forEach(level.getChunkSource().getLightEngine()::checkBlock);
    }

    private static void clear(ClientLevel level) {
        if (sources.isEmpty()) {
            return;
        }
        Set<BlockPos> previous = sources;
        sources = Set.of();
        if (level != null) {
            previous.forEach(level.getChunkSource().getLightEngine()::checkBlock);
        }
    }
}
