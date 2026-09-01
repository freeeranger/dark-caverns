package com.freeranger.dark_caverns.entities;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class LuminiteGolemEntityModel extends AnimatedGeoModel<LuminiteGolemEntity> {
    @Override
    public ResourceLocation getModelLocation(LuminiteGolemEntity object) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "geo/luminite_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(LuminiteGolemEntity object) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "textures/entity/luminite_golem.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(LuminiteGolemEntity animatable) {
        return new ResourceLocation(DarkCaverns.MOD_ID, "animations/luminite_golem.animation.json");
    }

    @Override
    public void setLivingAnimations(LuminiteGolemEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
    }
}
