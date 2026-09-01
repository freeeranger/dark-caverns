package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class LuminiteFoxEntityModel extends AnimatedGeoModel<LuminiteFoxEntity> {
    @Override
    public ResourceLocation getModelLocation(LuminiteFoxEntity object) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "geo/luminite_fox.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(LuminiteFoxEntity object) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "textures/entity/luminite_fox.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(LuminiteFoxEntity animatable) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "animations/luminite_fox.animation.json");
    }

    @Override
    public void setLivingAnimations(LuminiteFoxEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
