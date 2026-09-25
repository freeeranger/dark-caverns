package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernRouteFeature;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CavernRouteTests {
    private static final BlockState STONE = CustomBlocks.CARFSTONE.get().defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private CavernRouteTests() {}

    @GameTest(template = "sacred_torch", timeoutTicks = 100)
    public static void routesMakeCliffsWalkableWithoutCrossChunkWrites(GameTestHelper helper) {
        var first = new HashMap<BlockPos, BlockState>();
        var second = new HashMap<BlockPos, BlockState>();
        var reads = new AtomicInteger();
        var chunk = new ChunkPos(-1, -1);
        Function<BlockPos, BlockState> terrain =
                pos -> pos.getY() <= ((pos.getX() & 15) < 8 ? 64 : 68) ? STONE : AIR;
        var world = world(helper, chunk, terrain, first, reads);
        BlockPos start = new BlockPos(-13, 65, -8), end = new BlockPos(-4, 69, -8);
        helper.assertTrue(!connected(world, start, end, chunk), "Cliff fixture already walkable");
        reads.set(0);
        int changed = CavernRouteFeature.connect(world, chunk, false);
        helper.assertTrue(
                changed > 0 && changed <= 192, "Failed to build a small ramp through the cliff");
        helper.assertTrue(reads.get() <= 62000, "Route pass exceeded bounded block reads");
        helper.assertTrue(
                connected(world, start, end, chunk), "Ramp did not connect its walking areas");
        CavernRouteFeature.connect(
                world(helper, chunk, terrain, second, new AtomicInteger()), chunk, false);
        helper.assertTrue(first.equals(second), "Route geometry was not deterministic");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 100)
    public static void routesRespectProtectedWallsAndKeepLargeRifts(GameTestHelper helper) {
        var chunk = new ChunkPos(0, 0);
        for (BlockState protectedState :
                new BlockState[] {
                    Blocks.WATER.defaultBlockState(),
                    Blocks.LAVA.defaultBlockState(),
                    Blocks.CHEST.defaultBlockState(),
                    CustomBlocks.LUMINITE_ORE.get().defaultBlockState(),
                    CustomBlocks.TWISTWOOD_LOG.get().defaultBlockState(),
                    Blocks.BEDROCK.defaultBlockState()
                }) {
            var writes = new HashMap<BlockPos, BlockState>();
            var world =
                    world(
                            helper,
                            chunk,
                            pos ->
                                    pos.getX() == 7
                                            ? protectedState
                                            : pos.getY() <= (pos.getX() < 7 ? 64 : 68)
                                                    ? STONE
                                                    : AIR,
                            writes,
                            new AtomicInteger());
            helper.assertTrue(
                    CavernRouteFeature.connect(world, chunk, true) == 0 && writes.isEmpty(),
                    "Route cut a protected wall");
        }
        var writes = new HashMap<BlockPos, BlockState>();
        var rift =
                world(
                        helper,
                        chunk,
                        pos ->
                                (pos.getX() < 5 || pos.getX() > 10) && pos.getY() <= 64
                                        ? STONE
                                        : AIR,
                        writes,
                        new AtomicInteger());
        helper.assertTrue(
                CavernRouteFeature.connect(rift, chunk, true) == 0 && writes.isEmpty(),
                "Route bridged a major rift");
        var gap =
                world(
                        helper,
                        chunk,
                        pos -> (pos.getX() < 7 || pos.getX() > 8) && pos.getY() <= 64 ? STONE : AIR,
                        writes,
                        new AtomicInteger());
        helper.assertTrue(
                CavernRouteFeature.connect(gap, chunk, false) == 0,
                "Unsupported bridge appeared without its rarity gate");
        helper.assertTrue(
                CavernRouteFeature.connect(gap, chunk, true) > 0,
                "Short natural bridge was never constructed");
        helper.assertTrue(
                connected(gap, new BlockPos(3, 65, 8), new BlockPos(12, 65, 8), chunk),
                "Bridge did not connect its banks");
        helper.succeed();
    }

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_slow",
            template = "sacred_torch",
            timeoutTicks = 1200)
    public static void routesImproveDecoratedWalkingConnectivity(GameTestHelper helper)
            throws IOException {
        long[] seeds = {0, 8675309, -7046029254386353131L, 7361};
        String[] biomes = {
            "rocky_caverns", "molten_depths", "glimmershroom_forest", "tangled_hallow"
        };
        var report =
                new StringBuilder(
                        "seed,biome,connections,changed,walking_before,walking_after,largest_before,largest_after,height_before,height_after,milliseconds\n");
        long totalBefore = 0, totalAfter = 0;
        for (int sample = 0; sample < seeds.length; sample++) {
            var volume = new TerrainTestVolume(helper, seeds[sample], biomes[sample]);
            volume.carve();
            var features = volume.biome.value().getGenerationSettings().features();
            for (int step = 0; step < features.size(); step++) {
                var list = features.get(step).stream().toList();
                for (int index = 0; index < list.size(); index++) {
                    String name = list.get(index).unwrapKey().orElseThrow().location().getPath();
                    if (!name.equals("cavern_routes"))
                        volume.feature(name, GenerationStep.Decoration.values()[step], index);
                }
            }
            var before = TerrainTopology.measure(volume.snapshot());
            long start = System.nanoTime();
            var forward = runRegisteredRoutes(helper, volume, false);
            double ms = (System.nanoTime() - start) / 1_000_000.0;
            var reverse = runRegisteredRoutes(helper, volume, true);
            helper.assertTrue(
                    forward.equals(reverse), "Route geometry depends on chunk processing order");
            int connections = forward.connections(), changed = forward.blocks().size();
            for (var entry : forward.blocks().entrySet()) {
                var old = volume.get(entry.getKey());
                helper.assertTrue(
                        old.isAir()
                                || old.is(CustomBlocks.CARFSTONE.get())
                                || old.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                                || old.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                                || old.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                                || old.is(CustomBlocks.OVERGROWN_CARFSTONE.get())
                                || old.is(Blocks.MUD),
                        "Route overwrote protected decoration, ore, or fluid: " + old);
                volume.set(entry.getKey(), entry.getValue());
            }
            var after = TerrainTopology.measure(volume.snapshot());
            totalBefore += before.largestWalk;
            totalAfter += after.largestWalk;
            DarkCaverns.LOGGER.info(
                    "Traversal {}: connections={}, changed={}, largestWalk={}->{}, walkable={}->{},"
                            + " height={}->{}, {}ms/64 chunks",
                    biomes[sample],
                    connections,
                    changed,
                    before.largestWalk,
                    after.largestWalk,
                    before.walkable,
                    after.walkable,
                    before.routeHeight,
                    after.routeHeight,
                    ms);
            report.append(seeds[sample])
                    .append(',')
                    .append(biomes[sample])
                    .append(',')
                    .append(connections)
                    .append(',')
                    .append(changed)
                    .append(',')
                    .append(before.walkable)
                    .append(',')
                    .append(after.walkable)
                    .append(',')
                    .append(before.largestWalk)
                    .append(',')
                    .append(after.largestWalk)
                    .append(',')
                    .append(before.routeHeight)
                    .append(',')
                    .append(after.routeHeight)
                    .append(',')
                    .append(ms)
                    .append('\n');
            helper.assertTrue(
                    after.walkable >= before.walkable * .97,
                    "Route work removed too many walking positions");
            helper.assertTrue(
                    after.largestWalk >= before.largestWalk * .99,
                    "Route work severed an important walking network");
            helper.assertTrue(
                    after.largestAir >= before.largestAir * .99,
                    "Routes fragmented a major cavern");
            helper.assertTrue(
                    after.floatingStone <= before.floatingStone + 128,
                    "Route excavation detached too much terrain");
            helper.assertTrue(
                    changed < 128 * 128 * 256 * .004,
                    "Route work changed the cavern silhouette excessively");
        }
        Path directory = Path.of("../build/reports/terrain");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("traversal.csv"), report);
        helper.assertTrue(
                totalAfter > totalBefore * 1.05,
                "Route pass did not meaningfully improve walking connectivity");
        helper.succeed();
    }

    private record Routes(Map<BlockPos, BlockState> blocks, int connections) {}

    private static Routes runRegisteredRoutes(
            GameTestHelper helper, TerrainTestVolume volume, boolean reverse) {
        var feature =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.PLACED_FEATURE)
                        .getOrThrow(
                                ResourceKey.create(
                                        Registries.PLACED_FEATURE,
                                        DarkCaverns.id("cavern_routes")));
        var lastStep =
                volume.biome
                        .value()
                        .getGenerationSettings()
                        .features()
                        .get(GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal());
        helper.assertTrue(
                lastStep.size() == 1 && lastStep.get(0).value() == feature,
                "Routes must run last, after hazards and decoration, in each cavern biome");
        var writes = new HashMap<BlockPos, BlockState>();
        int connections = 0;
        for (int i = 0; i < 64; i++) {
            int index = reverse ? 63 - i : i;
            var chunk = volume.chunks[index / 8][index % 8].getPos();
            var world =
                    TerrainTestWorld.create(
                            pos -> {
                                if (!new ChunkPos(pos).equals(chunk))
                                    throw new AssertionError("Route read outside its owning chunk");
                                return writes.containsKey(pos) ? writes.get(pos) : volume.get(pos);
                            },
                            writes::put,
                            pos -> new ChunkPos(pos).equals(chunk),
                            volume.biome,
                            volume.seed);
            var random = new WorldgenRandom(new LegacyRandomSource(0));
            long decorationSeed =
                    random.setDecorationSeed(
                            volume.seed, chunk.getMinBlockX(), chunk.getMinBlockZ());
            random.setFeatureSeed(
                    decorationSeed, 0, GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal());
            if (feature.placeWithBiomeCheck(
                    world, volume.generator, random, chunk.getWorldPosition())) connections++;
        }
        return new Routes(writes, connections);
    }

    private static WorldGenLevel world(
            GameTestHelper helper,
            ChunkPos chunk,
            Function<BlockPos, BlockState> read,
            Map<BlockPos, BlockState> writes,
            AtomicInteger reads) {
        return TerrainTestWorld.create(
                pos -> {
                    if (!new ChunkPos(pos).equals(chunk))
                        throw new AssertionError("Cross-chunk route read: " + pos);
                    reads.incrementAndGet();
                    return writes.containsKey(pos) ? writes.get(pos) : read.apply(pos);
                },
                writes::put,
                pos -> new ChunkPos(pos).equals(chunk),
                helper.getLevel().getBiome(helper.absolutePos(BlockPos.ZERO)),
                42L);
    }

    private static boolean connected(
            WorldGenLevel world, BlockPos start, BlockPos end, ChunkPos chunk) {
        var queue = new ArrayDeque<BlockPos>();
        var seen = new HashSet<BlockPos>();
        queue.add(start);
        seen.add(start);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();
            if (pos.equals(end)) return true;
            for (Direction direction : Direction.Plane.HORIZONTAL)
                for (int dy = -1; dy <= 1; dy++) {
                    BlockPos next = pos.relative(direction).above(dy);
                    if (!new ChunkPos(next).equals(chunk)
                            || next.getY() < 24
                            || next.getY() > 100
                            || seen.contains(next)) continue;
                    if (!world.getBlockState(next).isAir()
                            || !world.getBlockState(next.above()).isAir()
                            || world.getBlockState(next.below()).isAir()) continue;
                    if (dy > 0 && !world.getBlockState(pos.above(2)).isAir()
                            || dy < 0 && !world.getBlockState(next.above(2)).isAir()) continue;
                    seen.add(next);
                    queue.add(next);
                }
        }
        return false;
    }
}
