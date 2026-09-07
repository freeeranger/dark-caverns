package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import com.freeranger.dark_caverns.registry.ShroomieTrades;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public ShroomieEntity(EntityType<? extends ShroomieEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
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
    public void playCelebrateSound() {
        playSound(CustomSoundEvents.SHROOMIE_TRADE_YES.get(), getSoundVolume(), getVoicePitch());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(CustomItems.SHROOMIE_SPAWN_EGG.get())
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
    }

    @Override
    protected void updateTrades() {
        VillagerTrades.ItemListing[] common = ShroomieTrades.commonTrades();
        VillagerTrades.ItemListing[] rare = ShroomieTrades.rareTrades();
        MerchantOffers offers = getOffers();
        addOffersFromItemListings(offers, common, 5);
        MerchantOffer rareOffer = rare[random.nextInt(rare.length)].getOffer(this, random);
        if (rareOffer != null) {
            offers.add(rareOffer);
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
        return random.nextInt(DarkCavernsConfig.COMMON.shroomieSpawnChance.get()) == 0
                && level.getBlockState(pos.below()).is(CustomBlocks.GLIMMERGRASS_BLOCK.get());
    }
}
