package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.PortedCreature;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("deprecation") // GeckoLib 4.9.2 requires these deprecated abstract overrides.
public final class PortedCreatureModel extends GeoModel<PortedCreature> {
    @Override
    public ResourceLocation getModelResource(PortedCreature entity) {
        return resource("geo/" + variantName(entity) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PortedCreature entity) {
        return resource("textures/entity/" + variantName(entity) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(PortedCreature entity) {
        return resource("animations/" + variantName(entity) + ".animation.json");
    }

    private static String variantName(PortedCreature entity) {
        return entity.variant().name().toLowerCase(java.util.Locale.ROOT);
    }

    private static ResourceLocation resource(String path) {
        return DarkCaverns.id(path);
    }
}
