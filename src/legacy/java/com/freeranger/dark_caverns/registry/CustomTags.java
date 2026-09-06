package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class CustomTags {
    public static class Blocks {
        public static final ITag.INamedTag<Block> BASE_STONE = BlockTags.createOptional(
                new ResourceLocation(DarkCaverns.MOD_ID, "dark_caverns_base_stone")
        );

        public static final ITag.INamedTag<Block> BASE_CARVEABLE = BlockTags.createOptional(
                new ResourceLocation(DarkCaverns.MOD_ID, "dark_caverns_base_carveable")
        );
    };
}
