package com.freeranger.dark_caverns.client;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.config.ClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

/** Physical-client entry point for rendering, particles, and client configuration. */
@Mod(value = DarkCaverns.MOD_ID, dist = Dist.CLIENT)
public final class DarkCavernsClient {
    public DarkCavernsClient(IEventBus modBus, ModContainer modContainer) {
        ClientModEvents.register(modBus);
        LuminiteHelmetLighting.register(NeoForge.EVENT_BUS);
        ItemTooltips.register(NeoForge.EVENT_BUS);
        modContainer.registerConfig(
                ModConfig.Type.CLIENT, ClientConfig.SPEC, DarkCaverns.MOD_ID + "-client.toml");
    }
}
