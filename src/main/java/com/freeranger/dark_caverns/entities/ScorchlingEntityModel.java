package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ScorchlingEntityModel extends AnimatedGeoModel<ScorchlingEntity> {
    @Override
    public ResourceLocation getModelLocation(ScorchlingEntity object) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "geo/scorchling.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ScorchlingEntity object) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "textures/entity/scorchling.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ScorchlingEntity animatable) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "animations/scorchling.animation.json");
    }

    @Override
    public void setLivingAnimations(ScorchlingEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
