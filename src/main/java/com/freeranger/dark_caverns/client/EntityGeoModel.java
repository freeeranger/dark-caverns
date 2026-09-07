package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/** Resolves the conventional model, texture, and animation paths for one entity type. */
@SuppressWarnings("deprecation") // GeckoLib 4.9.2 requires these deprecated abstract overrides.
public final class EntityGeoModel<T extends Entity & GeoAnimatable> extends GeoModel<T> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public EntityGeoModel(String entityName) {
        model = DarkCaverns.id("geo/" + entityName + ".geo.json");
        texture = DarkCaverns.id("textures/entity/" + entityName + ".png");
        animation = DarkCaverns.id("animations/" + entityName + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return animation;
    }
}
