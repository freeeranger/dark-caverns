package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.BiomeTransition;
import com.freeranger.dark_caverns.generation.CavernFloorFinder;
import com.freeranger.dark_caverns.generation.CavernNoiseColumns;
import com.freeranger.dark_caverns.generation.TransitionPlacementCache;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldgenPerformanceTests {
    private WorldgenPerformanceTests() {}

    @GameTest(template = "sacred_torch", timeoutTicks = 600)
    public static void batchedColumnsMatchVanillaAndPreserveFloorRandomness(GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        var settings =
                registries
                        .registryOrThrow(Registries.NOISE_SETTINGS)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.NOISE_SETTINGS, DarkCaverns.id("dark_caverns")));
        var generator =
                new NoiseBasedChunkGenerator(
                        new FixedBiomeSource(biome(helper, "glimmershroom_forest")), settings);
        long vanillaTime = 0, batchedTime = 0;
        int compared = 0;
        for (long seed : new long[] {0, 8675309, -7046029254386353131L}) {
            var random =
                    RandomState.create(
                            settings.value(), registries.lookupOrThrow(Registries.NOISE), seed);
            // Cross interpolation-cell and chunk boundaries, including negative coordinates.
            for (int origin : new int[] {-19, -5, 13}) {
                var bounds = new BoundingBox(origin, 0, origin - 3, origin + 10, 255, origin + 7);
                var reference = new ArrayList<NoiseColumn>();
                long start = System.nanoTime();
                for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
                    for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                        reference.add(
                                generator.getBaseColumn(x, z, TerrainTestVolume.HEIGHT, random));
                    }
                }
                vanillaTime += System.nanoTime() - start;
                start = System.nanoTime();
                var batched =
                        CavernNoiseColumns.footprint(
                                generator, TerrainTestVolume.HEIGHT, random, bounds);
                // Force realization so the timing doesn't just measure lazy-list construction.
                for (int i = 0; i < batched.size(); i++) batched.get(i);
                batchedTime += System.nanoTime() - start;
                for (int i = 0; i < reference.size(); i++) {
                    for (int y = 0; y < 256; y++) {
                        helper.assertTrue(
                                reference.get(i).getBlock(y) == batched.get(i).getBlock(y),
                                "Batched base column changed terrain at column " + i + " y=" + y);
                        compared++;
                    }
                }
                var oldRandom = RandomSource.create(seed);
                var newRandom = RandomSource.create(seed);
                helper.assertTrue(
                        CavernFloorFinder.find(reference, 15, 236, 10, oldRandom)
                                .equals(CavernFloorFinder.find(batched, 15, 236, 10, newRandom)),
                        "Floor selection changed");
                helper.assertTrue(
                        oldRandom.nextLong() == newRandom.nextLong(),
                        "Floor random stream changed");
            }
        }
        // Clamped and non-cell-aligned height views must retain vanilla's exact behavior.
        var clampedRandom =
                RandomState.create(settings.value(), registries.lookupOrThrow(Registries.NOISE), 0);
        var clampedBounds = new BoundingBox(-2, 0, -3, 4, 255, 3);
        for (var height :
                new LevelHeightAccessor[] {
                    LevelHeightAccessor.create(64, 128), LevelHeightAccessor.create(75, 128)
                }) {
            var columns =
                    CavernNoiseColumns.footprint(generator, height, clampedRandom, clampedBounds);
            for (int i = 0; i < columns.size(); i++) {
                var expected =
                        generator.getBaseColumn(
                                clampedBounds.minX() + i / clampedBounds.getZSpan(),
                                clampedBounds.minZ() + i % clampedBounds.getZSpan(),
                                height,
                                clampedRandom);
                for (int y = height.getMinBuildHeight(); y < height.getMaxBuildHeight(); y++) {
                    helper.assertTrue(
                            expected.getBlock(y) == columns.get(i).getBlock(y),
                            "Clamped height changed base-column behavior");
                }
            }
        }
        DarkCaverns.LOGGER.info(
                "Structure-column benchmark: {} block comparisons, vanilla={}ms batched={}ms ({}x)",
                compared,
                vanillaTime / 1_000_000,
                batchedTime / 1_000_000,
                vanillaTime / (double) batchedTime);
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void structureBiomeRejectionIsEarlyButSupportsVerticalBiomes(
            GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        var forest = biome(helper, "glimmershroom_forest");
        var rocky = biome(helper, "rocky_caverns");
        var settings =
                registries
                        .registryOrThrow(Registries.NOISE_SETTINGS)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.NOISE_SETTINGS, DarkCaverns.id("dark_caverns")));
        var structure =
                registries
                        .registryOrThrow(Registries.STRUCTURE)
                        .getOrThrow(
                                ResourceKey.create(
                                        Registries.STRUCTURE, DarkCaverns.id("shroomie_house")));
        var random =
                RandomState.create(
                        settings.value(), registries.lookupOrThrow(Registries.NOISE), 8675309);
        BlockState[] states = new BlockState[256];
        Arrays.fill(states, Blocks.AIR.defaultBlockState());
        Arrays.fill(states, 0, 81, CustomBlocks.CARFSTONE.get().defaultBlockState());
        for (boolean vertical : new boolean[] {false, true}) {
            AtomicInteger samples = new AtomicInteger();
            BiomeSource source =
                    new BiomeSource() {
                        @Override
                        protected MapCodec<? extends BiomeSource> codec() {
                            return MapCodec.unit(this);
                        }

                        @Override
                        protected Stream<Holder<Biome>> collectPossibleBiomes() {
                            return Stream.of(rocky, forest);
                        }

                        @Override
                        public Holder<Biome> getNoiseBiome(
                                int x, int y, int z, Climate.Sampler sampler) {
                            return vertical && y == 20 ? forest : rocky;
                        }
                    };
            var generator =
                    new NoiseBasedChunkGenerator(source, settings) {
                        @Override
                        public NoiseColumn getBaseColumn(
                                int x, int z, LevelHeightAccessor height, RandomState state) {
                            samples.incrementAndGet();
                            return new NoiseColumn(0, states);
                        }
                    };
            var context =
                    new Structure.GenerationContext(
                            registries,
                            generator,
                            source,
                            random,
                            helper.getLevel().getStructureManager(),
                            8675309,
                            new ChunkPos(0, 0),
                            TerrainTestVolume.HEIGHT,
                            holder -> holder.equals(forest));
            var result = structure.findValidGenerationPoint(context);
            helper.assertTrue(
                    vertical
                            ? result.isPresent() && samples.get() > 0
                            : result.isEmpty() && samples.get() == 0,
                    "Biome early rejection either sampled useless terrain or rejected a vertical"
                            + " biome");
        }
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void placementCacheReusesSamplesWithoutCrossingRegions(GameTestHelper helper) {
        var forest = biome(helper, "glimmershroom_forest");
        var rocky = biome(helper, "rocky_caverns");
        AtomicInteger lookups = new AtomicInteger();
        Function<BlockPos, Holder<Biome>> source = pos -> pos.getX() < 0 ? forest : rocky;
        var world =
                TerrainTestWorld.create(
                        pos -> Blocks.AIR.defaultBlockState(),
                        (pos, state) -> {},
                        pos -> true,
                        pos -> {
                            lookups.incrementAndGet();
                            return source.apply(pos);
                        },
                        7,
                        pos -> null);
        int uncached = 0;
        for (int pass = 0; pass < 3; pass++) {
            for (int x = -16; x < 0; x++) {
                for (int z = -16; z < 0; z++) {
                    var expected =
                            new BiomeTransition(
                                            pos -> {
                                                return source.apply(pos);
                                            })
                                    .weights(x, z);
                    var actual = TransitionPlacementCache.weights(world, x, z);
                    helper.assertTrue(expected.equals(actual), "Cache changed blend weights");
                    uncached += (x % 8 == 0 ? 3 : 4) * (z % 8 == 0 ? 3 : 4);
                }
            }
        }
        helper.assertTrue(lookups.get() <= 25, "Placement tile did not reuse its biome lattice");
        int sampled = lookups.get();
        var other =
                TerrainTestWorld.create(
                        pos -> Blocks.AIR.defaultBlockState(),
                        (pos, state) -> {},
                        pos -> true,
                        rocky,
                        7);
        helper.assertTrue(
                TransitionPlacementCache.weights(other, -8, -8).forest() == 0,
                "Cache leaked across region identity");
        helper.assertTrue(
                TransitionPlacementCache.weights(world, -8, -8).forest() > 0,
                "Cache did not reset on region switch");
        helper.assertTrue(
                TransitionPlacementCache.weights(world, 32, 32).forest() == 0,
                "Cache leaked across tile boundary");
        DarkCaverns.LOGGER.info(
                "Transition lookup benchmark: uncached={} cached={} for 3 passes over 256 columns",
                uncached,
                sampled);
        helper.succeed();
    }

    private static Holder<Biome> biome(GameTestHelper helper, String name) {
        return helper.getLevel()
                .registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getHolderOrThrow(ResourceKey.create(Registries.BIOME, DarkCaverns.id(name)));
    }
}
