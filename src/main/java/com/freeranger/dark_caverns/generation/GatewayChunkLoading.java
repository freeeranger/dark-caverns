package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Server-thread-only, bounded gateway preparation. Never joins a chunk future. */
public final class GatewayChunkLoading {
    private static final int TIMEOUT_TICKS = 1200;
    private static final int MAX_PENDING = 64;
    private static final TicketType<UUID> TICKET =
            TicketType.create("dark_caverns_gateway", UUID::compareTo, TIMEOUT_TICKS + 20);
    private static final Map<UUID, Pending> PENDING = new HashMap<>();

    private GatewayChunkLoading() {}

    public static void register(IEventBus bus) {
        bus.addListener(GatewayChunkLoading::onTick);
        bus.addListener(GatewayChunkLoading::onStopping);
    }

    public enum Result {
        READY,
        QUEUED,
        PENDING,
        BUSY,
        CANCELLED
    }

    public static Result prepare(
            ServerLevel source,
            ServerLevel destination,
            Entity entity,
            BlockPos column,
            int searchRadius,
            Runnable whenReady) {
        return prepare(source, destination, entity, column, searchRadius, () -> true, whenReady);
    }

    public static Result prepare(
            ServerLevel source,
            ServerLevel destination,
            Entity entity,
            BlockPos column,
            int searchRadius,
            BooleanSupplier sourceValid,
            Runnable whenReady) {
        if (!source.getServer().isSameThread())
            throw new IllegalStateException("Gateway request off server thread");
        if (PENDING.containsKey(entity.getUUID())) return Result.PENDING;
        List<ChunkPos> chunks = landingChunks(column, searchRadius);
        var pending =
                new Pending(
                        source,
                        destination,
                        entity,
                        entity.position(),
                        chunks,
                        source.getGameTime() + TIMEOUT_TICKS,
                        sourceValid,
                        whenReady);
        if (!pending.valid()) return Result.CANCELLED;
        if (ready(destination, chunks)) {
            whenReady.run();
            return Result.READY;
        }
        if (PENDING.size() >= MAX_PENDING) {
            message(entity, "message.dark_caverns.gateway.busy");
            return Result.BUSY;
        }
        PENDING.put(entity.getUUID(), pending);
        for (var chunk : chunks)
            destination.getChunkSource().addRegionTicket(TICKET, chunk, 0, entity.getUUID());
        message(entity, "message.dark_caverns.gateway.preparing");
        return Result.QUEUED;
    }

    public static List<ChunkPos> landingChunks(BlockPos column, int radius) {
        if (radius < 0 || radius > 16)
            throw new IllegalArgumentException("Unbounded gateway search");
        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = Math.floorDiv(column.getX() - radius, 16);
                x <= Math.floorDiv(column.getX() + radius, 16);
                x++) {
            for (int z = Math.floorDiv(column.getZ() - radius, 16);
                    z <= Math.floorDiv(column.getZ() + radius, 16);
                    z++) chunks.add(new ChunkPos(x, z));
        }
        return List.copyOf(chunks);
    }

    private static boolean ready(ServerLevel level, List<ChunkPos> chunks) {
        for (var chunk : chunks) {
            // hasChunk / getChunkFuture are not readiness checks: they may report tickets,
            // or even synchronously wait. getChunkNow never starts generation or waits.
            if (level.getChunkSource().getChunkNow(chunk.x, chunk.z) == null) return false;
        }
        return true;
    }

    public static int pendingCount(MinecraftServer server) {
        return (int) PENDING.values().stream().filter(p -> p.source.getServer() == server).count();
    }

    public static void cancel(Entity entity) {
        Pending pending = PENDING.remove(entity.getUUID());
        if (pending != null) pending.release();
    }

    public static boolean isPending(Entity entity) {
        return PENDING.containsKey(entity.getUUID());
    }

    private static void onTick(ServerTickEvent.Post event) {
        // Snapshot permits teleport hooks to enqueue/cancel another request safely.
        for (var pending : List.copyOf(PENDING.values())) {
            if (pending.source.getServer() != event.getServer()
                    || PENDING.get(pending.entity.getUUID()) != pending) continue;
            boolean expired = pending.source.getGameTime() >= pending.deadline;
            if (!pending.valid() || expired) {
                cancel(pending.entity);
                if (expired) message(pending.entity, "message.dark_caverns.gateway.timeout");
            } else if (ready(pending.destination, pending.chunks)) {
                PENDING.remove(pending.entity.getUUID());
                try {
                    pending.whenReady.run();
                } catch (RuntimeException exception) {
                    DarkCaverns.LOGGER.error("Could not complete gateway travel", exception);
                    message(pending.entity, "message.dark_caverns.gateway.destination_blocked");
                } finally {
                    pending.release();
                }
            }
        }
    }

    private static void onStopping(ServerStoppingEvent event) {
        for (var pending : List.copyOf(PENDING.values())) {
            if (pending.source.getServer() == event.getServer()) cancel(pending.entity);
        }
    }

    private static void message(Entity entity, String key) {
        if (entity instanceof ServerPlayer player)
            player.displayClientMessage(Component.translatable(key), true);
    }

    private record Pending(
            ServerLevel source,
            ServerLevel destination,
            Entity entity,
            Vec3 origin,
            List<ChunkPos> chunks,
            long deadline,
            BooleanSupplier sourceValid,
            Runnable whenReady) {
        boolean valid() {
            return entity.isAlive()
                    && !entity.isRemoved()
                    && entity.level() == source
                    && entity.getVehicle() == null
                    && !entity.isVehicle()
                    && entity.position().distanceToSqr(origin) <= 4
                    && sourceValid.getAsBoolean();
        }

        void release() {
            for (var chunk : chunks)
                destination.getChunkSource().removeRegionTicket(TICKET, chunk, 0, entity.getUUID());
        }
    }
}
