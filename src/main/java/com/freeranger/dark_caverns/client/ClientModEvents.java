package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomParticles;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@EventBusSubscriber(modid = DarkCaverns.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CustomParticles.LUMINITE_FLAME.get(), LuminiteFlameParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CustomEntityTypes.SHROOMBOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CustomEntityTypes.CORRUPTED_PEARL.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(
                CustomEntityTypes.SCORCHLING_ENTITY.get(),
                geoRenderer(new PortedMonsterModel(), 0.4F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.SCORCHHOUND_ENTITY.get(),
                geoRenderer(new PortedMonsterModel(), 1.0F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get(),
                geoRenderer(new PortedMonsterModel(), 0.7F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.MOLTENER_ENTITY.get(),
                geoRenderer(new PortedCreatureModel(), 0.4F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.CAMOROCK_ENTITY.get(),
                geoRenderer(new PortedCreatureModel(), 0.4F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.LUMINITE_FOX_ENTITY.get(),
                geoRenderer(new PortedCreatureModel(), 0.5F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.SHROOMIE_ENTITY.get(),
                geoRenderer(new ShroomieModel(), 0.3F)
        );
        event.registerEntityRenderer(
                CustomEntityTypes.SHROOMLING_ENTITY.get(),
                geoRenderer(new PortedCreatureModel(), 0.6F)
        );
    }

    private static <T extends Entity & GeoAnimatable> EntityRendererProvider<T> geoRenderer(
            GeoModel<T> model,
            float shadowRadius
    ) {
        return context -> new PortedGeoEntityRenderer<>(context, model, shadowRadius);
    }

    private static final class PortedGeoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
        private PortedGeoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadowRadius) {
            super(context, model);
            this.shadowRadius = shadowRadius;
        }
    }
}
