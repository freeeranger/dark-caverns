package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import net.minecraft.client.renderer.entity.ZoglinRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Random;

public class ScorchhoundEntity extends MonsterEntity implements IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);

    public static AttributeModifierMap ATTRIBUTE_MAP = MonsterEntity.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, 6D)
            .add(Attributes.ATTACK_KNOCKBACK, 0.6D)
            .add(Attributes.ARMOR, 6D)
            .add(Attributes.MAX_HEALTH, 40D)
            .add(Attributes.MOVEMENT_SPEED, .2D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 2D)
            .build();

    public ScorchhoundEntity(EntityType<? extends ScorchhoundEntity> type, World world) {
        super(type, world);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scorchhound.run", true));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.scorchhound.idle", true));
        return PlayState.CONTINUE;
    }

    public boolean doHurtTarget(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        } else {
            this.level.broadcastEntityEvent(this, (byte)4);
            this.playSound(SoundEvents.ZOGLIN_ATTACK, 1.0F, this.getVoicePitch());
            return IFlinging.hurtAndThrowTarget(this, (LivingEntity)entity);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte value) {
        if (value == 4) {
            this.playSound(SoundEvents.ZOGLIN_ATTACK, 1.0F, this.getVoicePitch());
        } else {
            super.handleEntityEvent(value);
        }

    }

    protected void blockedByShield(LivingEntity livingEntity) {
        if (!this.isBaby()) {
            IFlinging.throwTarget(this, livingEntity);
        }

    }

    public static boolean canScorchhoundSpawn(EntityType<? extends MonsterEntity> type, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random rand){
        return rand.nextInt(18) == 0 && checkMonsterSpawnRules(type, worldIn, reason, pos, rand);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6d, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CustomSoundEvents.SCORCHLING_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CustomSoundEvents.SCORCHLING_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CustomSoundEvents.SCORCHLING_AMBIENT.get();
    }
}
