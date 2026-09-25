package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernArrival;
import com.freeranger.dark_caverns.generation.GatewayChunkLoading;
import com.freeranger.dark_caverns.generation.GatewayLinks;
import com.freeranger.dark_caverns.generation.GatewayTeleports;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CavernArrivalTests {
    private static final BlockState STONE = CustomBlocks.CARFSTONE.get().defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private CavernArrivalTests() {}

    @GameTest(template = "sacred_torch", timeoutTicks = 600)
    public static void linkedTravelReturnsHomeAndPreservesRepeatArrival(GameTestHelper helper) {
        var source = helper.getLevel();
        // Vanilla's GameTest flat preset omits custom dimensions. Exercise the same resolved
        // travel workflow between actual ServerLevels, with a controlled cave floor in the Nether.
        var destination = source.getServer().getLevel(Level.NETHER);
        helper.assertTrue(destination != null, "Missing travel fixture dimension");
        var sourceGateway = helper.absolutePos(new BlockPos(1, 10, 1));
        source.setBlock(
                sourceGateway,
                CustomBlocks.GATEWAY_TO_THE_CAVERNS.get().defaultBlockState(),
                Block.UPDATE_ALL);
        source.setBlock(sourceGateway.above(), AIR, Block.UPDATE_ALL);
        source.setBlock(sourceGateway.above(2), AIR, Block.UPDATE_ALL);
        var traveler = helper.spawn(EntityType.PIG, new BlockPos(1, 11, 1));
        traveler.setNoAi(true);
        traveler.setNoGravity(true);
        var id = traveler.getUUID();
        var origin = new GatewayLinks.Origin(source.dimension(), sourceGateway);
        var saved = new AtomicReference<GatewayLinks.Link>();
        var roof =
                new BlockPos(
                        sourceGateway.getX(),
                        destination.getMaxBuildHeight() - 3,
                        sourceGateway.getZ() + 14);
        // Warm only this test fixture explicitly. GameTest advances ticks as fast as possible,
        // which can expire normal travel timeouts during cold Nether generation. Non-blocking
        // cold preparation and cancellation have dedicated GatewayLoadingTests.
        for (var chunk :
                GatewayChunkLoading.landingChunks(sourceGateway, CavernArrival.SEARCH_RADIUS)) {
            destination.getChunk(chunk.x, chunk.z);
        }
        // Controlled cave-floor fixture in the disposable GameTest dimension. Real
        // terrain
        // acceptance is measured separately, so random test-world coordinates cannot
        // flake.
        var floor = new BlockPos(sourceGateway.getX(), 128, sourceGateway.getZ());
        for (BlockPos pos :
                BlockPos.betweenClosed(
                        floor.offset(-10, -1, -10),
                        new BlockPos(
                                floor.getX() + 10,
                                destination.getMaxBuildHeight() - 1,
                                floor.getZ() + 10))) {
            destination.setBlock(
                    pos,
                    pos.getY() >= destination.getMaxBuildHeight() - 5
                            ? Blocks.BEDROCK.defaultBlockState()
                            : pos.getY() <= 128 ? STONE : AIR,
                    Block.UPDATE_CLIENTS);
        }
        destination.setBlock(
                roof,
                CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get().defaultBlockState(),
                Block.UPDATE_ALL);
        GatewayTeleports.prepareArrival(source, destination, traveler, sourceGateway);
        helper.runAfterDelay(
                590,
                () -> {
                    GatewayChunkLoading.cancel(traveler);
                    if (source.getEntity(id) != null) source.getEntity(id).discard();
                    if (destination.getEntity(id) != null) destination.getEntity(id).discard();
                });
        helper.startSequence()
                .thenWaitUntil(
                        () ->
                                helper.assertTrue(
                                        destination.getEntity(id) != null,
                                        "Traveler did not reach cave landing"))
                .thenExecute(
                        () -> {
                            var moved = destination.getEntity(id);
                            var link = GatewayLinks.get(source.getServer()).from(origin);
                            saved.set(link);
                            helper.assertTrue(
                                    link != null && moved.blockPosition().equals(link.feet()),
                                    "Traveler missed saved arrival feet");
                            helper.assertTrue(
                                    Math.abs(moved.getYRot() - link.facing().toYRot()) < 0.01,
                                    "Arrival did not face the walk-out");
                            helper.assertTrue(
                                    link.gateway().getY() == destination.getMaxBuildHeight() - 1,
                                    "Linked arrival did not reach the real roof");
                            BlockPos well = link.gateway().relative(link.facing(), 3);
                            for (int y = 129; y < destination.getMaxBuildHeight() - 1; y++) {
                                helper.assertTrue(
                                        destination
                                                .getBlockState(
                                                        new BlockPos(well.getX(), y, well.getZ()))
                                                .is(Blocks.LADDER),
                                        "Neighbour updates removed a ladder rung at Y=" + y);
                            }
                            helper.assertTrue(
                                    destination
                                            .getBlockState(roof)
                                            .is(CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()),
                                    "Old roof gateway was changed");
                            destination.setBlock(
                                    link.feet().below(),
                                    Blocks.GOLD_BLOCK.defaultBlockState(),
                                    Block.UPDATE_ALL);
                            moved.removeData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL);
                            GatewayTeleports.prepareReturn(
                                    destination, source, moved, link.gateway(), link);
                        })
                .thenWaitUntil(
                        () ->
                                helper.assertTrue(
                                        source.getEntity(id) != null,
                                        "Return gateway did not lead back to source dimension"))
                .thenExecute(
                        () -> {
                            var returned = source.getEntity(id);
                            helper.assertTrue(
                                    returned.blockPosition().equals(sourceGateway.above()),
                                    "Return did not use original gateway coordinates");
                            returned.removeData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL);
                            GatewayTeleports.prepareArrival(
                                    source, destination, returned, sourceGateway);
                        })
                .thenWaitUntil(
                        () ->
                                helper.assertTrue(
                                        destination.getEntity(id) != null,
                                        "Repeat visit did not reuse linked entrance"))
                .thenExecute(
                        () -> {
                            var link = saved.get();
                            var moved = destination.getEntity(id);
                            helper.assertTrue(
                                    link.equals(GatewayLinks.get(source.getServer()).from(origin)),
                                    "Repeat visit created a different link");
                            helper.assertTrue(
                                    destination
                                            .getBlockState(link.feet().below())
                                            .is(Blocks.GOLD_BLOCK),
                                    "Repeat visit rebuilt over player changes");
                            source.setBlock(sourceGateway, AIR, Block.UPDATE_ALL);
                            moved.removeData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL);
                            GatewayTeleports.prepareReturn(
                                    destination, source, moved, link.gateway(), link);
                            helper.assertTrue(
                                    destination.getEntity(id) == moved
                                            && source.getBlockState(sourceGateway).isAir(),
                                    "Broken linked gateway was recreated or traveler was sent to an"
                                            + " unrelated portal");
                            moved.discard();
                        })
                .thenSucceed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 40)
    public static void arrivalHasLitWalkOutAndReachableReturn(GameTestHelper helper) {
        var writes = new HashMap<BlockPos, BlockState>();
        var column = new BlockPos(-16, 0, 16);
        var world =
                world(
                        helper,
                        column,
                        pos -> writes.getOrDefault(pos, flatCave(pos)),
                        writes,
                        new AtomicInteger());
        var plan = CavernArrival.find(world, column);
        helper.assertTrue(plan != null, "Flat cave floor did not yield an arrival");
        helper.assertTrue(writes.isEmpty(), "Searching modified the world");
        helper.assertTrue(
                plan.equals(CavernArrival.find(world, column)),
                "Arrival search was not deterministic");
        helper.assertTrue(plan.place(world), "Preflighted arrival failed placement");
        helper.assertTrue(
                writes.size() <= CavernArrival.MAX_PLACED_BLOCKS,
                "Arrival exceeded its bounded footprint");
        helper.assertTrue(
                plan.gateway().getY() == world.getMaxBuildHeight() - 1
                        && world.getBlockState(plan.gateway().relative(plan.facing()))
                                .is(Blocks.BEDROCK),
                "Portal is not part of the actual bedrock ceiling");
        helper.assertTrue(
                world.getBlockState(plan.gateway()).is(CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()),
                "Missing return gateway");
        helper.assertTrue(
                world.getBlockState(plan.gateway().below()).isAir()
                        && world.getBlockState(plan.gateway().below(2)).isAir(),
                "Return gateway cannot be jumped into");
        helper.assertTrue(
                world.getBlockState(plan.gateway().below(3))
                        .is(CustomBlocks.SMOOTH_CARFSTONE.get()),
                "Return gateway has no standing floor");
        for (int forward = 0; forward <= 5; forward++) {
            BlockPos feet = plan.exit().relative(plan.facing(), forward);
            helper.assertTrue(
                    world.getBlockState(feet).isAir()
                            && world.getBlockState(feet.above()).isAir()
                            && !world.getBlockState(feet.below()).isAir(),
                    "Walk-out route is blocked or ends in a drop");
        }
        BlockPos shaft = plan.gateway().relative(plan.facing(), -3);
        for (int y = plan.exit().getY(); y < world.getMaxBuildHeight() - 1; y++) {
            BlockPos rung = new BlockPos(shaft.getX(), y, shaft.getZ());
            helper.assertTrue(
                    world.getBlockState(rung).is(Blocks.LADDER)
                            && world.getBlockState(rung).canSurvive(world, rung),
                    "Ladder shaft has a missing or unsupported rung at " + rung);
        }
        for (int y : new int[] {plan.exit().getY(), plan.feet().getY()}) {
            BlockPos doorway = new BlockPos(shaft.getX(), y, shaft.getZ()).relative(plan.facing());
            helper.assertTrue(
                    world.getBlockState(doorway).isAir()
                            && world.getBlockState(doorway.above()).isAir(),
                    "Ladder exit is blocked");
        }
        long lights =
                writes.values().stream()
                        .filter(state -> state.is(CustomBlocks.LUMINITE_LANTERN.get()))
                        .count();
        helper.assertTrue(lights == 2, "Landing must have two lanterns");
        writes.forEach(
                (pos, state) -> {
                    if (state.is(CustomBlocks.LUMINITE_LANTERN.get()))
                        helper.assertTrue(state.canSurvive(world, pos), "Lantern lacks support");
                });
        helper.assertTrue(
                GatewayChunkLoading.landingChunks(column, CavernArrival.SEARCH_RADIUS).size() == 9,
                "First arrival must fit in nine prepared chunks");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 40)
    public static void arrivalRefusesUnsafeSitesAndStaleExcavation(GameTestHelper helper) {
        for (BlockState obstruction :
                new BlockState[] {
                    Blocks.CHEST.defaultBlockState(),
                    Blocks.DIAMOND_ORE.defaultBlockState(),
                    Blocks.WATER.defaultBlockState(),
                    Blocks.LAVA.defaultBlockState()
                }) {
            var untouched = new HashMap<BlockPos, BlockState>();
            var blockedShaft =
                    world(
                            helper,
                            BlockPos.ZERO,
                            pos -> pos.getY() == 190 ? obstruction : flatCave(pos),
                            untouched,
                            new AtomicInteger());
            helper.assertTrue(
                    CavernArrival.find(blockedShaft, BlockPos.ZERO) == null && untouched.isEmpty(),
                    "Shaft excavated a protected or flooded layer");
        }
        var noRoofWrites = new HashMap<BlockPos, BlockState>();
        helper.assertTrue(
                CavernArrival.find(
                                        world(
                                                helper,
                                                BlockPos.ZERO,
                                                pos -> pos.getY() <= 128 ? STONE : AIR,
                                                noRoofWrites,
                                                new AtomicInteger()),
                                        BlockPos.ZERO)
                                == null
                        && noRoofWrites.isEmpty(),
                "A floating portal was created without actual bedrock overhead");
        for (BlockState protectedFloor :
                new BlockState[] {
                    Blocks.DIAMOND_ORE.defaultBlockState(),
                    Blocks.BEDROCK.defaultBlockState(),
                    Blocks.CHEST.defaultBlockState()
                }) {
            var writes = new HashMap<BlockPos, BlockState>();
            var world =
                    world(
                            helper,
                            BlockPos.ZERO,
                            pos -> pos.getY() <= 128 ? protectedFloor : AIR,
                            writes,
                            new AtomicInteger());
            helper.assertTrue(
                    CavernArrival.find(world, BlockPos.ZERO) == null && writes.isEmpty(),
                    "Protected floor was overwritten");
        }
        for (BlockState fluid :
                new BlockState[] {
                    Blocks.WATER.defaultBlockState(), Blocks.LAVA.defaultBlockState()
                }) {
            var writes = new HashMap<BlockPos, BlockState>();
            var world =
                    world(
                            helper,
                            BlockPos.ZERO,
                            pos -> pos.getY() <= 128 ? STONE : pos.getY() == 129 ? fluid : AIR,
                            writes,
                            new AtomicInteger());
            helper.assertTrue(
                    CavernArrival.find(world, BlockPos.ZERO) == null && writes.isEmpty(),
                    "Flooded landing was accepted");
        }
        var writes = new HashMap<BlockPos, BlockState>();
        var world =
                world(
                        helper,
                        BlockPos.ZERO,
                        pos -> writes.getOrDefault(pos, flatCave(pos)),
                        writes,
                        new AtomicInteger());
        var plan = CavernArrival.find(world, BlockPos.ZERO);
        writes.put(plan.feet(), Blocks.CHEST.defaultBlockState());
        helper.assertTrue(
                !plan.place(world)
                        && writes.size() == 1
                        && writes.get(plan.feet()).is(Blocks.CHEST),
                "Stale plan partially excavated a player build");
        writes.clear();
        var shaft =
                world(
                        helper,
                        BlockPos.ZERO,
                        pos ->
                                pos.getX() == 0 && pos.getZ() == 0 && pos.getY() <= 128
                                        ? STONE
                                        : AIR,
                        writes,
                        new AtomicInteger());
        helper.assertTrue(
                CavernArrival.find(shaft, BlockPos.ZERO) == null && writes.isEmpty(),
                "Arrival bridged a deep shaft");
        // A lake separated by one natural wall must not be opened by excavation.
        var fixture = new HashMap<BlockPos, BlockState>();
        var pocketWorld =
                world(
                        helper,
                        BlockPos.ZERO,
                        pos -> fixture.getOrDefault(pos, flatCave(pos)),
                        writes,
                        new AtomicInteger());
        var original = CavernArrival.find(pocketWorld, BlockPos.ZERO);
        BlockPos barrier =
                original.exit()
                        .relative(original.facing(), -2)
                        .relative(original.facing())
                        .relative(original.facing().getClockWise(), 2);
        BlockPos water = barrier.relative(original.facing().getClockWise());
        fixture.put(barrier, STONE);
        fixture.put(water, Blocks.WATER.defaultBlockState());
        var protectedPlan = CavernArrival.find(pocketWorld, BlockPos.ZERO);
        helper.assertTrue(
                protectedPlan != null && protectedPlan.place(pocketWorld),
                "Failed to find alternative to a fluid pocket");
        helper.assertTrue(
                !pocketWorld.getBlockState(barrier).isAir()
                        && pocketWorld.getBlockState(water).is(Blocks.WATER),
                "Arrival opened or drained an adjacent fluid pocket");
        writes.clear();
        fixture.clear();
        BlockPos wallOre = original.gateway().relative(original.facing(), -4).atY(180);
        fixture.put(wallOre, Blocks.DIAMOND_ORE.defaultBlockState());
        var orePlan = CavernArrival.find(pocketWorld, BlockPos.ZERO);
        helper.assertTrue(
                orePlan != null
                        && orePlan.place(pocketWorld)
                        && pocketWorld.getBlockState(wallOre).is(Blocks.DIAMOND_ORE),
                "Shaft wall ore was unnecessarily destroyed");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 40)
    public static void gatewayLinksPersistBothDirectionsAndReplaceCleanly(GameTestHelper helper) {
        var links = new GatewayLinks();
        var origin = new GatewayLinks.Origin(Level.NETHER, new BlockPos(-125, 5, 73));
        var first =
                new GatewayLinks.Link(
                        origin,
                        new BlockPos(-120, 131, 70),
                        new BlockPos(-120, 129, 72),
                        Direction.SOUTH);
        links.put(first);
        var loaded =
                GatewayLinks.load(
                        links.save(new CompoundTag(), helper.getLevel().registryAccess()),
                        helper.getLevel().registryAccess());
        helper.assertTrue(
                first.equals(loaded.from(origin)) && first.equals(loaded.home(first.gateway())),
                "Saved link lost its source dimension, offsets, facing, or reverse lookup");
        var replacement =
                new GatewayLinks.Link(
                        origin,
                        first.gateway().offset(5, 0, 0),
                        first.feet().offset(5, 0, 0),
                        Direction.EAST);
        loaded.put(replacement);
        helper.assertTrue(
                loaded.home(first.gateway()) == null && loaded.from(origin).equals(replacement),
                "Relink left a stale reverse entry");
        var other = new GatewayLinks.Origin(Level.OVERWORLD, origin.pos());
        loaded.put(
                new GatewayLinks.Link(
                        other, replacement.gateway(), replacement.feet(), Direction.NORTH));
        helper.assertTrue(
                loaded.from(origin) == null
                        && loaded.home(replacement.gateway()).origin().equals(other),
                "Conflicting endpoint retained two owners");
        helper.succeed();
    }

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_slow",
            template = "sacred_torch",
            timeoutTicks = 1200)
    public static void arrivalsFindGroundAcrossGeneratedBiomes(GameTestHelper helper) {
        String[] biomes = {
            "molten_depths", "rocky_caverns", "glimmershroom_forest", "tangled_hallow"
        };
        for (int biome = 0; biome < biomes.length; biome++) {
            var volume = new TerrainTestVolume(helper, 7361L + biome * 31L, biomes[biome]);
            volume.carve();
            var features = volume.biome.value().getGenerationSettings().features();
            for (int step = 0; step < features.size(); step++) {
                var list = features.get(step).stream().toList();
                for (int index = 0; index < list.size(); index++) {
                    volume.feature(
                            list.get(index).unwrapKey().orElseThrow().location().getPath(),
                            GenerationStep.Decoration.values()[step],
                            index);
                }
            }
            int found = 0;
            int maxReads = 0;
            long started = System.nanoTime();
            for (int x = -24; x <= 24; x += 24) {
                for (int z = -24; z <= 24; z += 24) {
                    var column = new BlockPos(x, 0, z);
                    var reads = new AtomicInteger();
                    var writes = new HashMap<BlockPos, BlockState>();
                    var world = world(helper, column, volume::get, writes, reads);
                    var plan = CavernArrival.find(world, column);
                    if (plan != null) {
                        found++;
                        helper.assertTrue(
                                plan.place(world), "Generated landing failed its preflight");
                        helper.assertTrue(
                                writes.size() <= CavernArrival.MAX_PLACED_BLOCKS,
                                "Generated arrival exceeded its footprint");
                    }
                    maxReads = Math.max(maxReads, reads.get());
                }
            }
            DarkCaverns.LOGGER.info(
                    "Arrival sample {}: {}/9 safe sites, max {} reads, {}ms search+placement total",
                    biomes[biome],
                    found,
                    maxReads,
                    (System.nanoTime() - started) / 1_000_000.0);
            helper.assertTrue(
                    found >= 7, "Too few safe arrivals in " + biomes[biome] + ": " + found + "/9");
            helper.assertTrue(maxReads < 150000, "Arrival search exceeded bounded read budget");
        }
        helper.succeed();
    }

    private static BlockState flatCave(BlockPos pos) {
        return pos.getY() >= 251
                ? Blocks.BEDROCK.defaultBlockState()
                : pos.getY() <= 128 ? STONE : AIR;
    }

    private static WorldGenLevel world(
            GameTestHelper helper,
            BlockPos column,
            Function<BlockPos, BlockState> read,
            Map<BlockPos, BlockState> writes,
            AtomicInteger reads) {
        return TerrainTestWorld.create(
                pos -> {
                    if (Math.abs(pos.getX() - column.getX()) > CavernArrival.SEARCH_RADIUS
                            || Math.abs(pos.getZ() - column.getZ()) > CavernArrival.SEARCH_RADIUS)
                        throw new AssertionError("Arrival read outside prepared chunks: " + pos);
                    reads.incrementAndGet();
                    return writes.containsKey(pos) ? writes.get(pos) : read.apply(pos);
                },
                writes::put,
                pos ->
                        Math.abs(pos.getX() - column.getX()) <= CavernArrival.SEARCH_RADIUS
                                && Math.abs(pos.getZ() - column.getZ())
                                        <= CavernArrival.SEARCH_RADIUS,
                helper.getLevel().getBiome(helper.absolutePos(BlockPos.ZERO)),
                42L);
    }
}
