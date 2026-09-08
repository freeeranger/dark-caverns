package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

final class DarkCavernsTags {
    private DarkCavernsTags() {}

    static TagKey<Block> commonBlock(String path) {
        return TagKey.create(
                Registries.BLOCK,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", path));
    }

    static TagKey<Item> commonItem(String path) {
        return TagKey.create(
                Registries.ITEM,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", path));
    }

    static TagKey<Block> modBlock(String path) {
        return TagKey.create(Registries.BLOCK, DarkCaverns.id(path));
    }

    static TagKey<Biome> modBiome(String path) {
        return TagKey.create(Registries.BIOME, DarkCaverns.id(path));
    }

    static TagKey<Structure> modStructure(String path) {
        return TagKey.create(Registries.STRUCTURE, DarkCaverns.id(path));
    }
}
