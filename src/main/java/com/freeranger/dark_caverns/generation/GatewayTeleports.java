package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.core.GatewayCooldowns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class GatewayTeleports {
    private static final int GATEWAY_CLEARANCE = 2;
    private static final int LANDING_RADIUS = 1;
    private static final int SAFE_POSITION_SEARCH_RADIUS = 4;
    private static final int ROOF_SEARCH_DEPTH = 6;
    private static final int BOTTOM_SEARCH_HEIGHT = 5;

    public static final ResourceKey<Level> DARK_CAVERNS =
            ResourceKey.create(Registries.DIMENSION, DarkCaverns.id(DarkCaverns.MOD_ID));

    private GatewayTeleports() {}

    @Nullable public static Entity toDarkCaverns(ServerLevel source, Entity entity) {
        ServerLevel destination = source.getServer().getLevel(DARK_CAVERNS);
        if (destination == null) {
            showFailure(entity, "message.dark_caverns.gateway.dimension_unavailable");
            return null;
        }
        if (!GatewayCooldowns.isReady(entity, source)) {
            showFailure(entity, "message.dark_caverns.gateway.recharging");
            return null;
        }

        BlockPos column = BlockPos.containing(entity.getX(), 0, entity.getZ());
        BlockPos gateway = findRoofGateway(destination, column);
        if (gateway == null) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }

        GatewayPreparation preparation =
                prepareGateway(
                        destination,
                        gateway,
                        CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get(),
                        Direction.DOWN);
        if (preparation == GatewayPreparation.FAILED) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }
        BlockPos preferredArrival = gateway.below(GATEWAY_CLEARANCE);
        if (preparation == GatewayPreparation.CREATED) {
            buildLanding(destination, preferredArrival.below());
        } else if (hasClearHeadroom(destination, preferredArrival)
                && canBuildLandingOn(destination, preferredArrival.below())) {
            // Upgrade gateways created by older versions without disturbing existing builds.
            buildLanding(destination, preferredArrival.below());
        }

        BlockPos arrival = findSafeArrival(destination, preferredArrival);
        if (arrival == null) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }

        return changeDimension(source, destination, entity, arrival);
    }

    @Nullable public static Entity toOverworld(ServerLevel source, Entity entity) {
        ServerLevel destination = source.getServer().getLevel(Level.OVERWORLD);
        if (destination == null) {
            showFailure(entity, "message.dark_caverns.gateway.dimension_unavailable");
            return null;
        }
        if (!GatewayCooldowns.isReady(entity, source)) {
            showFailure(entity, "message.dark_caverns.gateway.recharging");
            return null;
        }

        BlockPos column = BlockPos.containing(entity.getX(), 0, entity.getZ());
        BlockPos gateway = findBottomGateway(destination, column);
        if (gateway == null) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }

        GatewayPreparation preparation =
                prepareGateway(
                        destination,
                        gateway,
                        CustomBlocks.GATEWAY_TO_THE_CAVERNS.get(),
                        Direction.UP);
        if (preparation == GatewayPreparation.FAILED) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }

        BlockPos arrival = findSafeArrival(destination, gateway.above());
        if (arrival == null) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }

        return changeDimension(source, destination, entity, arrival);
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
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot(),
                moved -> {
                    moved.resetFallDistance();
                    GatewayCooldowns.start(moved, destination);
                });
    }

    private static GatewayPreparation prepareGateway(
            ServerLevel level, BlockPos gateway, Block gatewayBlock, Direction clearanceDirection) {
        if (level.getBlockState(gateway).is(gatewayBlock)) {
            return GatewayPreparation.EXISTING;
        }

        if (!level.setBlock(gateway, gatewayBlock.defaultBlockState(), Block.UPDATE_ALL)) {
            return GatewayPreparation.FAILED;
        }
        for (int distance = 1; distance <= GATEWAY_CLEARANCE; distance++) {
            level.setBlock(
                    gateway.relative(clearanceDirection, distance),
                    Blocks.AIR.defaultBlockState(),
                    Block.UPDATE_ALL);
        }
        return GatewayPreparation.CREATED;
    }

    private static void buildLanding(ServerLevel level, BlockPos center) {
        for (BlockPos pos :
                BlockPos.betweenClosed(
                        center.offset(-LANDING_RADIUS, 0, -LANDING_RADIUS),
                        center.offset(LANDING_RADIUS, 0, LANDING_RADIUS))) {
            if (canBuildLandingOn(level, pos)) {
                level.setBlock(
                        pos, CustomBlocks.CARFSTONE.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static boolean canBuildLandingOn(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || !state.getFluidState().isEmpty();
    }

    @Nullable private static BlockPos findSafeArrival(ServerLevel level, BlockPos preferred) {
        if (isSafeArrival(level, preferred)) {
            return preferred;
        }

        for (int radius = 1; radius <= SAFE_POSITION_SEARCH_RADIUS; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) != radius && Math.abs(z) != radius) {
                        continue;
                    }
                    BlockPos candidate = preferred.offset(x, 0, z);
                    if (isSafeArrival(level, candidate)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isSafeArrival(ServerLevel level, BlockPos pos) {
        if (!hasClearHeadroom(level, pos)) {
            return false;
        }

        BlockPos floor = pos.below();
        BlockState floorState = level.getBlockState(floor);
        return floorState.getFluidState().isEmpty()
                && floorState.isFaceSturdy(level, floor, Direction.UP)
                && !floorState.is(Blocks.MAGMA_BLOCK)
                && !floorState.is(Blocks.CACTUS)
                && !floorState.is(CustomBlocks.SCORCHED_BERRY_BUSH.get());
    }

    private static boolean hasClearHeadroom(ServerLevel level, BlockPos pos) {
        return isClear(level, pos) && isClear(level, pos.above());
    }

    private static boolean isClear(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getFluidState().isEmpty() && state.getCollisionShape(level, pos).isEmpty();
    }

    private static void showFailure(Entity entity, String translationKey) {
        if (entity instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable(translationKey), true);
        }
    }

    @Nullable private static BlockPos findRoofGateway(ServerLevel level, BlockPos column) {
        int roof = level.getMaxBuildHeight() - 1;
        int searchStart = roof - ROOF_SEARCH_DEPTH + 1;
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();

        for (int y = searchStart; y <= roof; y++) {
            candidate.set(column.getX(), y, column.getZ());
            if (level.getBlockState(candidate).is(CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get())) {
                return candidate.immutable();
            }
        }
        for (int y = searchStart; y <= roof; y++) {
            candidate.set(column.getX(), y, column.getZ());
            if (level.getBlockState(candidate).is(Blocks.BEDROCK)) {
                return candidate.immutable();
            }
        }
        return null;
    }

    @Nullable private static BlockPos findBottomGateway(ServerLevel level, BlockPos column) {
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
        return null;
    }

    private enum GatewayPreparation {
        EXISTING,
        CREATED,
        FAILED
    }
}
