package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.MoltenFissureConfiguration;
import com.freeranger.dark_caverns.generation.MoltenPondConfiguration;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MoltenLavaTests {
    private static final BlockPos POND_ORIGIN = new BlockPos(0, 40, 0);
    private static final MoltenPondConfiguration POND_CONFIG =
            new MoltenPondConfiguration(3, 5, 6, 9, 0.08F, 0.22F);
    private static final MoltenFissureConfiguration FISSURE_CONFIG =
            new MoltenFissureConfiguration(64, 80, 112, 32, 3, 1.0F, 0.14F, 0.22F, 0.05F, 0.14F);

    private MoltenLavaTests() {}

    @GameTest(template = "sacred_torch")
    public static void pondsAreCompactRecessedContainedAndDeterministic(GameTestHelper helper) {
        var first = new HashMap<BlockPos, BlockState>();
        var repeated = new HashMap<BlockPos, BlockState>();

        helper.assertTrue(
                placePond(room(helper, first, 40, 32, false), POND_ORIGIN),
                "Pond failed on a broad molten shelf");
        helper.assertTrue(
                placePond(room(helper, repeated, 40, 32, false), POND_ORIGIN)
                        && first.equals(repeated),
                "Pond generation is not deterministic");

        Set<BlockPos> lava = states(first, Blocks.LAVA.defaultBlockState());
        helper.assertTrue(lava.size() >= 12, "Pond basin is too small: " + lava.size());
        int spanX = span(lava, true);
        int spanZ = span(lava, false);
        helper.assertTrue(
                spanX <= 19 && spanZ <= 19,
                "Pond escaped its configured compact footprint: " + spanX + "x" + spanZ);
        helper.assertTrue(
                lava.stream().allMatch(pos -> pos.getY() <= 38),
                "Pond lava was not recessed below the cavern floor");

        for (BlockPos pos : lava) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockState neighbor = read(first, pos.relative(direction), 40);
                helper.assertTrue(
                        neighbor.is(Blocks.LAVA) || natural(neighbor),
                        "Pond has an unsealed retaining wall at " + pos);
            }
        }

        helper.assertFalse(
                placePond(room(helper, new HashMap<>(), 40, 32, true), POND_ORIGIN),
                "Pond placed in a non-Molten biome");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 400)
    public static void fissuresAreLongNarrowConnectedAndChunkOrderIndependent(
            GameTestHelper helper) {
        var forward = new HashMap<BlockPos, BlockState>();
        var reverse = new HashMap<BlockPos, BlockState>();
        WorldGenLevel forwardWorld = room(helper, forward, 34, 112, false);
        WorldGenLevel reverseWorld = room(helper, reverse, 34, 112, false);

        helper.assertTrue(placeFissureGrid(forwardWorld, false), "No regional fissure generated");
        helper.assertTrue(
                placeFissureGrid(reverseWorld, true) && forward.equals(reverse),
                "Fissure output changed with chunk generation order");

        Set<BlockPos> lava = states(forward, Blocks.LAVA.defaultBlockState());
        helper.assertTrue(!lava.isEmpty(), "Regional fissures generated no lava");
        helper.assertTrue(
                lava.stream().allMatch(pos -> pos.getY() == 32),
                "Fissure lost its stable regional lava elevation");
        helper.assertTrue(
                lava.stream()
                        .filter(pos -> !lava.contains(pos.above()))
                        .allMatch(pos -> read(forward, pos.above(), 34).isAir()),
                "An exposed fissure surface was not recessed below open air");

        Set<BlockPos> largest = largestComponent(lava);
        int reach = Math.max(span(largest, true), span(largest, false));
        helper.assertTrue(
                reach >= 64, "Longest fissure did not span enough terrain: " + reach + " blocks");
        helper.assertTrue(
                largest.size() >= 64,
                "Longest fissure contains too few connected cells: " + largest.size());

        int simple = 0;
        for (BlockPos pos : largest) {
            int neighbors = 0;
            for (Direction direction : Direction.values()) {
                if (largest.contains(pos.relative(direction))) neighbors++;
            }
            helper.assertTrue(neighbors > 0, "Fissure contains an isolated lava cell at " + pos);
            if (neighbors <= 2) simple++;
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockState neighbor = read(forward, pos.relative(direction), 34);
                helper.assertTrue(
                        neighbor.is(Blocks.LAVA) || natural(neighbor),
                        "Fissure has an unsealed retaining wall at " + pos);
            }
        }
        helper.assertTrue(
                simple >= largest.size() * 0.70,
                "Fissure became too wide or tangled: simple="
                        + simple
                        + ", total="
                        + largest.size());

        var wrongBiome = new HashMap<BlockPos, BlockState>();
        helper.assertFalse(
                placeFissureGrid(room(helper, wrongBiome, 34, 112, true), false),
                "Fissures placed outside Molten Depths");
        helper.assertTrue(wrongBiome.isEmpty(), "Rejected fissures left partial edits");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 1200)
    public static void pondsAndFissuresPlaceOnRealMoltenTerrain(GameTestHelper helper) {
        var volume = new TerrainTestVolume(helper, 8675309L, "molten_depths");
        volume.carve();

        volume.feature("molten_caldera", GenerationStep.Decoration.LAKES, 0);
        Set<BlockPos> pondLava = writtenLava(volume);
        helper.assertTrue(!pondLava.isEmpty(), "No lava ponds placed on real Molten terrain");

        volume.feature("molten_fissure", GenerationStep.Decoration.LOCAL_MODIFICATIONS, 0);
        Set<BlockPos> allLava = writtenLava(volume);
        Set<BlockPos> fissureLava = new HashSet<>(allLava);
        fissureLava.removeAll(pondLava);
        helper.assertTrue(
                fissureLava.size() >= 16,
                "Regional fissures placed too little lava on real terrain: " + fissureLava.size());

        Set<BlockPos> largest = largestComponent(fissureLava);
        int reach = Math.max(span(largest, true), span(largest, false));
        helper.assertTrue(
                reach >= 12,
                "Real-terrain fissures never crossed a meaningful part of a chunk: " + reach);
        for (BlockPos pos : allLava) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockState neighbor = volume.get(pos.relative(direction));
                helper.assertTrue(
                        neighbor.is(Blocks.LAVA) || natural(neighbor),
                        "Real-terrain "
                                + (fissureLava.contains(pos) ? "fissure" : "pond")
                                + " lava has an unsealed "
                                + direction
                                + " face at "
                                + pos
                                + ": "
                                + neighbor);
            }
        }
        DarkCaverns.LOGGER.info(
                "Molten lava terrain sample: ponds={}, fissures={}, longest fissure span={}",
                pondLava.size(),
                fissureLava.size(),
                reach);
        helper.succeed();
    }

    private static boolean placeFissureGrid(WorldGenLevel level, boolean reverse) {
        List<BlockPos> origins = new ArrayList<>();
        for (int chunkX = -4; chunkX < 4; chunkX++) {
            for (int chunkZ = -4; chunkZ < 4; chunkZ++) {
                origins.add(new BlockPos(chunkX * 16, 0, chunkZ * 16));
            }
        }
        if (reverse) java.util.Collections.reverse(origins);
        boolean placed = false;
        for (BlockPos origin : origins) {
            placed |=
                    CustomFeatures.MOLTEN_FISSURE
                            .get()
                            .place(
                                    new FeaturePlaceContext<>(
                                            Optional.empty(),
                                            level,
                                            null,
                                            RandomSource.create(42),
                                            origin,
                                            FISSURE_CONFIG));
        }
        return placed;
    }

    private static WorldGenLevel room(
            GameTestHelper helper,
            Map<BlockPos, BlockState> edits,
            int floorY,
            int radius,
            boolean wrongBiome) {
        var biome =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.BIOME,
                                        DarkCaverns.id(
                                                wrongBiome ? "rocky_caverns" : "molten_depths")));
        return TerrainTestWorld.create(
                pos -> read(edits, pos, floorY),
                (pos, state) -> edits.put(pos, state),
                pos ->
                        Math.abs(pos.getX()) <= radius
                                && Math.abs(pos.getZ()) <= radius
                                && pos.getY() >= 0
                                && pos.getY() < 256,
                biome,
                7);
    }

    private static BlockState read(Map<BlockPos, BlockState> edits, BlockPos pos, int floorY) {
        return edits.getOrDefault(
                pos,
                pos.getY() >= floorY
                        ? Blocks.AIR.defaultBlockState()
                        : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState());
    }

    private static boolean placePond(WorldGenLevel level, BlockPos pos) {
        return CustomFeatures.MOLTEN_CALDERA
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                level,
                                null,
                                RandomSource.create(42),
                                pos,
                                POND_CONFIG));
    }

    private static Set<BlockPos> states(Map<BlockPos, BlockState> edits, BlockState expected) {
        Set<BlockPos> positions = new HashSet<>();
        edits.forEach(
                (pos, state) -> {
                    if (state.is(expected.getBlock())) positions.add(pos);
                });
        return positions;
    }

    private static int span(Set<BlockPos> positions, boolean xAxis) {
        if (positions.isEmpty()) return 0;
        int min =
                positions.stream().mapToInt(pos -> xAxis ? pos.getX() : pos.getZ()).min().orElse(0);
        int max =
                positions.stream().mapToInt(pos -> xAxis ? pos.getX() : pos.getZ()).max().orElse(0);
        return max - min + 1;
    }

    private static Set<BlockPos> largestComponent(Set<BlockPos> positions) {
        Set<BlockPos> remaining = new HashSet<>(positions);
        Set<BlockPos> largest = Set.of();
        while (!remaining.isEmpty()) {
            Set<BlockPos> component = new HashSet<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            BlockPos start = remaining.iterator().next();
            remaining.remove(start);
            queue.add(start);
            while (!queue.isEmpty()) {
                BlockPos pos = queue.remove();
                component.add(pos);
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = pos.relative(direction);
                    if (remaining.remove(neighbor)) queue.add(neighbor);
                }
            }
            if (component.size() > largest.size()) largest = component;
        }
        return largest;
    }

    private static Set<BlockPos> writtenLava(TerrainTestVolume volume) {
        Set<BlockPos> lava = new HashSet<>();
        volume.featureWrites.forEach(
                (pos, state) -> {
                    if (state.is(Blocks.LAVA)) lava.add(pos);
                });
        return lava;
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                || state.is(Blocks.MAGMA_BLOCK);
    }
}
