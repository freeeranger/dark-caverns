package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DarkCaverns.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void onClientSetup (FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.SCORCHLING_ENTITY.get(), ScorchlingEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.SCORCHHOUND_ENTITY.get(), ScorchhoundEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.MOLTENER_ENTITY.get(), MoltenerEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.CAMOROCK_ENTITY.get(), CamorockEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get(), LuminiteGolemEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.LUMINITE_FOX_ENTITY.get(), LuminiteFoxEntityRenderer::new);
    }
}
