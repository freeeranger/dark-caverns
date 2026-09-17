package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.BiomeTransition;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BiomeTransitionTests {
    private static final String[] NAMES = {
        "rocky_caverns", "glimmershroom_forest", "molten_depths", "tangled_hallow"
    };
    private static final int[] COLORS = {0x85899b, 0x68bdec, 0xe27e48, 0x819744};

    private BiomeTransitionTests() {}

    @GameTest(template = "sacred_torch")
    public static void transitionWeightsAreContinuousAndKeepCores(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
        var biomes =
                Arrays.stream(NAMES)
                        .map(
                                name ->
                                        registry.getHolderOrThrow(
                                                ResourceKey.create(
                                                        Registries.BIOME, DarkCaverns.id(name))))
                        .toList();
        for (int left = 0; left < 4; left++) {
            for (int right = left + 1; right < 4; right++) {
                Holder<Biome> a = biomes.get(left);
                Holder<Biome> b = biomes.get(right);
                var transition = new BiomeTransition(pos -> pos.getX() < 0 ? a : b);
                double previous = transition.weights(-64, 0).cover(right);
                for (int x = -63; x <= 64; x++) {
                    var weight = transition.weights(x, 0);
                    double next = weight.cover(right);
                    helper.assertTrue(
                            next >= previous - 1e-9 && next - previous < 0.1,
                            "Transition jumped or reversed at x=" + x);
                    helper.assertTrue(
                            weight.chance(right, true) <= next + 1e-9,
                            "Core feature must taper faster than small decoration");
                    previous = next;
                }
                helper.assertTrue(
                        transition.weights(-40, 0).chance(left, true) == 1
                                && transition.weights(40, 0).chance(right, true) == 1,
                        "Pure biome interiors must retain full feature density");
                helper.assertTrue(
                        transition.weights(-40, 0).cover(right) == 0,
                        "Transition influence escaped its finite band");
                // Independent caches and negative lattice coordinates must agree exactly.
                var independentlySampled = new BiomeTransition(pos -> pos.getX() < 0 ? a : b);
                for (int x = 32; x >= -32; x--)
                    helper.assertTrue(
                            transition.weights(x, -17).equals(independentlySampled.weights(x, -17)),
                            "Sampling order changed transition weights");
            }
        }
        var contact = new BiomeTransition.Weights(0.5, 0.5);
        helper.assertTrue(
                contact.material(0.5) == BiomeTransition.ROCKY
                        && contact.chance(1, true) == 0
                        && contact.chance(2, true) == 0,
                "Forest/molten contact needs exposed rock and no core features");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 2400)
    public static void realBiomeBoundariesBlendWithoutChangingTerrain(GameTestHelper helper)
            throws IOException {
        NoiseBasedChunkGenerator configuredGenerator;
        // GameTestServer loads only its test dimension. Decode the actual datapack generator.
        try (var reader =
                helper.getLevel()
                        .getServer()
                        .getResourceManager()
                        .getResourceOrThrow(DarkCaverns.id("dimension/dark_caverns.json"))
                        .openAsReader()) {
            configuredGenerator =
                    (NoiseBasedChunkGenerator)
                            ChunkGenerator.CODEC
                                    .parse(
                                            RegistryOps.create(
                                                    JsonOps.INSTANCE,
                                                    helper.getLevel().registryAccess()),
                                            JsonParser.parseReader(reader)
                                                    .getAsJsonObject()
                                                    .get("generator"))
                                    .getOrThrow();
        }
        BiomeSource source = configuredGenerator.getBiomeSource();
        var sortedFeatures =
                FeatureSorter.buildFeaturesPerStep(
                        source.possibleBiomes().stream().toList(),
                        biome -> biome.value().getGenerationSettings().features(),
                        true);
        long[] seeds = {0, 8675309, -7046029254386353131L, 0, 8675309, -7046029254386353131L};
        int[][] pairs = {{0, 1}, {0, 2}, {1, 2}, {0, 3}, {1, 3}, {2, 3}};
        BufferedImage preview = new BufferedImage(792, 1920, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = preview.createGraphics();
        graphics.setColor(new Color(0x101723));
        graphics.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        StringBuilder report =
                new StringBuilder(
                        "seed,pair,min_x,min_z,transition_floors,changed_floors,plants,spillover_plants\n");
        for (int i = 0; i < seeds.length; i++) {
            var state =
                    RandomState.create(
                            configuredGenerator.generatorSettings().value(),
                            helper.getLevel().registryAccess().lookupOrThrow(Registries.NOISE),
                            seeds[i]);
            var manager =
                    new BiomeManager(
                            (x, y, z) -> source.getNoiseBiome(x, y, z, state.sampler()),
                            BiomeManager.obfuscateSeed(seeds[i]));
            BlockPos boundary = findBoundary(helper, manager, pairs[i][0], pairs[i][1]);
            var volume =
                    new TerrainTestVolume(
                            helper,
                            seeds[i],
                            source,
                            Math.floorDiv(boundary.getX(), 16) - 4,
                            Math.floorDiv(boundary.getZ(), 16) - 4,
                            false);
            var blend = new BiomeTransition(volume.biomeManager::getBiome);
            int transitions = 0;
            int changed = 0;
            var pos = new BlockPos.MutableBlockPos();
            for (int x = volume.minX; x < volume.minX + 128; x++) {
                for (int z = volume.minZ; z < volume.minZ + 128; z++) {
                    var weights = blend.weights(x, z);
                    int nativeBiome =
                            BiomeTransition.identity(manager.getBiome(new BlockPos(x, 128, z)));
                    var column =
                            volume.generator.getBaseColumn(
                                    x, z, TerrainTestVolume.HEIGHT, volume.random);
                    for (int y = 0; y < 256; y++) {
                        BlockState actual = volume.get(pos.set(x, y, z));
                        BlockState density = column.getBlock(y);
                        helper.assertTrue(
                                actual.isAir() == density.isAir()
                                        && actual.getFluidState().equals(density.getFluidState()),
                                "Surface blending changed geometry or fluids");
                        if (y == 0 || y == 255)
                            helper.assertTrue(
                                    actual.is(Blocks.BEDROCK), "Transition breached bedrock");
                        if (y < 254
                                && surface(actual)
                                && volume.get(pos.set(x, y + 1, z)).isAir()
                                && weights.cover(nativeBiome) < 0.999) {
                            transitions++;
                            if (material(actual) != nativeBiome) changed++;
                        }
                    }
                }
            }
            helper.assertTrue(
                    transitions > 100 && changed > 50,
                    "Real biome boundary did not produce mixed floor materials: "
                            + transitions
                            + "/"
                            + changed);
            // Generate every chunk in reverse order; compare actual materials, not just occupancy.
            if (i == 0) {
                var reverse =
                        new TerrainTestVolume(
                                helper, seeds[i], source, volume.minX / 16, volume.minZ / 16, true);
                for (int x = volume.minX; x < volume.minX + 128; x++)
                    for (int z = volume.minZ; z < volume.minZ + 128; z++)
                        for (int y = 0; y < 256; y++) {
                            pos.set(x, y, z);
                            if (!volume.get(pos).equals(reverse.get(pos)))
                                helper.fail("Chunk order changed surface material at " + pos);
                        }
            }
            drawPreview(graphics, volume, blend, i, seeds[i], pairs[i]);
            volume.carve();
            for (String feature :
                    new String[] {
                        "glimmergrass_patch",
                        "shroom_patch",
                        "charred_grass_patch",
                        "scorched_berry_bush_patch",
                        "mighty_undersprouts_patch",
                        "tall_undersprouts_patch",
                        "undersprouts_patch"
                    }) {
                var placed =
                        helper.getLevel()
                                .registryAccess()
                                .registryOrThrow(Registries.PLACED_FEATURE)
                                .getOrThrow(
                                        ResourceKey.create(
                                                Registries.PLACED_FEATURE,
                                                DarkCaverns.id(feature)));
                volume.feature(
                        feature,
                        GenerationStep.Decoration.VEGETAL_DECORATION,
                        sortedFeatures
                                .get(GenerationStep.Decoration.VEGETAL_DECORATION.ordinal())
                                .indexMapping()
                                .applyAsInt(placed));
            }
            int plants = 0;
            int spillover = 0;
            for (var entry : volume.featureWrites.entrySet()) {
                BlockState block = entry.getValue();
                boolean forest =
                        block.is(CustomBlocks.GLIMMERGRASS.get())
                                || block.is(CustomBlocks.GLIMMERSHROOM.get());
                boolean molten =
                        block.is(CustomBlocks.CHARRED_GRASS.get())
                                || block.is(CustomBlocks.SCORCHED_BERRY_BUSH.get());
                boolean tallHallow = block.is(CustomBlocks.TALL_UNDERSPROUTS.get());
                boolean hallow =
                        block.is(CustomBlocks.UNDERSPROUTS.get())
                                || block.is(CustomBlocks.MIGHTY_UNDERSPROUTS.get())
                                || tallHallow;
                if (!forest && !molten && !hallow) continue;
                plants++;
                BlockPos groundPos = entry.getKey().below();
                if (tallHallow && block.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                    groundPos = groundPos.below();
                }
                BlockState ground = volume.get(groundPos);
                helper.assertTrue(
                        ground.is(
                                hallow
                                        ? CustomBlocks.OVERGROWN_CARFSTONE.get()
                                        : forest
                                                ? CustomBlocks.GLIMMERGRASS_BLOCK.get()
                                                : CustomBlocks.MOLTEN_CARFSTONE.get()),
                        "Transition plant escaped its matching surface patch");
                int nativeBiome = BiomeTransition.identity(volume.world.getBiome(entry.getKey()));
                if (nativeBiome != (hallow ? 3 : forest ? 1 : 2)) spillover++;
            }
            helper.assertTrue(plants > 0, "Mixed-biome decoration produced no plants");
            helper.assertTrue(
                    spillover > 0, "Small plants failed to cross the categorical biome boundary");
            DarkCaverns.LOGGER.info(
                    "Biome transition seed {} pair {}-{} at {},{}: floors={}, changed={},"
                            + " plants={}, spillover={}",
                    seeds[i],
                    pairs[i][0],
                    pairs[i][1],
                    volume.minX,
                    volume.minZ,
                    transitions,
                    changed,
                    plants,
                    spillover);
            report.append(seeds[i])
                    .append(',')
                    .append(pairs[i][0])
                    .append('-')
                    .append(pairs[i][1])
                    .append(',')
                    .append(volume.minX)
                    .append(',')
                    .append(volume.minZ)
                    .append(',')
                    .append(transitions)
                    .append(',')
                    .append(changed)
                    .append(',')
                    .append(plants)
                    .append(',')
                    .append(spillover)
                    .append('\n');
        }
        graphics.dispose();
        Path directory = Path.of("../build/reports/terrain");
        Files.createDirectories(directory);
        javax.imageio.ImageIO.write(
                preview, "png", directory.resolve("biome-transitions.png").toFile());
        Files.writeString(directory.resolve("biome-transitions.csv"), report);
        helper.succeed();
    }

    private static BlockPos findBoundary(
            GameTestHelper helper, BiomeManager manager, int a, int b) {
        for (int radius = 128; radius <= 2048; radius *= 2) {
            for (int x = -radius; x < radius; x += 16) {
                for (int z = -radius; z < radius; z += 16) {
                    if (BiomeTransition.identity(manager.getBiome(new BlockPos(x, 128, z))) != a)
                        continue;
                    if (BiomeTransition.identity(manager.getBiome(new BlockPos(x + 16, 128, z)))
                                    == b
                            || BiomeTransition.identity(
                                            manager.getBiome(new BlockPos(x, 128, z + 16)))
                                    == b) return new BlockPos(x + 8, 128, z + 8);
                }
            }
        }
        helper.fail("Could not locate real biome boundary " + a + "-" + b);
        throw new AssertionError();
    }

    private static boolean surface(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }

    private static int material(BlockState state) {
        return state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                ? 2
                : state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                        ? 1
                        : state.is(CustomBlocks.OVERGROWN_CARFSTONE.get()) ? 3 : 0;
    }

    private static void drawPreview(
            Graphics2D g,
            TerrainTestVolume volume,
            BiomeTransition blend,
            int row,
            long seed,
            int[] pair) {
        int top = row * 320;
        g.setColor(new Color(0xe5edf5));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g.drawString(NAMES[pair[0]] + " / " + NAMES[pair[1]] + "   seed " + seed, 8, top + 19);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("Biome IDs", 8, top + 39);
        g.drawString("Blended biome influence", 272, top + 39);
        g.drawString("Actual cavern floors below Y 184", 536, top + 39);
        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                int bx = x + volume.minX;
                int bz = z + volume.minZ;
                var weights = blend.weights(bx, bz);
                g.setColor(
                        new Color(
                                COLORS[
                                        BiomeTransition.identity(
                                                volume.world.getBiome(
                                                        new BlockPos(bx, 128, bz)))]));
                g.fillRect(8 + x * 2, top + 48 + z * 2, 2, 2);
                double f = weights.cover(1);
                double m = weights.cover(2);
                double h = weights.cover(3);
                int color = 0;
                for (int shift : new int[] {0, 8, 16})
                    color |=
                            (int)
                                            (((COLORS[0] >> shift) & 255) * (1 - f - m - h)
                                                    + ((COLORS[1] >> shift) & 255) * f
                                                    + ((COLORS[2] >> shift) & 255) * m
                                                    + ((COLORS[3] >> shift) & 255) * h)
                                    << shift;
                g.setColor(new Color(color));
                g.fillRect(272 + x * 2, top + 48 + z * 2, 2, 2);
                int floorColor = 0x172234;
                for (int y = 184; y > 11; y--) {
                    BlockState state = volume.get(new BlockPos(bx, y, bz));
                    if (surface(state) && volume.get(new BlockPos(bx, y + 1, bz)).isAir()) {
                        floorColor = COLORS[material(state)];
                        break;
                    }
                }
                g.setColor(new Color(floorColor));
                g.fillRect(536 + x * 2, top + 48 + z * 2, 2, 2);
            }
        }
    }
}
