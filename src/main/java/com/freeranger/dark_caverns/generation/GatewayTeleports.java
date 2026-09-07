package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.core.GatewayCooldowns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class GatewayTeleports {
    private static final int GATEWAY_CLEARANCE = 2;
    private static final int ROOF_SEARCH_DEPTH = 6;
    private static final int BOTTOM_SEARCH_HEIGHT = 5;
    private static final int BOTTOM_FALLBACK_HEIGHT = 4;

    public static final ResourceKey<Level> DARK_CAVERNS =
            ResourceKey.create(Registries.DIMENSION, DarkCaverns.id(DarkCaverns.MOD_ID));

    private GatewayTeleports() {}

    @Nullable public static Entity toDarkCaverns(ServerLevel source, Entity entity) {
        ServerLevel destination = source.getServer().getLevel(DARK_CAVERNS);
        if (destination == null || !GatewayCooldowns.isReady(entity, source)) {
            return null;
        }

        BlockPos column = BlockPos.containing(entity.getX(), 0, entity.getZ());
        BlockPos gateway = findRoofGateway(destination, column);
        prepareGateway(
                destination, gateway, CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get(), Direction.DOWN);

        return changeDimension(source, destination, entity, gateway.below(GATEWAY_CLEARANCE));
    }

    @Nullable public static Entity toOverworld(ServerLevel source, Entity entity) {
        ServerLevel destination = source.getServer().getLevel(Level.OVERWORLD);
        if (destination == null || !GatewayCooldowns.isReady(entity, source)) {
            return null;
        }

        BlockPos column = BlockPos.containing(entity.getX(), 0, entity.getZ());
        BlockPos gateway = findBottomGateway(destination, column);
        prepareGateway(
                destination, gateway, CustomBlocks.GATEWAY_TO_THE_CAVERNS.get(), Direction.UP);

        return changeDimension(source, destination, entity, gateway.above());
    }

    @Nullable private static Entity changeDimension(
            ServerLevel source, ServerLevel destination, Entity entity, BlockPos target) {
        GatewayCooldowns.start(entity, source);
        return entity.changeDimension(transition(destination, entity, target));
    }

    private static DimensionTransition transition(
            ServerLevel destination, Entity entity, BlockPos target) {
        return new DimensionTransition(
                destination,
                Vec3.atBottomCenterOf(target),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                moved -> GatewayCooldowns.start(moved, destination));
    }

    private static void prepareGateway(
            ServerLevel level, BlockPos gateway, Block gatewayBlock, Direction clearanceDirection) {
        level.setBlock(gateway, gatewayBlock.defaultBlockState(), Block.UPDATE_ALL);
        for (int distance = 1; distance <= GATEWAY_CLEARANCE; distance++) {
            level.setBlock(
                    gateway.relative(clearanceDirection, distance),
                    Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL);
        }
    }

    private static BlockPos findRoofGateway(ServerLevel level, BlockPos column) {
        int roof = level.getMaxBuildHeight() - 1;
        int searchStart = roof - ROOF_SEARCH_DEPTH + 1;
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int y = searchStart; y <= roof; y++) {
            candidate.set(column.getX(), y, column.getZ());
            Block block = level.getBlockState(candidate).getBlock();
            if (block == Blocks.BEDROCK || block == CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()) {
                return candidate.immutable();
            }
        }
        return new BlockPos(column.getX(), searchStart, column.getZ());
    }

    private static BlockPos findBottomGateway(ServerLevel level, BlockPos column) {
        int bottom = level.getMinBuildHeight();
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int y = bottom + BOTTOM_SEARCH_HEIGHT; y >= bottom; y--) {
            candidate.set(column.getX(), y, column.getZ());
            if (level.getBlockState(candidate).is(CustomBlocks.GATEWAY_TO_THE_CAVERNS.get())) {
                return candidate.immutable();
            }
        }
        for (int y = bottom + BOTTOM_SEARCH_HEIGHT; y >= bottom; y--) {
            candidate.set(column.getX(), y, column.getZ());
            Block block = level.getBlockState(candidate).getBlock();
            if (block == Blocks.BEDROCK || block == CustomBlocks.CRACKED_BEDROCK.get()) {
                return candidate.immutable();
            }
        }
        return new BlockPos(column.getX(), bottom + BOTTOM_FALLBACK_HEIGHT, column.getZ());
    }
}
