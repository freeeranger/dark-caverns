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
    private static final int SAFE_POSITION_SEARCH_RADIUS = 4;
    private static final int BOTTOM_SEARCH_HEIGHT = 5;

    public static final ResourceKey<Level> DARK_CAVERNS =
            ResourceKey.create(Registries.DIMENSION, DarkCaverns.id(DarkCaverns.MOD_ID));

    private GatewayTeleports() {}

    public static void toDarkCaverns(ServerLevel source, Entity entity, BlockPos sourceGateway) {
        ServerLevel destination = source.getServer().getLevel(DARK_CAVERNS);
        prepareArrival(source, destination, entity, sourceGateway);
    }

    /** Arrival workflow with an explicitly resolved destination (also used by server fixtures). */
    public static void prepareArrival(
            ServerLevel source,
            @Nullable ServerLevel destination,
            Entity entity,
            BlockPos sourceGateway) {
        if (destination == null) {
            showFailure(entity, "message.dark_caverns.gateway.dimension_unavailable");
            return;
        }
        if (!GatewayCooldowns.isReady(entity, source)) {
            showFailure(entity, "message.dark_caverns.gateway.recharging");
            return;
        }

        var origin = new GatewayLinks.Origin(source.dimension(), sourceGateway);
        var link = GatewayLinks.get(source.getServer()).from(origin);
        BlockPos column = link == null ? sourceGateway : link.feet();
        GatewayChunkLoading.prepare(
                source,
                destination,
                entity,
                column,
                link == null ? CavernArrival.SEARCH_RADIUS : SAFE_POSITION_SEARCH_RADIUS,
                () ->
                        source.getBlockState(sourceGateway)
                                .is(CustomBlocks.GATEWAY_TO_THE_CAVERNS.get()),
                () -> arriveInCaverns(source, destination, entity, origin, link));
    }

    @Nullable private static Entity arriveInCaverns(
            ServerLevel source,
            ServerLevel destination,
            Entity entity,
            GatewayLinks.Origin origin,
            @Nullable GatewayLinks.Link preparedLink) {
        if (!GatewayCooldowns.isReady(entity, source)) return null;
        var links = GatewayLinks.get(source.getServer());
        // Another traveler may have established this link while our chunks were preparing.
        // Retry through prepare() so its exact destination footprint is guaranteed loaded.
        if (!java.util.Objects.equals(preparedLink, links.from(origin))) return null;
        var link = preparedLink;
        if (link == null) {
            var plan = CavernArrival.find(destination, origin.pos());
            if (plan == null || !plan.place(destination)) {
                GatewayCooldowns.start(entity, source);
                showFailure(entity, "message.dark_caverns.gateway.no_landing");
                return null;
            }
            link = new GatewayLinks.Link(origin, plan.gateway(), plan.feet(), plan.facing());
            links.put(link);
        }
        // Existing arrivals are never rebuilt: player changes belong to the player.
        BlockPos arrival =
                destination
                                .getBlockState(link.gateway())
                                .is(CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get())
                        ? findSafeArrival(destination, link.feet())
                        : null;
        if (arrival == null) {
            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
            return null;
        }

        Entity moved =
                changeDimension(source, destination, entity, arrival, link.facing().toYRot(), 0);
        if (moved != null) showFailure(moved, "message.dark_caverns.gateway.arrived");
        return moved;
    }

    public static void toOverworld(ServerLevel source, Entity entity, BlockPos sourceGateway) {
        var link =
                source.dimension().equals(DARK_CAVERNS)
                        ? GatewayLinks.get(source.getServer()).home(sourceGateway)
                        : null;
        ServerLevel destination =
                source.getServer()
                        .getLevel(link == null ? Level.OVERWORLD : link.origin().dimension());
        prepareReturn(source, destination, entity, sourceGateway, link);
    }

    /** Return workflow after resolving a saved link, or the legacy unlinked destination. */
    public static void prepareReturn(
            ServerLevel source,
            @Nullable ServerLevel destination,
            Entity entity,
            BlockPos sourceGateway,
            @Nullable GatewayLinks.Link link) {
        if (destination == null) {
            showFailure(entity, "message.dark_caverns.gateway.dimension_unavailable");
            return;
        }
        if (!GatewayCooldowns.isReady(entity, source)) {
            showFailure(entity, "message.dark_caverns.gateway.recharging");
            return;
        }

        BlockPos column = link == null ? sourceGateway : link.origin().pos();
        GatewayChunkLoading.prepare(
                source,
                destination,
                entity,
                column,
                SAFE_POSITION_SEARCH_RADIUS,
                () ->
                        source.getBlockState(sourceGateway)
                                .is(CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()),
                () -> {
                    if (link == null) {
                        // Keep old roof gateways usable without touching their platforms.
                        arriveInOverworld(source, destination, entity, column);
                    } else if (GatewayCooldowns.isReady(entity, source)) {
                        BlockPos arrival =
                                destination
                                                .getBlockState(column)
                                                .is(CustomBlocks.GATEWAY_TO_THE_CAVERNS.get())
                                        ? findSafeArrival(destination, column.above())
                                        : null;
                        if (arrival == null)
                            showFailure(entity, "message.dark_caverns.gateway.destination_blocked");
                        else changeDimension(source, destination, entity, arrival);
                    }
                });
    }

    @Nullable private static Entity arriveInOverworld(
            ServerLevel source, ServerLevel destination, Entity entity, BlockPos column) {
        if (!GatewayCooldowns.isReady(entity, source)) return null;
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
        return changeDimension(
                source, destination, entity, target, entity.getYRot(), entity.getXRot());
    }

    @Nullable private static Entity changeDimension(
            ServerLevel source,
            ServerLevel destination,
            Entity entity,
            BlockPos target,
            float yaw,
            float pitch) {
        GatewayCooldowns.start(entity, source);
        return entity.changeDimension(transition(destination, target, yaw, pitch));
    }

    private static DimensionTransition transition(
            ServerLevel destination, BlockPos target, float yaw, float pitch) {
        return new DimensionTransition(
                destination,
                Vec3.atBottomCenterOf(target),
                Vec3.ZERO,
                yaw,
                pitch,
                moved -> {
                    moved.resetFallDistance();
                    GatewayCooldowns.start(moved, destination);
                    DimensionTransition.PLACE_PORTAL_TICKET.onTransition(moved);
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
