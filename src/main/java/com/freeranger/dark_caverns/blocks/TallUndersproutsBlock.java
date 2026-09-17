package com.freeranger.dark_caverns.blocks;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class TallUndersproutsBlock extends DoublePlantBlock {
    private final Supplier<? extends Block> baseBlock;

    public TallUndersproutsBlock(
            BlockBehaviour.Properties properties, Supplier<? extends Block> baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
    }

    @Override
    public MapCodec<? extends DoublePlantBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(baseBlock.get());
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 100;
    }

    @Override
    public int getFireSpreadSpeed(
            BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 60;
    }
}
