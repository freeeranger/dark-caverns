package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.VariantMonsterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Selects model resources from the monster's registered variant. */
@SuppressWarnings("deprecation") // GeckoLib 4.9.2 requires these deprecated abstract overrides.
public final class VariantMonsterModel extends GeoModel<VariantMonsterEntity> {
    @Override
    public ResourceLocation getModelResource(VariantMonsterEntity entity) {
        return resource("geo/" + variantName(entity) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VariantMonsterEntity entity) {
        return resource("textures/entity/" + variantName(entity) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(VariantMonsterEntity entity) {
        return resource("animations/" + variantName(entity) + ".animation.json");
    }

    private static String variantName(VariantMonsterEntity entity) {
        return entity.variant().name().toLowerCase(java.util.Locale.ROOT);
    }

    private static ResourceLocation resource(String path) {
        return DarkCaverns.id(path);
    }
}
