package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomBlockTags;
import com.freeranger.dark_caverns.registry.CustomItems;
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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public final class LuminiteFoxEntity extends AbstractAnimatedPathfinderEntity {
    public LuminiteFoxEntity(EntityType<? extends LuminiteFoxEntity> type, Level level) {
        super(type, level, "luminite_fox", "run");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new PanicGoal(this, 1.6));
        goalSelector.addGoal(
                2, new TemptGoal(this, 1.5, Ingredient.of(CustomItems.LUMINITE_DUST.get()), false));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CustomSoundEvents.LUMINITE_FOX_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CustomSoundEvents.LUMINITE_FOX_DEATH.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CustomSoundEvents.LUMINITE_FOX_AMBIENT.get();
    }

    public static boolean canSpawn(
            EntityType<LuminiteFoxEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return CavernSpawnRules.creatureOn(
                level,
                reason,
                pos,
                random,
                ServerConfig.luminiteFoxSpawnChance(),
                CustomBlockTags.ROCKY_CREATURE_SPAWNABLE_ON);
    }
}
