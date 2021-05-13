package com.freeranger.dark_caverns.items;

import com.freeranger.dark_caverns.blocks.CrackedBedrockBlock;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class KeyToTheCavernsItem extends Item {
    public KeyToTheCavernsItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrackedBedrockBlock){
            if(context.getLevel() instanceof ServerWorld) {
                context.getLevel().setBlock(context.getClickedPos(), CustomBlocks.GATEWAY_TO_THE_CAVERNS.get().defaultBlockState(), 2);
                if (context.getPlayer() != null) {
                    if (!context.getPlayer().abilities.instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                }
            }else{
                BlockPos pos = context.getClickedPos();
                for(int i = 0; i < 10; i++) {
                    context.getLevel().addParticle(ParticleTypes.CLOUD, pos.getX()+.5f, pos.getY() + 1, pos.getZ()+.5f, randomFloat() / 5, random.nextFloat() / 5, randomFloat() / 5);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    float randomFloat(){
        Random rand = new Random();
        return rand.nextInt(2) == 0 ? rand.nextFloat() : -(rand.nextFloat());
    }
}
