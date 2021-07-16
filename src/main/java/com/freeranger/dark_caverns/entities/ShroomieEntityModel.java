package com.freeranger.dark_caverns.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ShroomieEntityModel extends AnimatedGeoModel<ShroomieEntity> {
    @Override
    public ResourceLocation getModelLocation(ShroomieEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "geo/shroomie.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ShroomieEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/entity/shroomie.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ShroomieEntity animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/shroomie.animation.json");
    }

    @Override
    public void setLivingAnimations(ShroomieEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
