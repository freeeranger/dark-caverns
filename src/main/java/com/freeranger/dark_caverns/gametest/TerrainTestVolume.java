package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;

/** Real generated chunks with an isolated bounded feature-access view. */
final class TerrainTestVolume {
    static final int CHUNKS = 8;
    static final int WIDTH = CHUNKS * 16;
    static final int MIN = -WIDTH / 2;
    static final LevelHeightAccessor HEIGHT = LevelHeightAccessor.create(0, 256);
    final ProtoChunk[][] chunks = new ProtoChunk[CHUNKS][CHUNKS];
    final NoiseBasedChunkGenerator generator;
    final RandomState random;
    final Holder<Biome> biome;
    final long seed;
    final WorldGenLevel world;
    final GameTestHelper helper;
    final Map<BlockPos, BlockState> featureWrites = new HashMap<>();

    TerrainTestVolume(GameTestHelper helper, long seed, String biomeName) {
        this.helper = helper;
        this.seed = seed;
        var level = helper.getLevel();
        var registries = level.registryAccess();
        var biomes = registries.registryOrThrow(Registries.BIOME);
        biome =
                biomes.getHolderOrThrow(
                        ResourceKey.create(Registries.BIOME, DarkCaverns.id(biomeName)));
        var settings =
                registries
                        .registryOrThrow(Registries.NOISE_SETTINGS)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.NOISE_SETTINGS, DarkCaverns.id("dark_caverns")));
        generator = new NoiseBasedChunkGenerator(new FixedBiomeSource(biome), settings);
        random =
                RandomState.create(
                        settings.value(), registries.lookupOrThrow(Registries.NOISE), seed);
        var biomeManager = new BiomeManager((x, y, z) -> biome, BiomeManager.obfuscateSeed(seed));
        var fluid =
                new Aquifer.FluidStatus(
                        settings.value().seaLevel(), settings.value().defaultFluid());
        for (int x = 0; x < CHUNKS; x++) {
            for (int z = 0; z < CHUNKS; z++) {
                var chunk =
                        new ProtoChunk(
                                new ChunkPos(x - CHUNKS / 2, z - CHUNKS / 2),
                                UpgradeData.EMPTY,
                                HEIGHT,
                                biomes,
                                null);
                chunks[x][z] = chunk;
                chunk.getOrCreateNoiseChunk(
                        c ->
                                NoiseChunk.forChunk(
                                        c,
                                        random,
                                        new Beardifier(
                                                new ObjectArrayList<Beardifier.Rigid>().iterator(),
                                                new ObjectArrayList<JigsawJunction>().iterator()),
                                        settings.value(),
                                        (bx, by, bz) -> fluid,
                                        Blender.empty()));
                generator
                        .createBiomes(random, Blender.empty(), level.structureManager(), chunk)
                        .join();
                generator
                        .fillFromNoise(Blender.empty(), random, level.structureManager(), chunk)
                        .join();
                generator.buildSurface(
                        chunk,
                        new WorldGenerationContext(generator, HEIGHT),
                        random,
                        level.structureManager(),
                        biomeManager,
                        biomes,
                        Blender.empty());
            }
        }
        world =
                TerrainTestWorld.create(
                        this::get,
                        this::set,
                        this::inside,
                        biome,
                        seed,
                        pos -> chunks[pos.x + CHUNKS / 2][pos.z + CHUNKS / 2]);
    }

    void carve() {
        var carver =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.CONFIGURED_CARVER)
                        .getOrThrow(
                                ResourceKey.create(
                                        Registries.CONFIGURED_CARVER,
                                        DarkCaverns.id("dark_caverns_cave")));
        var settings = generator.generatorSettings().value();
        for (var row : chunks) {
            for (var chunk : row) {
                var noise =
                        chunk.getOrCreateNoiseChunk(
                                c -> {
                                    throw new AssertionError("Missing initialized noise chunk");
                                });
                var context =
                        new CarvingContext(
                                generator,
                                helper.getLevel().registryAccess(),
                                HEIGHT,
                                noise,
                                random,
                                settings.surfaceRule());
                var carvingRandom = new WorldgenRandom(new LegacyRandomSource(0));
                var mask = chunk.getOrCreateCarvingMask(GenerationStep.Carving.AIR);
                // Same neighboring start chunks and random seeding as
                // NoiseBasedChunkGenerator.applyCarvers.
                // All three cavern biomes use the same single carver.
                for (int dx = -8; dx <= 8; dx++) {
                    for (int dz = -8; dz <= 8; dz++) {
                        var start = new ChunkPos(chunk.getPos().x + dx, chunk.getPos().z + dz);
                        carvingRandom.setLargeFeatureSeed(seed, start.x, start.z);
                        if (carver.isStartChunk(carvingRandom))
                            carver.carve(
                                    context,
                                    chunk,
                                    pos -> biome,
                                    carvingRandom,
                                    noise.aquifer(),
                                    start,
                                    mask);
                    }
                }
            }
        }
    }

    void formations(String featureName) {
        feature(featureName, GenerationStep.Decoration.UNDERGROUND_DECORATION, 0);
    }

    void feature(String featureName, GenerationStep.Decoration step, int index) {
        var feature =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.PLACED_FEATURE)
                        .getOrThrow(
                                ResourceKey.create(
                                        Registries.PLACED_FEATURE, DarkCaverns.id(featureName)));
        // Keep a one-chunk margin so formation footprints never need unavailable neighbors.
        for (int x = 1; x < CHUNKS - 1; x++) {
            for (int z = 1; z < CHUNKS - 1; z++) {
                var pos = chunks[x][z].getPos().getWorldPosition();
                var featureRandom = new WorldgenRandom(new LegacyRandomSource(0));
                long decorationSeed = featureRandom.setDecorationSeed(seed, pos.getX(), pos.getZ());
                featureRandom.setFeatureSeed(decorationSeed, index, step.ordinal());
                feature.placeWithBiomeCheck(world, generator, featureRandom, pos);
            }
        }
    }

    boolean inside(BlockPos pos) {
        return pos.getX() >= MIN
                && pos.getX() < MIN + WIDTH
                && pos.getZ() >= MIN
                && pos.getZ() < MIN + WIDTH
                && pos.getY() >= 0
                && pos.getY() < 256;
    }

    BlockState get(BlockPos pos) {
        if (!inside(pos)) return Blocks.BEDROCK.defaultBlockState();
        return chunks[(pos.getX() - MIN) >> 4][(pos.getZ() - MIN) >> 4].getBlockState(pos);
    }

    void set(BlockPos pos, BlockState state) {
        if (!inside(pos)) throw new AssertionError("Out-of-volume write");
        chunks[(pos.getX() - MIN) >> 4][(pos.getZ() - MIN) >> 4].setBlockState(pos, state, false);
        featureWrites.put(pos.immutable(), state);
    }

    byte[] snapshot() {
        byte[] blocks = new byte[WIDTH * WIDTH * 256];
        var pos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < 256; y++) {
            for (int z = 0; z < WIDTH; z++) {
                for (int x = 0; x < WIDTH; x++) {
                    var state = get(pos.set(x + MIN, y, z + MIN));
                    blocks[(y * WIDTH + z) * WIDTH + x] =
                            (byte) (state.isAir() ? 0 : state.getFluidState().isEmpty() ? 1 : 2);
                }
            }
        }
        return blocks;
    }
}
