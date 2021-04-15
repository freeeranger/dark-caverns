package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.data.CustomBlockTags;
import com.freeranger.dark_caverns.registry.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod(DarkCaverns.MOD_ID)
public class DarkCaverns {

	public static final String MOD_ID = "dark_caverns";

	public DarkCaverns() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::setup);
		bus.addListener(this::clientSetup);
		bus.addListener(this::gatherData);

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