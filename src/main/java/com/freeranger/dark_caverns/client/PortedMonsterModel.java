package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.PortedMonster;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("deprecation") // GeckoLib 4.9.2 requires these deprecated abstract overrides.
public final class PortedMonsterModel extends GeoModel<PortedMonster> {
    @Override
    public ResourceLocation getModelResource(PortedMonster entity) {
        return resource("geo/" + variantName(entity) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PortedMonster entity) {
        return resource("textures/entity/" + variantName(entity) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(PortedMonster entity) {
        return resource("animations/" + variantName(entity) + ".animation.json");
    }

    private static String variantName(PortedMonster entity) {
        return entity.variant().name().toLowerCase(java.util.Locale.ROOT);
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(DarkCaverns.MOD_ID, path);
    }
}
