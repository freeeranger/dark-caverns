package com.freeranger.dark_caverns.entities;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class ShroomieEntityRenderer extends GeoEntityRenderer {
    public ShroomieEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager, new ShroomieEntityModel());
        this.shadowRadius = 0.3f;
    }
}
