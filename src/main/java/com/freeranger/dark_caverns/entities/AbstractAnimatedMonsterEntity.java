package com.freeranger.dark_caverns.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/** Common animation plumbing for Dark Caverns monsters. */
public abstract class AbstractAnimatedMonsterEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final String animationPrefix;
    private final String movingAnimation;

    protected AbstractAnimatedMonsterEntity(
            EntityType<? extends AbstractAnimatedMonsterEntity> type,
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

    private PlayState animationState(AnimationState<AbstractAnimatedMonsterEntity> state) {
        String action = animationAction(state);
        return state.setAndContinue(
                RawAnimation.begin().thenLoop("animation." + animationPrefix + "." + action));
    }

    protected String animationAction(AnimationState<AbstractAnimatedMonsterEntity> state) {
        return state.isMoving() ? movingAnimation : "idle";
    }

    @Override
    public final AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
