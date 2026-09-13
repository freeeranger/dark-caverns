package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public final class TwistwoodSaplingBlock extends SaplingBlock {
    public TwistwoodSaplingBlock(TreeGrower grower, Properties properties) {
        super(grower, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(CustomBlocks.OVERGROWN_CARFSTONE.get())
                || super.mayPlaceOn(state, level, pos);
    }

    @Override
    protected void randomTick(
            BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // An underground tree must be renewable without skylight or artificial lighting.
        if (level.isAreaLoaded(pos, 8) && random.nextInt(7) == 0)
            advanceTree(level, pos, state, random);
    }
}
