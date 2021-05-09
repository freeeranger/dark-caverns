package com.freeranger.dark_caverns.items;

import com.freeranger.dark_caverns.blocks.CrackedBedrockBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.server.ServerWorld;

public class KeyToTheCavernsItem extends Item {
    public KeyToTheCavernsItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        // TODO first test in single player then test on server
        if(context.getLevel() instanceof ServerWorld && context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrackedBedrockBlock){
            context.getLevel().setBlock(context.getClickedPos(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
            return ActionResultType.CONSUME;
        }
        return ActionResultType.FAIL;
    }
}
