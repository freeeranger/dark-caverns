package com.freeranger.dark_caverns.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Common movement animation plumbing for Dark Caverns pathfinding creatures. */
public abstract class AbstractAnimatedPathfinderEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final String animationPrefix;
    private final String movingAnimation;

    protected AbstractAnimatedPathfinderEntity(
            EntityType<? extends AbstractAnimatedPathfinderEntity> type,
            Level level,
            String animationPrefix,
            String movingAnimation) {
        super(type, level);
        this.animationPrefix = animationPrefix;
        this.movingAnimation = movingAnimation;
    }

    @Override
    public final void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationState));
    }

    private PlayState animationState(AnimationState<AbstractAnimatedPathfinderEntity> state) {
        String action = state.isMoving() ? movingAnimation : "idle";
        return state.setAndContinue(
                RawAnimation.begin().thenLoop("animation." + animationPrefix + "." + action));
    }

    @Override
    public final AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
