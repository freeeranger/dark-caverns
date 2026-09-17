package com.freeranger.dark_caverns.blocks;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class UndersproutsBlock extends TallGrassBlock {
    private final Supplier<? extends Block> baseBlock;
    private final Supplier<? extends DoublePlantBlock> tallBlock;

    public UndersproutsBlock(
            BlockBehaviour.Properties properties,
            Supplier<? extends Block> baseBlock,
            Supplier<? extends DoublePlantBlock> tallBlock) {
        super(properties);
        this.baseBlock = baseBlock;
        this.tallBlock = tallBlock;
    }

    @Override
    public MapCodec<TallGrassBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(baseBlock.get());
    }

    @Override
    public void performBonemeal(
            ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        DoublePlantBlock tall = tallBlock.get();
        BlockState tallState = tall.defaultBlockState();
        if (tallState.canSurvive(level, pos) && level.isEmptyBlock(pos.above())) {
            DoublePlantBlock.placeAt(level, tallState, pos, Block.UPDATE_CLIENTS);
        }
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
