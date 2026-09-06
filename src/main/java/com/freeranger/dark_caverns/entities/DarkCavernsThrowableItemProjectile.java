package com.freeranger.dark_caverns.entities;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

abstract class DarkCavernsThrowableItemProjectile extends ThrowableItemProjectile {
    protected DarkCavernsThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    protected DarkCavernsThrowableItemProjectile(
            EntityType<? extends ThrowableItemProjectile> type,
            LivingEntity owner,
            Level level
    ) {
        super(type, owner, level);
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == 3) {
            ItemStack stack = getItem();
            ParticleOptions particle = stack.isEmpty()
                    ? ParticleTypes.ITEM_SNOWBALL
                    : new ItemParticleOption(ParticleTypes.ITEM, stack);
            for (int i = 0; i < 8; i++) {
                level().addParticle(particle, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent(eventId);
        }
    }

    protected void finishImpact() {
        if (!level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }
}
