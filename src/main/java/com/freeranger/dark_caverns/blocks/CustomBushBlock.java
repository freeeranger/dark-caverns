package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class CustomBushBlock extends BushBlock {
    public CustomBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get());
    }
}
