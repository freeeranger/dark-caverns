package com.freeranger.dark_caverns.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ShroomlingEntityModel extends AnimatedGeoModel<ShroomlingEntity> {
    @Override
    public ResourceLocation getModelLocation(ShroomlingEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "geo/shroomling.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ShroomlingEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/entity/shroomling.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ShroomlingEntity animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/shroomling.animation.json");
    }

    @Override
    public void setLivingAnimations(ShroomlingEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
