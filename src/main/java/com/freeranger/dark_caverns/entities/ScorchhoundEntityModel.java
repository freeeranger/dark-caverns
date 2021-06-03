package com.freeranger.dark_caverns.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ScorchhoundEntityModel extends AnimatedGeoModel<ScorchhoundEntity> {
    @Override
    public ResourceLocation getModelLocation(ScorchhoundEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "geo/scorchhound.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ScorchhoundEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/entity/scorchhound.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ScorchhoundEntity animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/scorchhound.animation.json");
    }

    @Override
    public void setLivingAnimations(ScorchhoundEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
