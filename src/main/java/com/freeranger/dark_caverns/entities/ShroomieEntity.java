package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomBlockTags;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import com.freeranger.dark_caverns.registry.CustomSpawnEggs;
import com.freeranger.dark_caverns.registry.ShroomieTrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
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

public final class ShroomieEntity extends AbstractVillager implements GeoEntity {
    private static final String NEXT_RESTOCK_TIME_TAG = "NextRestockGameTime";
    private static final long RESTOCK_INTERVAL = 12_000L;
    private static final int RESTOCK_CHECK_INTERVAL = 20;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private long nextRestockGameTime;

    public ShroomieEntity(EntityType<? extends ShroomieEntity> type, Level level) {
        super(type, level);
        nextRestockGameTime = level.getGameTime() + RESTOCK_INTERVAL;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Zombie.class, 12.0F, 1.1, 1.4));
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 1.0, 1.3));
        goalSelector.addGoal(
                4,
                new TemptGoal(
                        this,
                        1.1,
                        Ingredient.of(
                                CustomBlocks.GLIMMERSHROOM.get(),
                                Items.RED_MUSHROOM,
                                Items.BROWN_MUSHROOM),
                        false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationState));
    }

    private PlayState animationState(AnimationState<ShroomieEntity> state) {
        String action = state.isMoving() ? "walk" : "idle";
        return state.setAndContinue(RawAnimation.begin().thenLoop("animation.shroomie." + action));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CustomSoundEvents.SHROOMIE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CustomSoundEvents.SHROOMIE_DEATH.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CustomSoundEvents.SHROOMIE_AMBIENT.get();
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return CustomSoundEvents.SHROOMIE_TRADE_YES.get();
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean accepted) {
        return accepted
                ? CustomSoundEvents.SHROOMIE_TRADE_YES.get()
                : CustomSoundEvents.SHROOMIE_TRADE_NO.get();
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        super.notifyTradeUpdated(stack);
        if (!level().isClientSide && stack.isEmpty()) {
            level().broadcastEntityEvent(this, (byte) 13);
        }
    }

    @Override
    public void playCelebrateSound() {
        playSound(CustomSoundEvents.SHROOMIE_TRADE_YES.get(), getSoundVolume(), getVoicePitch());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isAlive()) {
            boolean isMushroom =
                    stack.is(CustomBlocks.GLIMMERSHROOM.get().asItem())
                            || stack.is(Items.RED_MUSHROOM)
                            || stack.is(Items.BROWN_MUSHROOM);
            if (isMushroom && getHealth() < getMaxHealth()) {
                heal(4.0F);
                playSound(SoundEvents.GENERIC_EAT, getSoundVolume(), getVoicePitch());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (!level().isClientSide) {
                    level().broadcastEntityEvent(this, (byte) 12);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        if (!stack.is(CustomSpawnEggs.SHROOMIE_SPAWN_EGG.get())
                && isAlive()
                && !isTrading()
                && !isBaby()) {
            if (hand == InteractionHand.MAIN_HAND) {
                player.awardStat(Stats.TALKED_TO_VILLAGER);
            }
            if (!level().isClientSide) {
                if (getOffers().isEmpty()) {
                    return InteractionResult.CONSUME;
                }
                setTradingPlayer(player);
                openTradingScreen(player, getDisplayName(), 1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            level().addFreshEntity(
                            new ExperienceOrb(
                                    level(), getX(), getY() + 0.5, getZ(), 3 + random.nextInt(4)));
        }
        level().broadcastEntityEvent(this, (byte) 14);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 14) {
            addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        } else if (id == 13) {
            addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        } else if (id == 12) {
            addParticlesAroundSelf(ParticleTypes.HEART);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity livingBase) {
        if (livingBase instanceof Player && isAlive() && !level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 13);
        }
        super.setLastHurtByMob(livingBase);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (tickCount % RESTOCK_CHECK_INTERVAL != 0 || isTrading()) {
            return;
        }

        long gameTime = level().getGameTime();
        if (gameTime < nextRestockGameTime) {
            return;
        }

        nextRestockGameTime = gameTime + RESTOCK_INTERVAL;
        if (needsRestock()) {
            restock();
        }
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    public void restock() {
        for (MerchantOffer offer : getOffers()) {
            offer.resetUses();
        }
        nextRestockGameTime = level().getGameTime() + RESTOCK_INTERVAL;
        if (!level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 14);
        }
    }

    private boolean needsRestock() {
        for (MerchantOffer offer : getOffers()) {
            if (offer.needsRestock()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void updateTrades() {
        VillagerTrades.ItemListing[] common = ShroomieTrades.commonTrades();
        VillagerTrades.ItemListing[] rare = ShroomieTrades.rareTrades();
        MerchantOffers offers = getOffers();
        addOffersFromItemListings(offers, common, 5);
        MerchantOffer progressionOffer = ShroomieTrades.progressionTrade().getOffer(this, random);
        if (progressionOffer != null) {
            offers.add(progressionOffer);
        }
        MerchantOffer rareOffer = rare[random.nextInt(rare.length)].getOffer(this, random);
        if (rareOffer != null) {
            offers.add(rareOffer);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong(NEXT_RESTOCK_TIME_TAG, nextRestockGameTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        nextRestockGameTime =
                tag.contains(NEXT_RESTOCK_TIME_TAG, Tag.TAG_LONG)
                        ? tag.getLong(NEXT_RESTOCK_TIME_TAG)
                        : level().getGameTime() + RESTOCK_INTERVAL;
        addMissingProgressionTrade();
    }

    private void addMissingProgressionTrade() {
        if (offers == null
                || offers.stream()
                        .anyMatch(
                                offer ->
                                        offer.getResult()
                                                .is(CustomItems.SHROOMSTONE_PIECE.get()))) {
            return;
        }

        MerchantOffer progressionOffer = ShroomieTrades.progressionTrade().getOffer(this, random);
        if (progressionOffer != null) {
            offers.add(progressionOffer);
        }
    }

    @Nullable @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    public static boolean canSpawn(
            EntityType<ShroomieEntity> type,
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random) {
        return CavernSpawnRules.creatureOn(
                level,
                reason,
                pos,
                random,
                ServerConfig.shroomieSpawnChance(),
                CustomBlockTags.GLIMMERSHROOM_CREATURE_SPAWNABLE_ON);
    }
}
