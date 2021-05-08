package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.data.CustomBlockTags;
import com.freeranger.dark_caverns.entities.ScorchlingEntity;
import com.freeranger.dark_caverns.entities.ScorchlingEntityRenderer;
import com.freeranger.dark_caverns.items.CustomSpawnEggItem;
import com.freeranger.dark_caverns.registry.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Direction;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

@Mod(DarkCaverns.MOD_ID)
public class DarkCaverns {

	public static final String MOD_ID = "dark_caverns";

	public DarkCaverns() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		GeckoLib.initialize();
		bus.addListener(this::setup);
		bus.addListener(this::clientSetup);
		bus.addListener(this::gatherData);
		bus.addListener(EventPriority.NORMAL, this::registerAttributes);
		bus.addListener(EventPriority.NORMAL, this::registerRenderers);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DarkCavernsClient::init);

		DeferredRegister<?>[] registers = {
				CustomEntityTypes.ENTITIES,
				CustomBlocks.BLOCKS,
				CustomItems.ITEMS,
				CustomParticles.PARTICLES,
				CustomBiomes.BIOMES,
				CustomCarvers.CARVERS,
				CustomFeatures.FEATURES,
				CustomSoundEvents.SOUNDS,
				CustomSurfaceBuilders.SURFACE_BUILDERS,
		};

		for (DeferredRegister<?> register : registers) {
			register.register(bus);
		}

		MinecraftForge.EVENT_BUS.addListener(this::gen);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CustomEntityTypes.registerSpawnPlacements();
			CustomCarvers.register();
			CustomBiomes.toDictionary();
			CustomDimensions.register();
			CustomFeatures.register();
		});

		DefaultDispenseItemBehavior spawnEggDispenserBehavior = new DefaultDispenseItemBehavior() {
			public ItemStack execute(IBlockSource source, ItemStack stack) {
				Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
				EntityType<?> type = ((CustomSpawnEggItem)stack.getItem()).getType(stack.getTag());
				type.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
				stack.shrink(1);
				return stack;
			}
		};

		DispenserBlock.registerBehavior(CustomItems.SCORCHLING_SPAWN_EGG.get(), spawnEggDispenserBehavior);
	}

	@SubscribeEvent
	public void registerAttributes(EntityAttributeCreationEvent event){
		event.put(CustomEntityTypes.SCORCHLING_ENTITY.get(), ScorchlingEntity.ATTRIBUTE_MAP);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void registerRenderers(final FMLClientSetupEvent event){
		if (!FMLEnvironment.production && !GeckoLibMod.DISABLE_IN_DEV) {
			RenderingRegistry.registerEntityRenderingHandler(CustomEntityTypes.SCORCHLING_ENTITY.get(), ScorchlingEntityRenderer::new);
		}
	}

	public void clientSetup(FMLClientSetupEvent event){
		DarkCavernsClient.registerBlockRenderers();
		DarkCavernsClient.registerEntityRenderers();
	}

	public void gatherData(GatherDataEvent event){
		if(event.includeServer()){
			event.getGenerator().addProvider(new CustomBlockTags(event.getGenerator(), event.getExistingFileHelper()));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void gen(BiomeLoadingEvent event) {
		event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(
				new OreFeatureConfig(
						new BlockMatchRuleTest(Blocks.BEDROCK),
						CustomBlocks.CRACKED_BEDROCK.get().defaultBlockState(),
						3
				)
		).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 5)))
				.squared()
				.count(100));
	}
}