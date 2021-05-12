package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class OverworldTeleporter implements ITeleporter {

    private BlockPos pos;

    public OverworldTeleporter(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);
        if (!(e instanceof ServerPlayerEntity)) {
            return e;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) e;

        BlockPos gatewayPos = findGatewayToTheCavernsBlock(destWorld, pos);

        if(destWorld.getBlockState(gatewayPos).getBlock() != CustomBlocks.GATEWAY_TO_THE_CAVERNS.get()){
            destWorld.setBlock(gatewayPos, CustomBlocks.GATEWAY_TO_THE_CAVERNS.get().defaultBlockState(), 4);
        }

        destWorld.setBlock(new BlockPos(gatewayPos.getX(), gatewayPos.getY() + 1, gatewayPos.getZ()),
                Blocks.AIR.defaultBlockState(), 4);
        destWorld.setBlock(new BlockPos(gatewayPos.getX(), gatewayPos.getY() + 2, gatewayPos.getZ()),
                Blocks.AIR.defaultBlockState(), 4);

        player.teleportTo(gatewayPos.getX() + 0.5D, gatewayPos.getY()+1, gatewayPos.getZ() + 0.5D);

        return e;
    }

    BlockPos findGatewayToTheCavernsBlock(ServerWorld world, BlockPos pos){
        for(int i = 5; i >= 0; i--){
            Block block = world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).getBlock();

            if(block == CustomBlocks.GATEWAY_TO_THE_CAVERNS.get()) return new BlockPos(pos.getX(), i, pos.getZ());
        }
        for(int i = 5; i >= 0; i--){
            Block block = world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).getBlock();

            if(block == Blocks.BEDROCK) return new BlockPos(pos.getX(), i, pos.getZ());
        }
        return new BlockPos(pos.getX(), 4, pos.getZ());
    }
}
