package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.data.CustomBlockTags;
import com.freeranger.dark_caverns.entities.ScorchlingEntity;
import com.freeranger.dark_caverns.entities.ScorchlingEntityRenderer;
import com.freeranger.dark_caverns.registry.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
				CustomBlocks.BLOCKS,
				CustomItems.ITEMS,
				CustomParticles.PARTICLES,
				CustomBiomes.BIOMES,
				CustomCarvers.CARVERS,
				CustomFeatures.FEATURES,
				CustomSurfaceBuilders.SURFACE_BUILDERS,
				CustomEntityTypes.ENTITIES
		};

		for (DeferredRegister<?> register : registers) {
			register.register(bus);
		}
	}

	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CustomCarvers.register();
			CustomBiomes.toDictionary();
			CustomDimensions.register();
			CustomFeatures.register();
		});
	}

	@SubscribeEvent
	public void registerAttributes(EntityAttributeCreationEvent event){
		event.put(CustomEntityTypes.SCORCHLING_ENTITY.get(),
				MonsterEntity.createMonsterAttributes()
						.add(Attributes.ATTACK_DAMAGE, 4.0D)
						.add(Attributes.ATTACK_KNOCKBACK, 3.0D)
						.add(Attributes.ARMOR, 1.0D)
						.add(Attributes.MOVEMENT_SPEED, 0.5D)
						.add(Attributes.FOLLOW_RANGE, 24.0D)
						.build()
		);
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
}