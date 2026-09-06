package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.generation.GatewayTeleports;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class GatewayToTheOverworldBlock extends Block {
    public GatewayToTheOverworldBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel serverLevel && entity.getVehicle() == null && !entity.isVehicle()) {
            GatewayTeleports.toOverworld(serverLevel, entity);
        }
        super.entityInside(state, level, pos, entity);
    }
}
