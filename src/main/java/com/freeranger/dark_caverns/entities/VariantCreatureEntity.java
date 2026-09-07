package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Shared behavior for creature entity types whose differences are represented by a variant. */
public final class VariantCreatureEntity extends PathfinderMob implements GeoEntity, NeutralMob {
    private static final UniformInt SHROOMLING_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    public enum Variant {
        MOLTENER,
        CAMOROCK,
        LUMINITE_FOX,
        SHROOMLING
    }

    private final Variant variant;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;

    private VariantCreatureEntity(
            EntityType<? extends VariantCreatureEntity> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
        if (!level.isClientSide) {
            registerVariantGoals();
        }
    }

    public static VariantCreatureEntity moltener(
            EntityType<VariantCreatureEntity> type, Level level) {
        return new VariantCreatureEntity(type, level, Variant.MOLTENER);
    }

    public static VariantCreatureEntity camorock(
            EntityType<VariantCreatureEntity> type, Level level) {
        return new VariantCreatureEntity(type, level, Variant.CAMOROCK);
    }

    public static VariantCreatureEntity luminiteFox(
            EntityType<VariantCreatureEntity> type, Level level) {
        return new VariantCreatureEntity(type, level, Variant.LUMINITE_FOX);
    }

    public static VariantCreatureEntity shroomling(
            EntityType<VariantCreatureEntity> type, Level level) {
        return new VariantCreatureEntity(type, level, Variant.SHROOMLING);
    }

    public Variant variant() {
        return variant;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationState));
    }

    private PlayState animationState(AnimationState<VariantCreatureEntity> state) {
        String entityName =
                switch (variant) {
                    case MOLTENER -> "moltener";
                    case CAMOROCK -> "camorock";
                    case LUMINITE_FOX -> "luminite_fox";
                    case SHROOMLING -> "shroomling";
                };
        String movingAnimation =
                switch (variant) {
                    case LUMINITE_FOX, SHROOMLING -> "run";
                    default -> "walk";
                };
        String action = state.isMoving() ? movingAnimation : "idle";
        return state.setAndContinue(
                RawAnimation.begin().thenLoop("animation." + entityName + "." + action));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    public static AttributeSupplier.Builder moltenerAttributes() {
        return baseAttributes(10.0, 0.15);
    }

    public static AttributeSupplier.Builder camorockAttributes() {
        return baseAttributes(10.0, 0.15);
    }

    public static AttributeSupplier.Builder luminiteFoxAttributes() {
        return baseAttributes(8.0, 0.25);
    }

    public static AttributeSupplier.Builder shroomlingAttributes() {
        return baseAttributes(18.0, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_SPEED, 1.4)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0);
    }

    private static AttributeSupplier.Builder baseAttributes(double health, double speed) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        // The variant is assigned after PathfinderMob's constructor invokes this hook.
    }

    private void registerVariantGoals() {
        switch (variant) {
            case MOLTENER -> {
                goalSelector.addGoal(1, new PanicGoal(this, 2.0));
                goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0));
                goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
                goalSelector.addGoal(4, new RandomLookAroundGoal(this));
            }
            case CAMOROCK -> goalSelector.addGoal(1, new PanicGoal(this, 2.0));
            case LUMINITE_FOX -> {
                goalSelector.addGoal(1, new PanicGoal(this, 1.6));
                goalSelector.addGoal(
                        2,
                        new TemptGoal(
                                this,
                                1.5,
                                Ingredient.of(CustomBlocks.LUMINITE_BLOCK.get()),
                                false));
                goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
                goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
                goalSelector.addGoal(5, new RandomLookAroundGoal(this));
            }
            case SHROOMLING -> {
                goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
                goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
                goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
                goalSelector.addGoal(5, new RandomLookAroundGoal(this));
                goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
                targetSelector.addGoal(1, new HurtByTargetGoal(this));
                targetSelector.addGoal(
                        2,
                        new NearestAttackableTargetGoal<>(
                                this, Player.class, 10, true, false, this::isAngryAt));
                targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (variant == Variant.SHROOMLING && level() instanceof ServerLevel serverLevel) {
            updatePersistentAnger(serverLevel, true);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (variant == Variant.SHROOMLING) {
            addPersistentAngerSaveData(tag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (variant == Variant.SHROOMLING) {
            readPersistentAngerSaveData(level(), tag);
        }
    }

    @Override
    public void startPersistentAngerTimer() {
        setRemainingPersistentAngerTime(SHROOMLING_ANGER_TIME.sample(random));
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int ticks) {
        remainingPersistentAngerTime = ticks;
    }

    @Nullable @Override
    public UUID getPersistentAngerTarget() {
        return persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        persistentAngerTarget = target;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return switch (variant) {
            case MOLTENER -> CustomSoundEvents.MOLTENER_HURT.get();
            case CAMOROCK -> CustomSoundEvents.CAMOROCK_HURT.get();
            case LUMINITE_FOX -> CustomSoundEvents.LUMINITE_FOX_HURT.get();
            case SHROOMLING -> CustomSoundEvents.SHROOMLING_HURT.get();
        };
    }

    @Override
    protected SoundEvent getDeathSound() {
        return switch (variant) {
            case MOLTENER -> CustomSoundEvents.MOLTENER_DEATH.get();
            case CAMOROCK -> CustomSoundEvents.CAMOROCK_DEATH.get();
            case LUMINITE_FOX -> CustomSoundEvents.LUMINITE_FOX_DEATH.get();
            case SHROOMLING -> CustomSoundEvents.SHROOMLING_DEATH.get();
        };
    }

    @Nullable @Override
    protected SoundEvent getAmbientSound() {
        return switch (variant) {
            case MOLTENER -> CustomSoundEvents.MOLTENER_AMBIENT.get();
            case CAMOROCK -> null;
            case LUMINITE_FOX -> CustomSoundEvents.LUMINITE_FOX_AMBIENT.get();
            case SHROOMLING -> CustomSoundEvents.SHROOMLING_AMBIENT.get();
        };
    }

    public static boolean canMoltenerSpawn(
            EntityType<VariantCreatureEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return canSpawn(
                type,
                level,
                reason,
                pos,
                random,
                DarkCavernsConfig.COMMON.moltenerSpawnChance.get());
    }

    public static boolean canCamorockSpawn(
            EntityType<VariantCreatureEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return canSpawn(
                type,
                level,
                reason,
                pos,
                random,
                DarkCavernsConfig.COMMON.camorockSpawnChance.get());
    }

    public static boolean canLuminiteFoxSpawn(
            EntityType<VariantCreatureEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return canSpawn(
                type,
                level,
                reason,
                pos,
                random,
                DarkCavernsConfig.COMMON.luminiteFoxSpawnChance.get());
    }

    public static boolean canShroomlingSpawn(
            EntityType<VariantCreatureEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return canSpawn(
                type,
                level,
                reason,
                pos,
                random,
                DarkCavernsConfig.COMMON.shroomlingSpawnChance.get());
    }

    private static boolean canSpawn(
            EntityType<VariantCreatureEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random,
            int chance) {
        return random.nextInt(chance) == 0;
    }
}
