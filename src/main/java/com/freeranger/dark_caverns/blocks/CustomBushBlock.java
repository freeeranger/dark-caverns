package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class CustomBushBlock extends BushBlock {
    Block baseBlock;

    public CustomBushBlock(Properties properties, Block baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.is(baseBlock);
    }
}
