package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class DarkCavernsTeleporter implements ITeleporter {

    private BlockPos pos;

    public DarkCavernsTeleporter(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);

        BlockPos lowestBedrock = calculateLowestBedrockPos(destWorld, pos);
        BlockPos targetBlock1 = new BlockPos(
                lowestBedrock.getX(),
                lowestBedrock.getY() - 1,
                lowestBedrock.getZ()
        );
        BlockPos targetBlock2 = new BlockPos(
                lowestBedrock.getX(),
                lowestBedrock.getY() - 2,
                lowestBedrock.getZ()
        );

        destWorld.setBlock(lowestBedrock, CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get().defaultBlockState(), 4);
        destWorld.setBlock(targetBlock1, Blocks.AIR.defaultBlockState(), 4);
        destWorld.setBlock(targetBlock2, Blocks.AIR.defaultBlockState(), 4);

        if (e instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) e).teleportTo(pos.getX() + 0.5D, targetBlock2.getY(), pos.getZ() + 0.5D);
        } else {
            e.moveTo(pos.getX() + 0.5D, targetBlock2.getY(), pos.getZ() + 0.5D, yaw, e.xRot);
        }

        return e;
    }

    BlockPos calculateLowestBedrockPos(ServerWorld world, BlockPos pos){
        for(int i = 250; i <= 255; i++){
            Block block = world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).getBlock();

            if(block == Blocks.BEDROCK || block == CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()) return new BlockPos(pos.getX(), i, pos.getZ());
        }
        return new BlockPos(pos.getX(), 250, pos.getZ());
    }
}
