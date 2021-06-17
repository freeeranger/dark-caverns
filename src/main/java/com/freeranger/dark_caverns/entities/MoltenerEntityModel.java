package com.freeranger.dark_caverns.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MoltenerEntityModel extends AnimatedGeoModel<MoltenerEntity> {
    @Override
    public ResourceLocation getModelLocation(MoltenerEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "geo/moltener.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(MoltenerEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/entity/moltener.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(MoltenerEntity animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/moltener.animation.json");
    }

    @Override
    public void setLivingAnimations(MoltenerEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
