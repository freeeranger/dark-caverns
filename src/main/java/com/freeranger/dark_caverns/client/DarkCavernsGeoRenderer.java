package com.freeranger.dark_caverns.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class DarkCavernsGeoRenderer<T extends Entity & GeoAnimatable>
        extends GeoEntityRenderer<T> {
    public DarkCavernsGeoRenderer(
            EntityRendererProvider.Context context, GeoModel<T> model, float shadowRadius) {
        super(context, model);
        this.shadowRadius = shadowRadius;
    }
}
