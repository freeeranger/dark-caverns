package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class ConfiguredStructures {
    public static StructureFeature<?, ?> CONFIGURED_FORGOTTEN_TOWER = CustomStructures.FORGOTTEN_TOWER.get().configured(IFeatureConfig.NONE);
    public static StructureFeature<?, ?> CONFIGURED_SACRET_TORCH = CustomStructures.SACRET_TORCH.get().configured(IFeatureConfig.NONE);
    public static StructureFeature<?, ?> CONFIGURED_TERRITORY_MARKER = CustomStructures.TERRITORY_MARKER.get().configured(IFeatureConfig.NONE);
    public static StructureFeature<?, ?> CONFIGURED_SHROOMIE_HOUSE = CustomStructures.SHROOMIE_HOUSE.get().configured(IFeatureConfig.NONE);

    public static void registerConfiguredStructures() {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(DarkCaverns.MOD_ID, "configured_forgotten_tower"), CONFIGURED_FORGOTTEN_TOWER);
        Registry.register(registry, new ResourceLocation(DarkCaverns.MOD_ID, "configured_sacret_torch"), CONFIGURED_SACRET_TORCH);
        Registry.register(registry, new ResourceLocation(DarkCaverns.MOD_ID, "configured_shroomie_house"), CONFIGURED_SHROOMIE_HOUSE);

        FlatGenerationSettings.STRUCTURE_FEATURES.put(CustomStructures.FORGOTTEN_TOWER.get(), CONFIGURED_FORGOTTEN_TOWER);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(CustomStructures.SACRET_TORCH.get(), CONFIGURED_SACRET_TORCH);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(CustomStructures.TERRITORY_MARKER.get(), CONFIGURED_TERRITORY_MARKER);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(CustomStructures.SHROOMIE_HOUSE.get(), CONFIGURED_SHROOMIE_HOUSE);
    }
}
