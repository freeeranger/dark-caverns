package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

final class DarkCavernsBiomeTagsProvider extends TagsProvider<Biome> {
    DarkCavernsBiomeTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, lookupProvider, DarkCaverns.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DarkCavernsTags.modBiome("has_structure/forgotten_tower"))
                .add(
                        Biomes.FOREST,
                        Biomes.FLOWER_FOREST,
                        Biomes.BIRCH_FOREST,
                        Biomes.OLD_GROWTH_BIRCH_FOREST);
        tag(DarkCavernsTags.modBiome("has_structure/sacret_torch"))
                .add(ResourceKey.create(Registries.BIOME, DarkCaverns.id("molten_depths")));
        tag(DarkCavernsTags.modBiome("has_structure/shroomie_house"))
                .add(ResourceKey.create(Registries.BIOME, DarkCaverns.id("glimmershroom_forest")));
        tag(DarkCavernsTags.modBiome("has_structure/territory_marker"))
                .add(ResourceKey.create(Registries.BIOME, DarkCaverns.id("molten_depths")));
    }
}
