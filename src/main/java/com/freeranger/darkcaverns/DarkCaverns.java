package com.freeranger.darkcaverns;

import com.freeranger.darkcaverns.registries.BlockRegistry;
import com.freeranger.darkcaverns.registries.CreativeTabRegistry;
import com.freeranger.darkcaverns.registries.FeatureRegistry;
import com.freeranger.darkcaverns.registries.ItemRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(DarkCaverns.MODID)
public class DarkCaverns {
    public static final String MODID = "darkcaverns";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public DarkCaverns(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        CreativeTabRegistry.register(modEventBus);
        FeatureRegistry.register(modEventBus);
        
        NeoForge.EVENT_BUS.register(this);
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {}
    
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
    
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            var transparentBlocks = new Block[]{
                BlockRegistry.TWISTWOOD_DOOR.get(),
                BlockRegistry.TWISTWOOD_TRAPDOOR.get(),
                BlockRegistry.TWISTWOOD_LEAVES.get()
            };
            
            for(var block : transparentBlocks) ItemBlockRenderTypes.setRenderLayer(block, RenderType.CUTOUT);
        }
    }
}
