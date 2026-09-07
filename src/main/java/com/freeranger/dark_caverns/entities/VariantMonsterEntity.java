package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Shared behavior for monster entity types whose differences are represented by a variant. */
public final class VariantMonsterEntity extends Monster implements GeoEntity {
    public enum Variant {
        SCORCHLING,
        SCORCHHOUND,
        LUMINITE_GOLEM
    }

    private final Variant variant;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private int attackAnimationTick;

    private VariantMonsterEntity(
            EntityType<? extends VariantMonsterEntity> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
        if (!level.isClientSide) {
            registerVariantGoals();
        }
    }

    public static VariantMonsterEntity scorchling(
            EntityType<VariantMonsterEntity> type, Level level) {
        return new VariantMonsterEntity(type, level, Variant.SCORCHLING);
    }

    public static VariantMonsterEntity scorchhound(
            EntityType<VariantMonsterEntity> type, Level level) {
        return new VariantMonsterEntity(type, level, Variant.SCORCHHOUND);
    }

    public static VariantMonsterEntity luminiteGolem(
            EntityType<VariantMonsterEntity> type, Level level) {
        return new VariantMonsterEntity(type, level, Variant.LUMINITE_GOLEM);
    }

    public Variant variant() {
        return variant;
    }

    public int attackAnimationTick() {
        return attackAnimationTick;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationState));
    }

    private PlayState animationState(AnimationState<VariantMonsterEntity> state) {
        String entityName =
                switch (variant) {
                    case SCORCHLING -> "scorchling";
                    case SCORCHHOUND -> "scorchhound";
                    case LUMINITE_GOLEM -> "luminite_golem";
                };
        String action;
        if (variant == Variant.LUMINITE_GOLEM && attackAnimationTick > 0) {
            action = "attack";
        } else if (state.isMoving()) {
            action =
                    switch (variant) {
                        case SCORCHLING, SCORCHHOUND -> "run";
                        case LUMINITE_GOLEM -> "walk";
                    };
        } else {
            action = "idle";
        }
        return state.setAndContinue(
                RawAnimation.begin().thenLoop("animation." + entityName + "." + action));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    public static AttributeSupplier.Builder scorchlingAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.7)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public static AttributeSupplier.Builder scorchhoundAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.6)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    public static AttributeSupplier.Builder luminiteGolemAttributes() {
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
        // The variant is assigned after Monster's constructor invokes this hook.
    }

    private void registerVariantGoals() {
        if (variant == Variant.SCORCHLING) {
            goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0, true));
            goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
            goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
            goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        } else {
            goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6, true));
            goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
            goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
            targetSelector.addGoal(1, new HurtByTargetGoal(this));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (variant == Variant.SCORCHHOUND) {
            if (!(target instanceof LivingEntity livingTarget)) {
                return false;
            }
            level().broadcastEntityEvent(this, (byte) 4);
            playSound(SoundEvents.ZOGLIN_ATTACK, 1.0F, getVoicePitch());
            return HoglinBase.hurtAndThrowTarget(this, livingTarget);
        }

        if (variant == Variant.LUMINITE_GOLEM) {
            attackAnimationTick = 10;
            level().broadcastEntityEvent(this, (byte) 4);

            float attackDamage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
            float randomizedDamage =
                    (int) attackDamage > 0
                            ? attackDamage / 2.0F + random.nextInt((int) attackDamage)
                            : attackDamage;
            DamageSource damageSource = damageSources().mobAttack(this);
            boolean hurt = target.hurt(damageSource, randomizedDamage);
            if (hurt) {
                if (target instanceof LivingEntity livingTarget) {
                    livingTarget.setDeltaMovement(
                            livingTarget.getDeltaMovement().add(0.0, 0.5, 0.0));
                }
                if (level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
                }
                setLastHurtMob(target);
            }
            playSound(CustomSoundEvents.LUMINITE_GOLEM_ATTACK.get(), 1.0F, 1.0F);
            return hurt;
        }

        return super.doHurtTarget(target);
    }

    @Override
    protected void blockedByShield(LivingEntity blockingEntity) {
        if (variant == Variant.SCORCHHOUND
                && !isBaby()
                && DarkCavernsConfig.COMMON.scorchhoundBypassShields.get()) {
            HoglinBase.throwTarget(this, blockingEntity);
            return;
        }
        super.blockedByShield(blockingEntity);
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == 4 && variant == Variant.LUMINITE_GOLEM) {
            attackAnimationTick = 10;
        } else if (eventId == 4 && variant == Variant.SCORCHHOUND) {
            playSound(SoundEvents.ZOGLIN_ATTACK, 1.0F, getVoicePitch());
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
        if (variant == Variant.LUMINITE_GOLEM) {
            playSound(CustomSoundEvents.LUMINITE_GOLEM_STEP.get(), 1.0F, 1.0F);
        } else {
            super.playStepSound(pos, state);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (variant) {
            case SCORCHLING -> CustomSoundEvents.SCORCHLING_HURT.get();
            case SCORCHHOUND -> CustomSoundEvents.SCORCHHOUND_HURT.get();
            case LUMINITE_GOLEM -> CustomSoundEvents.LUMINITE_GOLEM_HURT.get();
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return switch (variant) {
            case SCORCHLING -> CustomSoundEvents.SCORCHLING_DEATH.get();
            case SCORCHHOUND -> CustomSoundEvents.SCORCHHOUND_DEATH.get();
            case LUMINITE_GOLEM -> CustomSoundEvents.LUMINITE_GOLEM_DEATH.get();
        };
    }

    @Nullable @Override
    protected SoundEvent getAmbientSound() {
        return switch (variant) {
            case SCORCHLING -> CustomSoundEvents.SCORCHLING_AMBIENT.get();
            case SCORCHHOUND -> CustomSoundEvents.SCORCHHOUND_AMBIENT.get();
            case LUMINITE_GOLEM -> null;
        };
    }

    public static boolean canScorchlingSpawn(
            EntityType<VariantMonsterEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return oneIn(DarkCavernsConfig.COMMON.scorchlingSpawnChance.get(), random)
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    public static boolean canScorchhoundSpawn(
            EntityType<VariantMonsterEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return oneIn(DarkCavernsConfig.COMMON.scorchhoundSpawnChance.get(), random)
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    public static boolean canLuminiteGolemSpawn(
            EntityType<VariantMonsterEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return oneIn(DarkCavernsConfig.COMMON.luminiteGolemSpawnChance.get(), random)
                && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    private static boolean oneIn(int chance, RandomSource random) {
        return random.nextInt(chance) == 0;
    }
}
