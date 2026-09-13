package com.freeranger.dark_caverns.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public final class TwistwoodLogBlock extends RotatedPillarBlock {
    private final Supplier<? extends RotatedPillarBlock> stripped;

    public TwistwoodLogBlock(
            Properties properties, Supplier<? extends RotatedPillarBlock> stripped) {
        super(properties);
        this.stripped = stripped;
    }

    @Override
    public BlockState getToolModifiedState(
            BlockState state, UseOnContext context, ItemAbility ability, boolean simulate) {
        if (ability == ItemAbilities.AXE_STRIP && context.getItemInHand().canPerformAction(ability))
            return stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        return super.getToolModifiedState(state, context, ability, simulate);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(
            BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 5;
    }
}
