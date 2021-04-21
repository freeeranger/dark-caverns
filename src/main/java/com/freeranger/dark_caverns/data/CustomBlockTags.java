package com.freeranger.dark_caverns.data;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomTags;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class CustomBlockTags extends BlockTagsProvider {
    public CustomBlockTags(DataGenerator p_i48256_1_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_i48256_1_, DarkCaverns.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Dark Caverns Block Tags";
    }

    @Override
    protected void addTags() {
        tag(CustomTags.Blocks.BASE_STONE).add(
                CustomBlocks.CARFSTONE.get()
        );

        tag(CustomTags.Blocks.BASE_CARVEABLE).add(
                CustomBlocks.CARFSTONE.get(),
                CustomBlocks.SHROOMGRASS_BLOCK.get(),
                CustomBlocks.SHROOMGRASS.get()
        );
    }

    protected TagsProvider.Builder<Block> tag(ITag.INamedTag<Block> tag) {
        return super.tag(tag);
    }
}
