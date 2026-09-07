package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class ShroombombEntity extends DarkCavernsThrowableItemProjectile {
    public ShroombombEntity(EntityType<? extends ShroombombEntity> type, Level level) {
        super(type, level);
    }

    public ShroombombEntity(Level level, LivingEntity owner) {
        super(CustomEntityTypes.SHROOMBOMB.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return CustomItems.SHROOMBOMB.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            level().explode(
                            this,
                            getX(),
                            getY(),
                            getZ(),
                            ServerConfig.shroombombExplosionPower(),
                            Level.ExplosionInteraction.BLOCK);
        }
        finishImpact();
    }
}
