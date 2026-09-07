package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.events.CorruptedPearlTeleportEvent;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomItems;
import java.util.Comparator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.NeoForge;

public final class CorruptedPearlEntity extends DarkCavernsThrowableItemProjectile {
    public CorruptedPearlEntity(EntityType<? extends CorruptedPearlEntity> type, Level level) {
        super(type, level);
    }

    public CorruptedPearlEntity(Level level, LivingEntity owner) {
        super(CustomEntityTypes.CORRUPTED_PEARL.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return CustomItems.CORRUPTED_PEARL.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(damageSources().thrown(this, getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        for (int i = 0; i < 32; i++) {
            level().addParticle(
                            ParticleTypes.PORTAL,
                            getX(),
                            getY() + random.nextDouble() * 2.0,
                            getZ(),
                            random.nextGaussian(),
                            0.0,
                            random.nextGaussian());
        }

        if (!level().isClientSide && !isRemoved()) {
            Entity owner = getOwner();
            if (owner != null) {
                double radius = DarkCavernsConfig.COMMON.corruptedPearlScanRadius.get();
                level()
                        .getEntities(
                                owner,
                                owner.getBoundingBox().inflate(radius),
                                entity ->
                                        entity instanceof LivingEntity
                                                && !(entity instanceof Player))
                        .stream()
                        .min(Comparator.comparingDouble(owner::distanceToSqr))
                        .ifPresent(
                                victim -> {
                                    double targetX = getX();
                                    double targetY = getY();
                                    double targetZ = getZ();
                                    if (owner instanceof ServerPlayer player) {
                                        if (!player.connection.isAcceptingMessages()
                                                || player.level() != level()
                                                || player.isSleeping()) {
                                            return;
                                        }
                                        CorruptedPearlTeleportEvent event =
                                                NeoForge.EVENT_BUS.post(
                                                        new CorruptedPearlTeleportEvent(
                                                                player, targetX, targetY, targetZ,
                                                                this, 5.0F));
                                        if (event.isCanceled()) {
                                            return;
                                        }
                                        targetX = event.getTargetX();
                                        targetY = event.getTargetY();
                                        targetZ = event.getTargetZ();
                                    }
                                    if (victim.isPassenger()) {
                                        victim.unRide();
                                    }
                                    victim.teleportTo(targetX, targetY, targetZ);
                                    victim.resetFallDistance();
                                });
            }
            discard();
        }
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        if (owner instanceof Player && !owner.isAlive()) {
            discard();
        } else {
            super.tick();
        }
    }
}
