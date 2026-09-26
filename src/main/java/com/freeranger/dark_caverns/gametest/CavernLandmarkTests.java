package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernLandmarkConfiguration;
import com.freeranger.dark_caverns.generation.CavernLandmarkFeature;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CavernLandmarkTests {
    private static final BlockState STONE = CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private CavernLandmarkTests() {}

    @GameTest(template = "sacred_torch", timeoutTicks = 600)
    public static void landmarksPopulateMultipleFloorsAndKeepSpacingAndTemplates(
            GameTestHelper helper) {
        var forward = new Room(helper);
        var reverse = new Room(helper);
        int torches = populate(helper, forward, false);
        int reversed = populate(helper, reverse, true);
        helper.assertTrue(
                torches > 48 && torches == reversed,
                "Torch groups were missing or chunk order changed counts");
        helper.assertTrue(
                forward.writes.equals(reverse.writes),
                "Feature or chunk order changed landmark blocks");
        var centers = new ArrayList<BlockPos>();
        int[] perFloor = new int[3];
        for (var entry : forward.writes.entrySet()) {
            if (entry.getValue().is(CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get())
                    && (entry.getKey().getY() == 41
                            || entry.getKey().getY() == 91
                            || entry.getKey().getY() == 141)) {
                centers.add(entry.getKey().below());
                perFloor[(entry.getKey().getY() - 41) / 50]++;
            }
            if (entry.getValue().is(Blocks.PLAYER_HEAD)) {
                var skull = (SkullBlockEntity) forward.world.getBlockEntity(entry.getKey());
                helper.assertTrue(
                        skull != null && skull.getOwnerProfile() != null,
                        "Template placement lost marker skull profile data");
            }
        }
        for (int count : perFloor)
            helper.assertTrue(count >= 16, "One cave floor consumed another floor's placements");
        for (int i = 0; i < centers.size(); i++) {
            for (int j = i + 1; j < centers.size(); j++) {
                var a = centers.get(i);
                var b = centers.get(j);
                if (a.getY() == b.getY())
                    helper.assertTrue(a.distSqr(b) >= 49, "Torches violated seven-block spacing");
            }
        }
        helper.succeed();
    }

    private static int populate(GameTestHelper helper, Room room, boolean reverse) {
        int torches = 0;
        int markers = 0;
        for (int order = 0; order < 16; order++) {
            int index = reverse ? 15 - order : order;
            var chunk = new ChunkPos(index / 4 - 2, index % 4 - 2);
            for (int step = 0; step < 2; step++) {
                boolean torch = (reverse ? 1 - step : step) == 0;
                String name = torch ? "sacred_torch" : "territory_marker";
                var config =
                        new CavernLandmarkConfiguration(
                                DarkCaverns.id(name), 1, torch, torch ? 12 : 16);
                int placed =
                        CavernLandmarkFeature.decorate(
                                room.world,
                                chunk,
                                RandomSource.create(8675309L + index * 31L + (torch ? 0 : 1000)),
                                config,
                                template(helper, name));
                if (torch) torches += placed;
                else markers += placed;
            }
        }
        helper.assertTrue(markers == 48, "Markers should independently populate all three floors");
        return torches;
    }

    @GameTest(template = "sacred_torch")
    public static void landmarksPreflightProtectedBlocksFluidsAndBounds(GameTestHelper helper) {
        for (String name : new String[] {"sacred_torch", "territory_marker"}) {
            for (BlockState obstacle :
                    new BlockState[] {
                        Blocks.CHEST.defaultBlockState(),
                        Blocks.LAVA.defaultBlockState(),
                        Blocks.BEDROCK.defaultBlockState(),
                        CustomBlocks.LUMINITE_ORE.get().defaultBlockState(),
                        STONE
                    }) {
                var room = new Room(helper);
                room.original.put(new BlockPos(8, 42, 8), obstacle);
                helper.assertTrue(
                        !CavernLandmarkFeature.placeOnFloor(
                                room.world,
                                new ChunkPos(0, 0),
                                template(helper, name),
                                new BlockPos(8, 40, 8),
                                RandomSource.create(7)),
                        "Landmark overwrote protected headroom");
                helper.assertTrue(room.writes.isEmpty(), "Rejected template left partial writes");
            }
            var unsupported = new Room(helper);
            for (int y = 34; y < 40; y++) unsupported.original.put(new BlockPos(8, y, 8), AIR);
            helper.assertTrue(
                    !CavernLandmarkFeature.placeOnFloor(
                                    unsupported.world,
                                    new ChunkPos(0, 0),
                                    template(helper, name),
                                    new BlockPos(8, 40, 8),
                                    RandomSource.create(7))
                            && unsupported.writes.isEmpty(),
                    "Landmark bridged a deep unsupported gap");
            var clipped = new Room(helper);
            clipped.bounds = pos -> pos.getX() < 8;
            helper.assertTrue(
                    !CavernLandmarkFeature.placeOnFloor(
                                    clipped.world,
                                    new ChunkPos(0, 0),
                                    template(helper, name),
                                    new BlockPos(8, 40, 8),
                                    RandomSource.create(7))
                            && clipped.writes.isEmpty(),
                    "Clipped template left a partial placement");
            var edge = new Room(helper);
            helper.assertTrue(
                    !CavernLandmarkFeature.placeOnFloor(
                                    edge.world,
                                    new ChunkPos(0, 0),
                                    template(helper, name),
                                    new BlockPos(0, 40, 0),
                                    RandomSource.create(7))
                            && edge.writes.isEmpty(),
                    "Landmark crossed its owning chunk");
        }
        var gap = new Room(helper);
        for (int y = 38; y <= 40; y++) gap.original.put(new BlockPos(7, y, 7), AIR);
        helper.assertTrue(
                CavernLandmarkFeature.placeOnFloor(
                        gap.world,
                        new ChunkPos(0, 0),
                        template(helper, "sacred_torch"),
                        new BlockPos(8, 40, 8),
                        RandomSource.create(7)),
                "Small supported foundation gap should fit");
        helper.assertTrue(
                gap.read(new BlockPos(7, 38, 7)).is(CustomBlocks.MOLTEN_CARFSTONE.get()),
                "Short foundation was not filled");
        helper.succeed();
    }

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_slow",
            template = "sacred_torch",
            timeoutTicks = 1200)
    public static void registeredLandmarksAreEncounterableWithoutBlockingRoutes(
            GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        for (String name : new String[] {"sacred_torch", "territory_marker"}) {
            var set =
                    registries
                            .registryOrThrow(Registries.STRUCTURE_SET)
                            .getOrThrow(
                                    ResourceKey.create(
                                            Registries.STRUCTURE_SET, DarkCaverns.id(name)));
            for (int x = -16; x <= 16; x++)
                helper.assertTrue(
                        !set.placement().applyAdditionalChunkRestrictions(x, -x, 8675309),
                        "Old structure starts still generate duplicates");
        }
        for (long seed : new long[] {8675309}) {
            var volume = new TerrainTestVolume(helper, seed, "molten_depths");
            volume.carve();
            volume.feature("luminite_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 0);
            volume.feature("hellstone_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 8);
            volume.feature("magma_patch", GenerationStep.Decoration.UNDERGROUND_ORES, 9);
            var protectedBlocks = new HashMap<>(volume.featureWrites);
            volume.formations("molten_spike_feature");
            var before = volume.snapshot();
            var topology = TerrainTopology.measure(before);
            volume.featureWrites.clear();
            long start = System.nanoTime();
            volume.feature(
                    "sacred_torch_decoration", GenerationStep.Decoration.UNDERGROUND_DECORATION, 1);
            volume.feature(
                    "territory_marker_decoration",
                    GenerationStep.Decoration.UNDERGROUND_DECORATION,
                    2);
            long elapsed = System.nanoTime() - start;
            int torches = 0, heads = 0;
            var levels = new java.util.HashSet<Integer>();
            for (var entry : volume.featureWrites.entrySet()) {
                if (entry.getValue().is(Blocks.FIRE)) {
                    torches++;
                    levels.add(entry.getKey().getY() / 32);
                }
                if (entry.getValue().is(Blocks.PLAYER_HEAD)) heads++;
            }
            helper.assertTrue(
                    torches >= 12 && heads >= 4 && torches > heads / 4,
                    "Small landmarks remain too sparse, or markers outnumber torches: "
                            + torches
                            + "/"
                            + heads);
            helper.assertTrue(
                    levels.size() >= 3, "Landmarks were concentrated in just one height band");
            for (var entry : protectedBlocks.entrySet())
                helper.assertTrue(
                        volume.get(entry.getKey()).equals(entry.getValue()),
                        "Landmark replaced ore or magma");
            var after = volume.snapshot();
            var afterTopology = TerrainTopology.measure(after);
            int lost = 0;
            for (int i = 0; i < before.length; i++) {
                if (before[i] == 2) helper.assertTrue(after[i] == 2, "Landmark replaced fluid");
                if (TerrainIntegrationTests.canStand(before, i)
                        && !TerrainIntegrationTests.canStand(after, i)) lost++;
            }
            helper.assertTrue(
                    lost < topology.walkable * .05
                            && afterTopology.largestWalk >= topology.largestWalk * .8,
                    "Landmarks obstructed too many cave routes");
            DarkCaverns.LOGGER.info(
                    "Cave-floor landmarks seed {}: torches={}, markerHeads={}, heightBands={},"
                            + " lostFloors={}/{}, featureTime={}ms across 36 decorated chunks",
                    seed,
                    torches,
                    heads,
                    levels.size(),
                    lost,
                    topology.walkable,
                    elapsed / 1_000_000.0);
        }
        helper.succeed();
    }

    private static StructureTemplate template(GameTestHelper helper, String name) {
        return helper.getLevel().getStructureManager().getOrCreate(DarkCaverns.id(name));
    }

    private static final class Room {
        final Map<BlockPos, BlockState> original = new HashMap<>();
        final Map<BlockPos, BlockState> writes = new HashMap<>();
        Predicate<BlockPos> bounds =
                pos ->
                        pos.getX() >= -32
                                && pos.getX() < 32
                                && pos.getZ() >= -32
                                && pos.getZ() < 32
                                && pos.getY() >= 0
                                && pos.getY() < 256;
        final WorldGenLevel world;

        Room(GameTestHelper helper) {
            var biome =
                    helper.getLevel()
                            .registryAccess()
                            .registryOrThrow(Registries.BIOME)
                            .getHolderOrThrow(
                                    ResourceKey.create(
                                            Registries.BIOME, DarkCaverns.id("molten_depths")));
            world =
                    TerrainTestWorld.create(
                            this::read,
                            (pos, state) -> writes.put(pos.immutable(), state),
                            pos -> bounds.test(pos),
                            pos -> biome,
                            8675309,
                            pos -> {
                                throw new AssertionError("Landmark requested a terrain chunk");
                            },
                            helper.getLevel());
        }

        BlockState read(BlockPos pos) {
            if (writes.containsKey(pos)) return writes.get(pos);
            if (original.containsKey(pos)) return original.get(pos);
            return pos.getY() <= 40
                            || pos.getY() >= 85 && pos.getY() <= 90
                            || pos.getY() >= 135 && pos.getY() <= 140
                    ? STONE
                    : AIR;
        }
    }
}
