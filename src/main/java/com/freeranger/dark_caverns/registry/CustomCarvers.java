package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.freeranger.dark_caverns.generation.CustomCaveWorldCarver;

public class CustomCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(ForgeRegistries.WORLD_CARVERS, DarkCaverns.MOD_ID);

    public static final RegistryObject<WorldCarver<ProbabilityConfig>> DARK_CAVERNS_CAVE = CARVERS.register(
            "dark_caverns_cave", () -> new CustomCaveWorldCarver(ProbabilityConfig.CODEC));

    public static void register() {
        registerCarver("dark_caverns_cave", DARK_CAVERNS_CAVE.get().configured(new ProbabilityConfig(0.6f)));
    }

    private static <WC extends ICarverConfig> void registerCarver(String name, ConfiguredCarver<WC> carver) {
        Registry.register(WorldGenRegistries.CONFIGURED_CARVER, new ResourceLocation(DarkCaverns.MOD_ID, name), carver);
    }
}