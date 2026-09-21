package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomParticles;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;

public final class ClientModEvents {
    private ClientModEvents() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ClientModEvents::registerParticleProviders);
        modBus.addListener(ClientModEvents::registerEntityRenderers);
        modBus.addListener(ClientModEvents::registerLayerDefinitions);
        modBus.addListener(ClientModEvents::clientSetup);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                CustomParticles.LUMINITE_FLAME.get(), LuminiteFlameParticle.Provider::new);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CustomEntityTypes.SHROOMBOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(
                CustomEntityTypes.CORRUPTED_PEARL.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(
                CustomEntityTypes.SCORCHLING_ENTITY.get(), geoRenderer("scorchling", 0.4F));
        event.registerEntityRenderer(
                CustomEntityTypes.SCORCHHOUND_ENTITY.get(), geoRenderer("scorchhound", 1.0F));
        event.registerEntityRenderer(
                CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get(), geoRenderer("luminite_golem", 0.7F));
        event.registerEntityRenderer(
                CustomEntityTypes.MOLTENER_ENTITY.get(), geoRenderer("moltener", 0.4F));
        event.registerEntityRenderer(
                CustomEntityTypes.CAMOROCK_ENTITY.get(), geoRenderer("camorock", 0.4F));
        event.registerEntityRenderer(
                CustomEntityTypes.LUMINITE_FOX_ENTITY.get(), geoRenderer("luminite_fox", 0.5F));
        event.registerEntityRenderer(
                CustomEntityTypes.SHROOMIE_ENTITY.get(), geoRenderer("shroomie", 0.3F));
        event.registerEntityRenderer(
                CustomEntityTypes.SHROOMLING_ENTITY.get(), geoRenderer("shroomling", 0.6F));
    }

    private static <T extends Entity & GeoAnimatable> EntityRendererProvider<T> geoRenderer(
            String entityName, float shadowRadius) {
        return context ->
                new DarkCavernsGeoRenderer<>(
                        context, new EntityGeoModel<>(entityName), shadowRadius);
    }

    private static void registerLayerDefinitions(
            EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                ModelLayers.createBoatModelName(CustomItems.TWISTWOOD_BOAT_TYPE.getValue()),
                BoatModel::createBodyModel);
        event.registerLayerDefinition(
                ModelLayers.createChestBoatModelName(CustomItems.TWISTWOOD_BOAT_TYPE.getValue()),
                ChestBoatModel::createBodyModel);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Sheets.addWoodType(CustomBlocks.TWISTWOOD_WOOD_TYPE));
    }
}
