package com.freeranger.dark_caverns.items;

import com.freeranger.dark_caverns.blocks.CrackedBedrockBlock;
import com.freeranger.dark_caverns.registry.CustomBlocks;
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
        if(context.getLevel() instanceof ServerWorld && context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrackedBedrockBlock){
            context.getLevel().setBlock(context.getClickedPos(), CustomBlocks.GATEWAY_TO_THE_CAVERNS.get().defaultBlockState(), 2);
            if(context.getPlayer() != null) {
                if (!context.getPlayer().abilities.instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }
}
