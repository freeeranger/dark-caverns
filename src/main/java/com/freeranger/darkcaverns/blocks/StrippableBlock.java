package com.freeranger.darkcaverns.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class StrippableBlock extends RotatedPillarBlock {
    private Block strippedVersion;

    public StrippableBlock(Properties properties, Block strippedVersion) {
        super(properties);
        this.strippedVersion = strippedVersion;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        ItemStack stack = context.getItemInHand();

        if(!stack.canPerformAction(itemAbility))
            return null;

        if(itemAbility != ItemAbilities.AXE_STRIP)
            return null;

        return strippedVersion.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
    }
}
