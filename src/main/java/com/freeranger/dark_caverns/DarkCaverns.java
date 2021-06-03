package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.capabilities.GatewayCooldownCapability;
import com.freeranger.dark_caverns.core.EventHandler;
import com.freeranger.dark_caverns.data.CustomBlockTags;
import com.freeranger.dark_caverns.entities.ScorchhoundEntity;
import com.freeranger.dark_caverns.entities.ScorchlingEntity;
import com.freeranger.dark_caverns.items.CustomSpawnEggItem;
import com.freeranger.dark_caverns.registry.*;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import software.bernie.geckolib3.GeckoLib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
				CustomStructures.STRUCTURES
		};

		for (DeferredRegister<?> register : registers) {
			register.register(bus);
		}

		MinecraftForge.EVENT_BUS.addListener(this::gen);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::biomeModification);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void biomeModification(final BiomeLoadingEvent event) {
		if(event.getCategory() == Biome.Category.FOREST) {
			if(event.getName() != null &&
					(event.getName() == Biomes.DARK_FOREST.getRegistryName()
					|| event.getName() == Biomes.DARK_FOREST_HILLS.getRegistryName()))
				return;

			event.getGeneration().getStructures().add(() -> ConfiguredStructures.CONFIGURED_FORGOTTEN_TOWER);
		}
	}

	public void setup(FMLCommonSetupEvent event) {
		GatewayCooldownCapability.register();

		event.enqueueWork(() -> {
			CustomEntityTypes.registerSpawnPlacements();
			CustomCarvers.register();
			CustomBiomes.toDictionary();
			CustomDimensions.register();
			CustomFeatures.register();
			CustomStructures.setupStructures();
			ConfiguredStructures.registerConfiguredStructures();
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
		event.put(CustomEntityTypes.SCORCHHOUND_ENTITY.get(), ScorchhoundEntity.ATTRIBUTE_MAP);
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
				.count(116));
	}

	private static Method GETCODEC_METHOD;
	public void addDimensionalSpacing(final WorldEvent.Load event) {
		if(event.getWorld() instanceof ServerWorld){
			ServerWorld serverWorld = (ServerWorld)event.getWorld();

			try {
				if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
				if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
			}
			catch(Exception e){
				System.out.println("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
			}

			if(serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator &&
					serverWorld.dimension().equals(World.OVERWORLD)){
				return;
			}

			Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
			tempMap.putIfAbsent(CustomStructures.FORGOTTEN_TOWER.get(), DimensionStructuresSettings.DEFAULTS.get(CustomStructures.FORGOTTEN_TOWER.get()));
			serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
		}
	}
}