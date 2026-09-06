package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.generation.GatewayTeleports;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class GatewayToTheCavernsBlock extends Block {
    public GatewayToTheCavernsBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level instanceof ServerLevel serverLevel && entity.getVehicle() == null && !entity.isVehicle()) {
            GatewayTeleports.toDarkCaverns(serverLevel, entity);
        }
        super.stepOn(level, pos, state, entity);
    }
}
