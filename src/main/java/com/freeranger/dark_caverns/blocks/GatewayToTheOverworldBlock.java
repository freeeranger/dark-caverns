package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.capabilities.GatewayCooldownCapability;
import com.freeranger.dark_caverns.generation.OverworldTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GatewayToTheOverworldBlock extends Block {
    public GatewayToTheOverworldBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.getVehicle() != null || entity.isVehicle()) return;

        java.util.concurrent.atomic.AtomicInteger cooldown = new java.util.concurrent.atomic.AtomicInteger();
        entity.getCapability(GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY).ifPresent(h -> {
            cooldown.set(h.getCooldown());
        });

        if(world instanceof ServerWorld && world.getServer() != null && cooldown.get() <= 0){
            ServerWorld world2 = world.getServer().getLevel(World.OVERWORLD);
            if(world2 == null) return;

            BlockPos targetPos = new BlockPos(entity.position().x, entity.position().y, entity.position().z);

            entity.getCapability(GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY).ifPresent(h -> {
                h.setCooldown(com.freeranger.dark_caverns.core.DarkCavernsConfig.COMMON.gatewayCooldownTicks.get());
            });
            entity.changeDimension(world2, new OverworldTeleporter(targetPos));
        }
    }
}
