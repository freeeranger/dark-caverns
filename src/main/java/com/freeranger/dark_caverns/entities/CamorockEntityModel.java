package com.freeranger.dark_caverns.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CamorockEntityModel extends AnimatedGeoModel<CamorockEntity> {
    @Override
    public ResourceLocation getModelLocation(CamorockEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "geo/camorock.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(CamorockEntity object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/entity/camorock.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(CamorockEntity animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/camorock.animation.json");
    }

    @Override
    public void setLivingAnimations(CamorockEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
