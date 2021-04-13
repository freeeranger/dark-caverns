package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.core.LuminiteHelmetLighting;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DarkCaverns.MOD_ID, value = Dist.CLIENT)
public class DarkCavernsClient {

    private static void render(Supplier<? extends Block> block, RenderType render) {
        RenderTypeLookup.setRenderLayer(block.get(), render);
    }

    public static void registerBlockRenderers() {
        RenderType cutout = RenderType.cutout();
        //RenderType mipped = RenderType.cutoutMipped(); Will be used later
        //RenderType translucent = RenderType.translucent(); Will be used later

        render(CustomBlocks.LUMINITE_TORCH, cutout);
        render(CustomBlocks.LUMINITE_WALL_TORCH, cutout);
    }

    public static void init(){
        MinecraftForge.EVENT_BUS.addListener((LivingEvent.LivingUpdateEvent event) -> LuminiteHelmetLighting.tick(event.getEntityLiving()));
    }
}
