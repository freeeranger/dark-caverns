package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import com.freeranger.dark_caverns.registry.ShroomieTrades;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Random;

public class ShroomieEntity extends AbstractVillagerEntity implements IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);

    public static AttributeModifierMap ATTRIBUTE_MAP = createLivingAttributes()
            .add(Attributes.MAX_HEALTH, 15D)
            .add(Attributes.MOVEMENT_SPEED, .25D)
            .add(Attributes.FOLLOW_RANGE, 24D)
            .build();

    public ShroomieEntity(EntityType<? extends ShroomieEntity> type, World world) {
        super(type, world);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.shroomie.walk", true));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.shroomie.idle", true));
        return PlayState.CONTINUE;
    }

    public static boolean canShroomieSpawn(EntityType<? extends CreatureEntity> type, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random rand){
        return rand.nextInt(6) == 0 && worldIn.getBlockState(pos.below()).is(CustomBlocks.GLIMMERGRASS_BLOCK.get());
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
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(1, new LookAtCustomerGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        super.registerGoals();
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return CustomSoundEvents.SHROOMIE_HURT.get(); }

    @Override
    protected SoundEvent getDeathSound() {
        return CustomSoundEvents.SHROOMIE_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return CustomSoundEvents.SHROOMIE_AMBIENT.get();
    }

    public SoundEvent getNotifyTradeSound() {
        return CustomSoundEvents.SHROOMIE_TRADE_YES.get();
    }

    protected SoundEvent getTradeUpdatedSound(boolean answer) {
        return answer ? CustomSoundEvents.SHROOMIE_TRADE_YES.get() : CustomSoundEvents.SHROOMIE_TRADE_NO.get();
    }

    public void playCelebrateSound() {
        this.playSound(CustomSoundEvents.SHROOMIE_TRADE_YES.get(), this.getSoundVolume(), this.getVoicePitch());
    }


    public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        if (itemstack.getItem() != CustomItems.SHROOMIE_SPAWN_EGG.get() && this.isAlive() && !this.isTrading() && !this.isBaby()) {
            if (this.getOffers().isEmpty()) {
                return ActionResultType.sidedSuccess(this.level.isClientSide);
            } else {
                if (!this.level.isClientSide) {
                    this.setTradingPlayer(p_230254_1_);
                    this.openTradingScreen(p_230254_1_, this.getDisplayName(), 1);
                }

                return ActionResultType.sidedSuccess(this.level.isClientSide);
            }
        } else {
            return super.mobInteract(p_230254_1_, p_230254_2_);
        }
    }

    @Override
    protected void rewardTradeXp(MerchantOffer p_213713_1_) {
        if (p_213713_1_.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }

    @Override
    protected void updateTrades() {
        VillagerTrades.ITrade[] ashroomietrades$itrade = ShroomieTrades.SHROOMIE_TRADES.get(1);
        VillagerTrades.ITrade[] ashroomietrades$itrade1 = ShroomieTrades.SHROOMIE_TRADES.get(2);
        if (ashroomietrades$itrade != null && ashroomietrades$itrade1 != null) {
            MerchantOffers merchantoffers = this.getOffers();
            this.addOffersFromItemListings(merchantoffers, ashroomietrades$itrade, 5);
            int i = this.random.nextInt(ashroomietrades$itrade1.length);
            VillagerTrades.ITrade shroomietrades$itrade = ashroomietrades$itrade1[i];
            MerchantOffer merchantoffer = shroomietrades$itrade.getOffer(this, this.random);
            if (merchantoffer != null) {
                merchantoffers.add(merchantoffer);
            }

        }
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) { return null; }

    public boolean showProgressBar() {
        return false;
    }
}
