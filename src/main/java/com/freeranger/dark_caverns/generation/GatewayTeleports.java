package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.core.GatewayCooldowns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public final class GatewayTeleports {
    public static final ResourceKey<Level> DARK_CAVERNS = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DarkCaverns.MOD_ID, DarkCaverns.MOD_ID)
    );

    private GatewayTeleports() {
    }

    public static void toDarkCaverns(ServerLevel source, Entity entity) {
        ServerLevel destination = source.getServer().getLevel(DARK_CAVERNS);
        if (destination == null || !GatewayCooldowns.isReady(entity, source)) {
            return;
        }

        BlockPos column = BlockPos.containing(entity.getX(), 248, entity.getZ());
        BlockPos gateway = findRoofGateway(destination, column);
        destination.setBlock(gateway, CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get().defaultBlockState(), Block.UPDATE_ALL);
        destination.setBlock(gateway.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        destination.setBlock(gateway.below(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        GatewayCooldowns.start(entity, source);
        Entity moved = entity.changeDimension(transition(destination, entity, gateway.below(2)));
        if (moved != null) {
            GatewayCooldowns.start(moved, destination);
        }
    }

    public static void toOverworld(ServerLevel source, Entity entity) {
        ServerLevel destination = source.getServer().getLevel(Level.OVERWORLD);
        if (destination == null || !GatewayCooldowns.isReady(entity, source)) {
            return;
        }

        BlockPos column = BlockPos.containing(
                entity.getX(),
                destination.getMinBuildHeight() + 4,
                entity.getZ()
        );
        BlockPos gateway = findBottomGateway(destination, column);
        destination.setBlock(gateway, CustomBlocks.GATEWAY_TO_THE_CAVERNS.get().defaultBlockState(), Block.UPDATE_ALL);
        destination.setBlock(gateway.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        destination.setBlock(gateway.above(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        GatewayCooldowns.start(entity, source);
        Entity moved = entity.changeDimension(transition(destination, entity, gateway.above()));
        if (moved != null) {
            GatewayCooldowns.start(moved, destination);
        }
    }

    private static DimensionTransition transition(ServerLevel destination, Entity entity, BlockPos target) {
        return new DimensionTransition(
                destination,
                Vec3.atBottomCenterOf(target),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.DO_NOTHING
        );
    }

    private static BlockPos findRoofGateway(ServerLevel level, BlockPos column) {
        for (int y = 250; y <= 255; y++) {
            BlockPos candidate = new BlockPos(column.getX(), y, column.getZ());
            Block block = level.getBlockState(candidate).getBlock();
            if (block == Blocks.BEDROCK || block == CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()) {
                return candidate;
            }
        }
        return new BlockPos(column.getX(), 250, column.getZ());
    }

    private static BlockPos findBottomGateway(ServerLevel level, BlockPos column) {
        int bottom = level.getMinBuildHeight();
        for (int y = bottom + 5; y >= bottom; y--) {
            BlockPos candidate = new BlockPos(column.getX(), y, column.getZ());
            if (level.getBlockState(candidate).is(CustomBlocks.GATEWAY_TO_THE_CAVERNS.get())) {
                return candidate;
            }
        }
        for (int y = bottom + 5; y >= bottom; y--) {
            BlockPos candidate = new BlockPos(column.getX(), y, column.getZ());
            Block block = level.getBlockState(candidate).getBlock();
            if (block == Blocks.BEDROCK || block == CustomBlocks.CRACKED_BEDROCK.get()) {
                return candidate;
            }
        }
        return new BlockPos(column.getX(), bottom + 4, column.getZ());
    }
}
