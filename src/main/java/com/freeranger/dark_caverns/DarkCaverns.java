package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.ModRegistries;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(DarkCaverns.MOD_ID)
public final class DarkCaverns {
    public static final String MOD_ID = "dark_caverns";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DarkCaverns(IEventBus modBus, ModContainer modContainer) {
        ModRegistries.register(modBus);

        modContainer.registerConfig(
                ModConfig.Type.COMMON, DarkCavernsConfig.COMMON_SPEC, MOD_ID + "-common.toml");
        modContainer.registerConfig(
                ModConfig.Type.CLIENT, DarkCavernsConfig.CLIENT_SPEC, MOD_ID + "-client.toml");

        LOGGER.info("Initializing Dark Caverns for NeoForge 1.21.1");
    }
}
