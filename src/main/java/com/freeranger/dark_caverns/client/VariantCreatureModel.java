package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.VariantCreatureEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Selects model resources from the creature's registered variant. */
@SuppressWarnings("deprecation") // GeckoLib 4.9.2 requires these deprecated abstract overrides.
public final class VariantCreatureModel extends GeoModel<VariantCreatureEntity> {
    @Override
    public ResourceLocation getModelResource(VariantCreatureEntity entity) {
        return resource("geo/" + variantName(entity) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VariantCreatureEntity entity) {
        return resource("textures/entity/" + variantName(entity) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(VariantCreatureEntity entity) {
        return resource("animations/" + variantName(entity) + ".animation.json");
    }

    private static String variantName(VariantCreatureEntity entity) {
        return entity.variant().name().toLowerCase(java.util.Locale.ROOT);
    }

    private static ResourceLocation resource(String path) {
        return DarkCaverns.id(path);
    }
}
