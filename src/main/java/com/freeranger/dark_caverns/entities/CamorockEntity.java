package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public final class CamorockEntity extends AbstractAnimatedPathfinderEntity {
    public CamorockEntity(EntityType<? extends CamorockEntity> type, Level level) {
        super(type, level, "camorock", "walk");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new PanicGoal(this, 2.0));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CustomSoundEvents.CAMOROCK_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CustomSoundEvents.CAMOROCK_DEATH.get();
    }

    public static boolean canSpawn(
            EntityType<CamorockEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return random.nextInt(ServerConfig.camorockSpawnChance()) == 0;
    }
}
