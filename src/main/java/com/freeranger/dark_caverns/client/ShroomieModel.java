package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.ShroomieEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class ShroomieModel extends GeoModel<ShroomieEntity> {
    @Override
    public ResourceLocation getModelResource(ShroomieEntity entity) {
        return resource("geo/shroomie.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShroomieEntity entity) {
        return resource("textures/entity/shroomie.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShroomieEntity entity) {
        return resource("animations/shroomie.animation.json");
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(DarkCaverns.MOD_ID, path);
    }
}
