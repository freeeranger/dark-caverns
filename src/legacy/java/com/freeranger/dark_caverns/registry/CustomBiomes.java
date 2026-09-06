package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomBiomes {

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, DarkCaverns.MOD_ID);

    public static final RegistryKey<Biome> ROCKY_CAVERNS = register("rocky_caverns");
    public static final RegistryKey<Biome> GLIMMERSHROOM_FOREST = register("glimmershroom_forest");
    public static final RegistryKey<Biome> MOLTEN_DEPTHS = register("molten_depths");

    public static void toDictionary() {
        BiomeDictionary.addTypes(ROCKY_CAVERNS, BiomeDictionary.Type.DEAD);
        BiomeDictionary.addTypes(GLIMMERSHROOM_FOREST, BiomeDictionary.Type.MAGICAL);
        BiomeDictionary.addTypes(MOLTEN_DEPTHS, BiomeDictionary.Type.HOT);
    }

    private static ResourceLocation name(String name) {
        return new ResourceLocation(DarkCaverns.MOD_ID, name);
    }

    private static RegistryKey<Biome> register(String name) {
        BIOMES.register(name, BiomeMaker::theVoidBiome);
        return RegistryKey.create(Registry.BIOME_REGISTRY, name(name));
    }
}