package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.capabilities.GatewayCooldownCapability;
import com.freeranger.dark_caverns.generation.DarkCavernsTeleporter;
import com.freeranger.dark_caverns.registry.CustomDimensions;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.concurrent.atomic.AtomicInteger;

public class GatewayToTheCavernsBlock extends Block {
    public GatewayToTheCavernsBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        if (entity.getVehicle() != null || entity.isVehicle()) return;

        AtomicInteger cooldown = new AtomicInteger();
        entity.getCapability(GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY).ifPresent(h -> {
            cooldown.set(h.getCooldown());
        });

        if(world instanceof ServerWorld && world.getServer() != null && cooldown.get() <= 0){
            ServerWorld world2 = world.getServer().getLevel(CustomDimensions.DARK_CAVERNS_WORLD);
            if(world2 == null) return;

            BlockPos targetPos = new BlockPos(entity.position().x, 248, entity.position().z);

            entity.changeDimension(world2, new DarkCavernsTeleporter(targetPos));
        }
    }


}
