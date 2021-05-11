package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomDimensions;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class CustomTeleporter implements ITeleporter {

    private BlockPos pos;

    public CustomTeleporter(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity e = repositionEntity.apply(false);
        if (!(e instanceof ServerPlayerEntity)) {
            return e;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) e;
        Chunk chunk = (Chunk) destWorld.getChunk(pos);
        player.teleportTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        return e;
    }
}
