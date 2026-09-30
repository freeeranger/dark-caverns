package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernRouteFeature;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.sun.management.ThreadMXBean;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.function.Function;

/** Opt-in measurements, with fixture setup and terrain generation outside the timed region. */
@GameTestHolder(DarkCaverns.MOD_ID + "_profile")
@PrefixGameTestTemplate(false)
public final class CavernRouteProfileTests {
    private static final int WARMUP_PASSES = 100;
    private static final int MEASURED_PASSES = 100;

    private CavernRouteProfileTests() {}

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_profile",
            template = "sacred_torch",
            timeoutTicks = 1200)
    public static void profileRoutes(GameTestHelper helper) {
        var bean = ManagementFactory.getThreadMXBean();
        helper.assertTrue(
                bean instanceof ThreadMXBean allocation
                        && allocation.isThreadAllocatedMemorySupported(),
                "Route profiling requires a JVM with thread allocation counters");
        var allocation = (ThreadMXBean) bean;
        allocation.setThreadAllocatedMemoryEnabled(true);
        BlockState stone = CustomBlocks.CARFSTONE.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        ChunkPos chunk = new ChunkPos(-1, -1);
        measure(helper, allocation, "solid", chunk, pos -> stone, false);
        measure(helper, allocation, "flat", chunk, pos -> pos.getY() <= 64 ? stone : air, false);
        measure(
                helper,
                allocation,
                "cliff",
                chunk,
                pos -> pos.getY() <= ((pos.getX() & 15) < 8 ? 64 : 68) ? stone : air,
                false);
        measure(
                helper,
                allocation,
                "bridge",
                chunk,
                pos ->
                        ((pos.getX() & 15) < 7 || (pos.getX() & 15) > 8) && pos.getY() <= 64
                                ? stone
                                : air,
                true);

        var volume = new TerrainTestVolume(helper, 7361, "tangled_hallow");
        volume.carve();
        for (int x = 2; x <= 3; x++) {
            for (int z = 2; z <= 3; z++) {
                measure(
                        helper,
                        allocation,
                        "generated-hallow-" + x + "-" + z,
                        volume.chunks[x][z].getPos(),
                        volume::get,
                        false);
            }
        }
        helper.succeed();
    }

    private static void measure(
            GameTestHelper helper,
            ThreadMXBean allocation,
            String name,
            ChunkPos chunk,
            Function<BlockPos, BlockState> terrain,
            boolean bridges) {
        // Snapshot the fixture so counters measure route work rather than terrain construction.
        BlockState[] original = new BlockState[16 * 16 * 256];
        var mutable = new BlockPos.MutableBlockPos();
        for (int y = 0; y < 256; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    original[y * 256 + z * 16 + x] =
                            terrain.apply(
                                    mutable.set(
                                            chunk.getMinBlockX() + x, y, chunk.getMinBlockZ() + z));
                }
            }
        }
        var writes = new HashMap<BlockPos, BlockState>();
        long[] reads = new long[1];
        var world =
                TerrainTestWorld.create(
                        pos -> {
                            if ((pos.getX() >> 4) != chunk.x || (pos.getZ() >> 4) != chunk.z)
                                throw new AssertionError("Route profile read outside owning chunk");
                            reads[0]++;
                            return writes.getOrDefault(
                                    pos,
                                    original[
                                            pos.getY() * 256
                                                    + (pos.getZ() & 15) * 16
                                                    + (pos.getX() & 15)]);
                        },
                        writes::put,
                        pos -> (pos.getX() >> 4) == chunk.x && (pos.getZ() >> 4) == chunk.z,
                        helper.getLevel().getBiome(helper.absolutePos(BlockPos.ZERO)),
                        42);
        long thread = Thread.currentThread().threadId();
        long[] nanos = new long[MEASURED_PASSES];
        long[] bytes = new long[MEASURED_PASSES];
        Map<BlockPos, BlockState> expected = null;
        int changed = 0;
        for (int pass = -WARMUP_PASSES; pass < MEASURED_PASSES; pass++) {
            writes.clear();
            reads[0] = 0;
            long allocatedBefore = allocation.getThreadAllocatedBytes(thread);
            long started = System.nanoTime();
            changed = CavernRouteFeature.connect(world, chunk, bridges);
            long elapsed = System.nanoTime() - started;
            long allocated = allocation.getThreadAllocatedBytes(thread) - allocatedBefore;
            if (pass >= 0) {
                nanos[pass] = elapsed;
                bytes[pass] = allocated;
            }
            if (expected == null) expected = Map.copyOf(writes);
            helper.assertTrue(
                    expected.equals(writes), "Route profiling changed geometry between passes");
        }
        Arrays.sort(nanos);
        Arrays.sort(bytes);
        DarkCaverns.LOGGER.info(
                "Route profile {}: median={}ms, p95={}ms, allocated={}bytes/pass, reads={},"
                        + " changed={}, geometry={}",
                name,
                nanos[MEASURED_PASSES / 2] / 1_000_000.0,
                nanos[(int) (MEASURED_PASSES * .95) - 1] / 1_000_000.0,
                bytes[MEASURED_PASSES / 2],
                reads[0],
                changed,
                fingerprint(writes));
    }

    private static String fingerprint(Map<BlockPos, BlockState> writes) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            writes.entrySet().stream()
                    .sorted(Comparator.comparingLong(entry -> entry.getKey().asLong()))
                    .forEach(
                            entry ->
                                    digest.update(
                                            (entry.getKey().asLong()
                                                            + "="
                                                            + entry.getValue()
                                                            + "\n")
                                                    .getBytes(StandardCharsets.UTF_8)));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
