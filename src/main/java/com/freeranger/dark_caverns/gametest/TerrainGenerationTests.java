package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TerrainGenerationTests {
    private static final long[] SEEDS = {0L, 8675309L, -7046029254386353131L};
    private static final LevelHeightAccessor HEIGHT = LevelHeightAccessor.create(0, 256);

    private TerrainGenerationTests() {}

    @GameTest(template = "sacred_torch", timeoutTicks = 400)
    public static void composedTerrainHasCavernsAndClosedBoundaries(GameTestHelper helper)
            throws IOException {
        var generator = generator(helper);
        var registries = helper.getLevel().registryAccess();
        var settings =
                registries
                        .registryOrThrow(Registries.NOISE_SETTINGS)
                        .getOrThrow(
                                ResourceKey.create(
                                        Registries.NOISE_SETTINGS, DarkCaverns.id("dark_caverns")));
        helper.assertTrue(
                settings.noiseSettings().minY() == 0 && settings.noiseSettings().height() == 256,
                "Keep the full cavern height");
        helper.assertFalse(
                settings.useLegacyRandomSource(), "Terrain must use the modern random source");
        helper.assertFalse(
                settings.isAquifersEnabled(), "Aquifers need custom fluid routing first");
        helper.assertTrue(
                settings.seaLevel() == 11 && settings.defaultFluid().is(Blocks.LAVA),
                "Keep the initial lower lava boundary");

        long previousFingerprint = 0;
        for (long seed : SEEDS) {
            RandomState random =
                    RandomState.create(settings, registries.lookupOrThrow(Registries.NOISE), seed);
            Sample sample = sample(helper, generator, random);
            writeSlice(generator, random, seed);
            double airFraction = sample.air / (1024.0 * 256.0);
            DarkCaverns.LOGGER.info(
                    "Terrain seed {}: air={}, lowerAir={}, middleAir={}, upperAir={},"
                            + " vaultColumns={}, denseColumns={}, tallRiftColumns={}, maxGap={},"
                            + " fingerprint={}",
                    seed,
                    airFraction,
                    sample.lowerAir / (1024.0 * 48.0),
                    sample.middleAir / (1024.0 * 128.0),
                    sample.upperAir / (1024.0 * 48.0),
                    sample.vaultColumns,
                    sample.denseColumns,
                    sample.tallRiftColumns,
                    sample.maxGap,
                    sample.fingerprint);
            // Broad guardrails: tuning should not accidentally erase either caves or mining
            // terrain.
            helper.assertTrue(
                    airFraction > 0.15 && airFraction < 0.60,
                    "Unexpected open volume for seed " + seed + ": " + airFraction);
            helper.assertTrue(sample.vaultColumns > 30, "Missing large caverns for seed " + seed);
            helper.assertTrue(sample.denseColumns > 100, "Missing dense passages for seed " + seed);
            helper.assertTrue(
                    sample.tallRiftColumns > 0 && sample.tallRiftColumns < 31,
                    "Very tall openings should exist but remain rare for seed " + seed);
            helper.assertTrue(
                    sample.lowerAir / (1024.0 * 48.0) > 0.03,
                    "The lower caverns need passages, not a solid slab");
            helper.assertTrue(
                    sample.middleAir / 128.0 > sample.lowerAir / 48.0
                            && sample.middleAir / 128.0 > sample.upperAir / 48.0,
                    "Central elevations should be more open than the floor and roof regions");
            helper.assertTrue(
                    sample.fingerprint != previousFingerprint,
                    "Different seeds produced identical terrain");
            previousFingerprint = sample.fingerprint;

            RandomState repeated =
                    RandomState.create(settings, registries.lookupOrThrow(Registries.NOISE), seed);
            for (int x = -64; x <= 64; x += 16) {
                for (int y = 0; y < 256; y += 7) {
                    var point = new DensityFunction.SinglePointContext(x, y, -x);
                    helper.assertTrue(
                            Double.isFinite(random.router().finalDensity().compute(point)),
                            "Terrain density must remain finite");
                    helper.assertTrue(
                            random.router().finalDensity().compute(point)
                                    == repeated.router().finalDensity().compute(point),
                            "Seeded terrain is not deterministic");
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 400)
    public static void generatedCavernsKeepBedrockShell(GameTestHelper helper) {
        var generator = generator(helper);
        var level = helper.getLevel();
        var biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        var random =
                RandomState.create(
                        generator.generatorSettings().value(),
                        level.registryAccess().lookupOrThrow(Registries.NOISE),
                        SEEDS[0]);
        var chunk = new ProtoChunk(new ChunkPos(0, 0), UpgradeData.EMPTY, HEIGHT, biomes, null);
        // No structure lookups into the test world from the generation worker.
        var settings = generator.generatorSettings().value();
        chunk.getOrCreateNoiseChunk(
                c ->
                        NoiseChunk.forChunk(
                                c,
                                random,
                                new Beardifier(
                                        new ObjectArrayList<Beardifier.Rigid>().iterator(),
                                        new ObjectArrayList<JigsawJunction>().iterator()),
                                settings,
                                (x, y, z) ->
                                        new Aquifer.FluidStatus(
                                                settings.seaLevel(), settings.defaultFluid()),
                                Blender.empty()));
        generator.createBiomes(random, Blender.empty(), level.structureManager(), chunk).join();
        generator.fillFromNoise(Blender.empty(), random, level.structureManager(), chunk).join();
        var biomeManager =
                new BiomeManager(
                        (x, y, z) ->
                                generator.getBiomeSource().getNoiseBiome(x, y, z, random.sampler()),
                        BiomeManager.obfuscateSeed(SEEDS[0]));
        generator.buildSurface(
                chunk,
                new WorldGenerationContext(generator, HEIGHT),
                random,
                level.structureManager(),
                biomeManager,
                biomes,
                Blender.empty());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                helper.assertTrue(
                        chunk.getBlockState(new BlockPos(x, 0, z)).is(Blocks.BEDROCK),
                        "Generated cavern floor must have bedrock after surface generation");
                helper.assertTrue(
                        chunk.getBlockState(new BlockPos(x, 255, z)).is(Blocks.BEDROCK),
                        "Generated cavern roof must have bedrock after surface generation");
            }
        }
        helper.succeed();
    }

    private static NoiseBasedChunkGenerator generator(GameTestHelper helper) {
        // GameTestServer creates only vanilla dimensions; use the actual loaded cavern settings
        // in an isolated generator so tests never require or alter a development save.
        var registries = helper.getLevel().registryAccess();
        var biome =
                registries
                        .registryOrThrow(Registries.BIOME)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.BIOME, DarkCaverns.id("rocky_caverns")));
        var settings =
                registries
                        .registryOrThrow(Registries.NOISE_SETTINGS)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.NOISE_SETTINGS, DarkCaverns.id("dark_caverns")));
        return new NoiseBasedChunkGenerator(new FixedBiomeSource(biome), settings);
    }

    private static Sample sample(
            GameTestHelper helper, NoiseBasedChunkGenerator generator, RandomState random) {
        Sample sample = new Sample();
        // Sample a kilometre-wide grid on both sides of zero, off the 4-block interpolation
        // lattice.
        for (int x = -509; x < 515; x += 32) {
            for (int z = -507; z < 517; z += 32) {
                var column = generator.getBaseColumn(x, z, HEIGHT, random);
                int air = 0;
                int gap = 0;
                int maxGap = 0;
                for (int y = 0; y < 256; y++) {
                    var block = column.getBlock(y);
                    helper.assertFalse(
                            block.is(Blocks.WATER), "Unexpected water in cavern terrain");
                    if (y <= 5 || y >= 248) {
                        helper.assertFalse(
                                block.isAir() || !block.getFluidState().isEmpty(),
                                "An opening breached the solid floor or roof shell");
                    }
                    if (!block.getFluidState().isEmpty()) {
                        helper.assertTrue(
                                block.is(Blocks.LAVA) && y < 11,
                                "Noise terrain fluid escaped the lower lava boundary");
                    }
                    sample.fingerprint = 31 * sample.fingerprint + (block.isAir() ? 1 : 0);
                    if (block.isAir()) {
                        air++;
                        gap++;
                        maxGap = Math.max(maxGap, gap);
                        if (y < 48) sample.lowerAir++;
                        if (y >= 64 && y < 192) sample.middleAir++;
                        if (y >= 208) sample.upperAir++;
                    } else {
                        gap = 0;
                    }
                }
                sample.air += air;
                sample.maxGap = Math.max(sample.maxGap, maxGap);
                if (maxGap >= 50) sample.vaultColumns++;
                if (maxGap > 180) sample.tallRiftColumns++;
                if (air < 48) sample.denseColumns++;
            }
        }
        return sample;
    }

    private static void writeSlice(
            NoiseBasedChunkGenerator generator, RandomState random, long seed) throws IOException {
        // Reproducible X/Y sections at Z=0. Noise and fluids only, before surfaces or decoration.
        BufferedImage image = new BufferedImage(1024, 256, BufferedImage.TYPE_INT_RGB);
        for (int x = -512; x < 512; x++) {
            var column = generator.getBaseColumn(x, 0, HEIGHT, random);
            for (int y = 0; y < 256; y++) {
                var block = column.getBlock(y);
                int color = block.isAir() ? 0x111827 : block.is(Blocks.LAVA) ? 0xf97316 : 0xa8a29e;
                image.setRGB(x + 512, 255 - y, color);
            }
        }
        Path directory = Path.of("../build/reports/terrain");
        Files.createDirectories(directory);
        ImageIO.write(image, "png", directory.resolve("seed-" + seed + ".png").toFile());
    }

    private static final class Sample {
        int air;
        int lowerAir;
        int middleAir;
        int upperAir;
        int vaultColumns;
        int denseColumns;
        int tallRiftColumns;
        int maxGap;
        long fingerprint;
    }
}
