package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.core.ArmorEffects;
import com.freeranger.dark_caverns.core.ExplorationTrades;
import com.freeranger.dark_caverns.registry.CustomArmorMaterials;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomCreativeTabs;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomParticles;
import com.freeranger.dark_caverns.registry.CustomSoundEvents;
import com.freeranger.dark_caverns.registry.CustomSpawnEggs;
import com.freeranger.dark_caverns.registry.CustomStructureTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(DarkCaverns.MOD_ID)
public final class DarkCaverns {
    public static final String MOD_ID = "dark_caverns";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public DarkCaverns(IEventBus modBus, ModContainer modContainer) {
        CustomBlocks.register(modBus);
        CustomArmorMaterials.register(modBus);
        CustomAttachments.register(modBus);
        CustomEntityTypes.register(modBus);
        CustomItems.register(modBus);
        CustomEquipment.register(modBus);
        CustomSpawnEggs.register(modBus);
        CustomCreativeTabs.register(modBus);
        CustomSoundEvents.register(modBus);
        CustomParticles.register(modBus);
        CustomFeatures.register(modBus);
        CustomStructureTypes.register(modBus);

        ArmorEffects.register(NeoForge.EVENT_BUS);
        ExplorationTrades.register(NeoForge.EVENT_BUS);

        modContainer.registerConfig(
                ModConfig.Type.SERVER, ServerConfig.SPEC, MOD_ID + "-server.toml");
        LOGGER.info("Initializing Dark Caverns for NeoForge 1.21.1");
    }
}
