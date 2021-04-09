package com.freeranger.dark_caverns.registry;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CustomBiomeProvider;
import com.freeranger.dark_caverns.generation.CustomChunkGenerator;

public class CustomDimensions {

    public static final RegistryKey<DimensionType> UNDERGARDEN_DIMENSION = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, name("dark_caverns"));
    public static final RegistryKey<World> UNDERGARDEN_WORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, name("dark_caverns"));

    private static ResourceLocation name(String name) {
        return new ResourceLocation(DarkCaverns.MOD_ID, name);
    }

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, name("chunk_generator"), CustomChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, name("biome_provider"), CustomBiomeProvider.CODEC);
    }
}