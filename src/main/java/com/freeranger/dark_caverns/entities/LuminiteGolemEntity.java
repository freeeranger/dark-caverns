package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimationState;

public final class LuminiteGolemEntity extends AbstractAnimatedMonsterEntity {
    private static final byte ATTACK_EVENT = 4;
    private static final int ATTACK_ANIMATION_TICKS = 10;

    private int attackAnimationTick;

    public LuminiteGolemEntity(EntityType<? extends LuminiteGolemEntity> type, Level level) {
        super(type, level, "luminite_golem", "walk");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.KNOCKBACK_RESISTANCE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6, true));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public int attackAnimationTick() {
        return attackAnimationTick;
    }

    @Override
    protected String animationAction(AnimationState<AbstractAnimatedMonsterEntity> state) {
        return attackAnimationTick > 0 ? "attack" : super.animationAction(state);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        startAttackAnimation();
        level().broadcastEntityEvent(this, ATTACK_EVENT);

        float attackDamage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        float randomizedDamage =
                (int) attackDamage > 0
                        ? attackDamage / 2.0F + random.nextInt((int) attackDamage)
                        : attackDamage;
        DamageSource damageSource = damageSources().mobAttack(this);
        boolean hurt = target.hurt(damageSource, randomizedDamage);
        if (hurt) {
            if (target instanceof LivingEntity livingTarget) {
                livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(0.0, 0.5, 0.0));
            }
            if (level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
            }
            setLastHurtMob(target);
        }
        playSound(CustomSoundEvents.LUMINITE_GOLEM_ATTACK.get(), 1.0F, 1.0F);
        return hurt;
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == ATTACK_EVENT) {
            startAttackAnimation();
        } else {
            super.handleEntityEvent(eventId);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (attackAnimationTick > 0) {
            attackAnimationTick--;
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(CustomSoundEvents.LUMINITE_GOLEM_STEP.get(), 1.0F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CustomSoundEvents.LUMINITE_GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CustomSoundEvents.LUMINITE_GOLEM_DEATH.get();
    }

    public static boolean canSpawn(
            EntityType<LuminiteGolemEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return random.nextInt(ServerConfig.luminiteGolemSpawnChance()) == 0
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    private void startAttackAnimation() {
        attackAnimationTick = ATTACK_ANIMATION_TICKS;
    }
}
