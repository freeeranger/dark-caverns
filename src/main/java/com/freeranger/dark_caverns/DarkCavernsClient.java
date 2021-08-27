package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.core.LuminiteHelmetLighting;
import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.entities.ShroombombEntity;
import com.freeranger.dark_caverns.entities.ThrowableLuminiteTorchEntity;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DarkCaverns.MOD_ID, value = Dist.CLIENT)
public class DarkCavernsClient {
    private static void render(Supplier<? extends Block> block, RenderType render) {
        RenderTypeLookup.setRenderLayer(block.get(), render);
    }

    public static void registerBlockRenderers() {
        render(CustomBlocks.LUMINITE_TORCH, RenderType.cutout());
        render(CustomBlocks.LUMINITE_WALL_TORCH, RenderType.cutout());
        render(CustomBlocks.LUMINITE_LANTERN, RenderType.cutout());
        render(CustomBlocks.GLIMMERGRASS, RenderType.cutout());
        render(CustomBlocks.GLIMMERSHROOM, RenderType.cutout());
        render(CustomBlocks.CHARRED_GRASS, RenderType.cutout());
        render(CustomBlocks.SCORCHED_BERRY_BUSH, RenderType.cutout());
        render(CustomBlocks.CARFSTONE_STAIRS, RenderType.cutout());
        render(CustomBlocks.CARFSTONE_SLAB, RenderType.cutout());
        render(CustomBlocks.SMOOTH_CARFSTONE_STAIRS, RenderType.cutout());
        render(CustomBlocks.SMOOTH_CARFSTONE_SLAB, RenderType.cutout());
        render(CustomBlocks.CARFSTONE_BRICK_STAIRS, RenderType.cutout());
        render(CustomBlocks.CARFSTONE_BRICK_SLAB, RenderType.cutout());
        render(CustomBlocks.CARFSTONE_WALL, RenderType.cutout());
        render(CustomBlocks.CARFSTONE_BRICK_WALL, RenderType.cutout());
        render(CustomBlocks.SMOOTH_CARFSTONE_WALL, RenderType.cutout());
        render(CustomBlocks.MOLTEN_CARFSTONE_STAIRS, RenderType.cutout());
        render(CustomBlocks.MOLTEN_CARFSTONE_SLAB, RenderType.cutout());
        render(CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS, RenderType.cutout());
        render(CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB, RenderType.cutout());
        render(CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS, RenderType.cutout());
        render(CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB, RenderType.cutout());
        render(CustomBlocks.MOLTEN_CARFSTONE_WALL, RenderType.cutout());
        render(CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL, RenderType.cutout());
        render(CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL, RenderType.cutout());
    }

    public static void registerEntityRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.THROWABLE_LUMINITE_TORCH_TYPE,
                new ThrowableLuminiteTorchRenderingFactory());

        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.CORRUPTED_PEARL_TYPE,
                new CorruptedPearlRenderingFactory());

        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.SHROOMBOMB_TYPE,
                new ShroombombRenderingFactory());
    }

    private static class ThrowableLuminiteTorchRenderingFactory implements IRenderFactory<ThrowableLuminiteTorchEntity> {
        @Override
        public EntityRenderer<? super ThrowableLuminiteTorchEntity> createRenderFor(EntityRendererManager manager) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            return new SpriteRenderer<>(manager, itemRenderer);
        }
    }

    private static class ShroombombRenderingFactory implements IRenderFactory<ShroombombEntity> {
        @Override
        public EntityRenderer<? super ShroombombEntity> createRenderFor(EntityRendererManager manager) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            return new SpriteRenderer<>(manager, itemRenderer);
        }
    }

    private static class CorruptedPearlRenderingFactory implements IRenderFactory<CorruptedPearlEntity> {
        @Override
        public EntityRenderer<? super CorruptedPearlEntity> createRenderFor(EntityRendererManager manager) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            return new SpriteRenderer<>(manager, itemRenderer);
        }
    }

    public static void init(){
        MinecraftForge.EVENT_BUS.addListener((LivingEvent.LivingUpdateEvent event) -> LuminiteHelmetLighting.tick(event.getEntityLiving()));
    }
}
