package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.core.LuminiteHelmetLighting;
import com.freeranger.dark_caverns.entities.ScorchlingEntity;
import com.freeranger.dark_caverns.entities.ScorchlingEntityRenderer;
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
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

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
    }

    public static void registerEntityRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.THROWABLE_LUMINITE_TORCH_TYPE,
                new ThrowableLuminiteTorchRenderingFactory());
    }

    private static class ThrowableLuminiteTorchRenderingFactory implements IRenderFactory<ThrowableLuminiteTorchEntity> {
        @Override
        public EntityRenderer<? super ThrowableLuminiteTorchEntity> createRenderFor(EntityRendererManager manager) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            return new SpriteRenderer<>(manager, itemRenderer);
        }
    }

    public static void init(){
        MinecraftForge.EVENT_BUS.addListener((LivingEvent.LivingUpdateEvent event) -> LuminiteHelmetLighting.tick(event.getEntityLiving()));
    }
}
