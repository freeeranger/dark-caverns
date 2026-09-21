package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import java.util.HashMap;
import java.util.HashSet;
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
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MoltenLavaTests {
    private static final BlockPos ORIGIN = new BlockPos(0, 40, 0);

    private MoltenLavaTests() {}

    @GameTest(template = "sacred_torch")
    public static void calderasAreContainedDeterministicAndProvideCrossings(GameTestHelper helper) {
        var first = new HashMap<BlockPos, BlockState>();
        var repeated = new HashMap<BlockPos, BlockState>();

        helper.assertTrue(
                placeCaldera(room(helper, first, false, false), ORIGIN),
                "Caldera failed on a broad molten shelf");
        helper.assertTrue(
                placeCaldera(room(helper, repeated, false, false), ORIGIN)
                        && first.equals(repeated),
                "Caldera generation is not deterministic");

        Set<BlockPos> lava = new HashSet<>();
        Set<BlockPos> rim = new HashSet<>();
        Set<BlockPos> steppingStones = new HashSet<>();

        first.forEach(
                (pos, state) -> {
                    helper.assertFalse(
                            state.is(CustomBlocks.MOLTEN_CARFSTONE_SLAB.get()),
                            "Slabs must not generate in natural volcanic calderas");
                    helper.assertFalse(
                            state.is(CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get()),
                            "Smooth carfstone must not generate in natural volcanic calderas");
                    helper.assertFalse(
                            state.is(Blocks.FIRE),
                            "Fire spam must not generate around volcanic calderas");
                    if (state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())) {
                        helper.assertTrue(
                                read(first, pos.above()).isAir(),
                                "Ashy carfstone must never generate under solid blocks: " + pos);
                    }

                    if (state.is(Blocks.LAVA)) lava.add(pos);
                    if (pos.getY() == 39 && state.is(Blocks.MAGMA_BLOCK)) {
                        // Check if it's adjacent to lava (stepping stone)
                        boolean nextToLava = false;
                        for (Direction d : Direction.Plane.HORIZONTAL) {
                            if (read(first, pos.relative(d)).is(Blocks.LAVA)) {
                                nextToLava = true;
                                break;
                            }
                        }
                        if (nextToLava) {
                            steppingStones.add(pos);
                        } else {
                            rim.add(pos);
                        }
                    }
                });

        helper.assertTrue(lava.size() >= 20, "Caldera basin is too small: " + lava.size());
        helper.assertTrue(
                !steppingStones.isEmpty(), "Caldera generated without stepping stone crossings");

        // Zero leak policy: every lava block must be bounded
        for (BlockPos pos : lava) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockState neighbor = read(first, pos.relative(direction));
                helper.assertTrue(
                        neighbor.is(Blocks.LAVA)
                                || neighbor.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                                || neighbor.is(CustomBlocks.CARFSTONE.get())
                                || neighbor.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                                || neighbor.is(Blocks.MAGMA_BLOCK),
                        "Caldera has an unsealed retaining wall at " + pos);
            }
        }

        // Wrong biome test
        helper.assertFalse(
                placeCaldera(room(helper, new HashMap<>(), false, true), ORIGIN),
                "Caldera placed in a non-Molten biome");

        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void canalsAreContainedAndProvideSteppingCrossings(GameTestHelper helper) {
        var first = new HashMap<BlockPos, BlockState>();
        var repeated = new HashMap<BlockPos, BlockState>();

        helper.assertTrue(
                placeCanal(room(helper, first, false, false), ORIGIN),
                "Canal failed on a broad molten floor");
        helper.assertTrue(
                placeCanal(room(helper, repeated, false, false), ORIGIN) && first.equals(repeated),
                "Canal generation is not deterministic");

        Set<BlockPos> lava = new HashSet<>();
        Set<BlockPos> crossings = new HashSet<>();

        first.forEach(
                (pos, state) -> {
                    helper.assertFalse(
                            state.is(CustomBlocks.MOLTEN_CARFSTONE_SLAB.get()),
                            "Slabs must not generate in natural volcanic canals");
                    helper.assertFalse(
                            state.is(CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get()),
                            "Smooth carfstone must not generate in natural volcanic canals");
                    helper.assertFalse(
                            state.is(Blocks.FIRE),
                            "Fire spam must not generate along volcanic canals");
                    if (state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())) {
                        helper.assertTrue(
                                read(first, pos.above()).isAir(),
                                "Ashy carfstone must never generate under solid blocks: " + pos);
                    }

                    if (state.is(Blocks.LAVA)) lava.add(pos);
                    if (pos.getY() == 39
                            && (state.is(Blocks.MAGMA_BLOCK)
                                    || state.is(CustomBlocks.MOLTEN_CARFSTONE.get()))) {
                        // Check if adjacent to lava in the stream
                        for (Direction d : Direction.Plane.HORIZONTAL) {
                            if (read(first, pos.relative(d)).is(Blocks.LAVA)) {
                                crossings.add(pos);
                                break;
                            }
                        }
                    }
                });

        helper.assertTrue(lava.size() >= 12, "Canal generated too few lava cells: " + lava.size());
        helper.assertTrue(!crossings.isEmpty(), "Canal generated without walking crossings");

        // Zero leak policy: every lava cell in canal must be sealed
        for (BlockPos pos : lava) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockState neighbor = read(first, pos.relative(direction));
                helper.assertTrue(
                        neighbor.is(Blocks.LAVA)
                                || neighbor.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                                || neighbor.is(CustomBlocks.CARFSTONE.get())
                                || neighbor.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                                || neighbor.is(Blocks.MAGMA_BLOCK),
                        "Canal has an unsealed retaining wall at " + pos);
            }
        }

        helper.succeed();
    }

    private static WorldGenLevel room(
            GameTestHelper helper,
            Map<BlockPos, BlockState> edits,
            boolean clipped,
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
                pos -> read(edits, pos),
                (pos, state) -> edits.put(pos, state),
                pos -> Math.abs(pos.getX()) <= (clipped ? 0 : 25) && Math.abs(pos.getZ()) <= 25,
                biome,
                7);
    }

    private static BlockState read(Map<BlockPos, BlockState> edits, BlockPos pos) {
        return edits.getOrDefault(
                pos,
                pos.getY() >= 40
                        ? Blocks.AIR.defaultBlockState()
                        : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState());
    }

    private static boolean placeCaldera(WorldGenLevel level, BlockPos pos) {
        return CustomFeatures.MOLTEN_CALDERA
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                level,
                                null,
                                RandomSource.create(42),
                                pos,
                                NoneFeatureConfiguration.INSTANCE));
    }

    private static boolean placeCanal(WorldGenLevel level, BlockPos pos) {
        return CustomFeatures.MOLTEN_CANAL
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                level,
                                null,
                                RandomSource.create(42),
                                pos,
                                NoneFeatureConfiguration.INSTANCE));
    }
}
