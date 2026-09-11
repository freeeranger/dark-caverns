package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlockTags;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TerrainIntegrationTests {
    private TerrainIntegrationTests() {}

    @GameTest(template = "sacred_torch", timeoutTicks = 1200)
    public static void carvedAndDecoratedTerrainRetainsRoutes(GameTestHelper helper)
            throws IOException {
        long[] seeds = {0, 8675309, -7046029254386353131L};
        String[] biomes = {"rocky_caverns", "molten_depths", "glimmershroom_forest"};
        StringBuilder report =
                new StringBuilder(
                        "seed,biome,air_fraction,carver_added_fraction,formation_solid_fraction,largest_air_fraction,walkable,largest_walk,route_height,floating_stone\n");
        for (int seedIndex = 0; seedIndex < seeds.length; seedIndex++) {
            var volume = new TerrainTestVolume(helper, seeds[seedIndex], biomes[seedIndex]);
            byte[] before = volume.snapshot();
            volume.carve();
            byte[] carved = volume.snapshot();
            volume.feature("luminite_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 0);
            volume.feature("platinum_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 7);
            if (seedIndex == 1)
                volume.feature(
                        "hellstone_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 8);
            if (seedIndex < 2)
                volume.formations(seedIndex == 0 ? "spike_feature" : "molten_spike_feature");
            byte[] after = volume.snapshot();
            int carvedBlocks = 0;
            int formations = 0;
            int originalWalkable = 0;
            int carverLostFloors = 0;
            int formationLostFloors = 0;
            for (int i = 0; i < after.length; i++) {
                if (before[i] == 1 && carved[i] != 1) carvedBlocks++;
                if (carved[i] == 0 && after[i] == 1) formations++;
                if (canStand(before, i)) {
                    originalWalkable++;
                    if (!canStand(carved, i)) carverLostFloors++;
                }
                if (canStand(carved, i) && !canStand(after, i)) formationLostFloors++;
                if (after[i] == 2)
                    helper.assertTrue(
                            i / (TerrainTestVolume.WIDTH * TerrainTestVolume.WIDTH) < 11,
                            "Carving moved the lava boundary above Y 11");
            }
            var stats = TerrainTopology.measure(after);
            double carverFraction = (double) carvedBlocks / after.length;
            double formationFraction = (double) formations / after.length;
            double connectedFraction = (double) stats.largestAir / stats.air;
            DarkCaverns.LOGGER.info(
                    "Terrain integration seed {} ({}): carverAdded={}, formationSolid={},"
                            + " connectedAir={}, walkable={}, largestWalk={}, routeHeight={},"
                            + " floatingStone={}",
                    seeds[seedIndex],
                    biomes[seedIndex],
                    carverFraction,
                    formationFraction,
                    connectedFraction,
                    stats.walkable,
                    stats.largestWalk,
                    stats.routeHeight,
                    stats.floatingStone);
            report.append(seeds[seedIndex])
                    .append(',')
                    .append(biomes[seedIndex])
                    .append(',')
                    .append((double) stats.air / after.length)
                    .append(',')
                    .append(carverFraction)
                    .append(',')
                    .append(formationFraction)
                    .append(',')
                    .append(connectedFraction)
                    .append(',')
                    .append(stats.walkable)
                    .append(',')
                    .append(stats.largestWalk)
                    .append(',')
                    .append(stats.routeHeight)
                    .append(',')
                    .append(stats.floatingStone)
                    .append('\n');
            writeSection(before, after, seeds[seedIndex]);
            DarkCaverns.LOGGER.info(
                    "Terrain route impact seed {}: originalFloors={}, carverLost={},"
                            + " formationLost={}",
                    seeds[seedIndex],
                    originalWalkable,
                    carverLostFloors,
                    formationLostFloors);
            helper.assertTrue(
                    carverLostFloors < originalWalkable * 0.1,
                    "Carver removed too many usable floor positions");
            helper.assertTrue(
                    formationLostFloors < originalWalkable * 0.1,
                    "Formations obstructed too many usable floor positions");
            helper.assertTrue(
                    carverFraction > 0 && carverFraction < 0.04,
                    "Carver must add detail without replacing the density terrain");
            helper.assertTrue(formationFraction < 0.04, "Formations consumed too much open volume");
            if (seedIndex < 2)
                helper.assertTrue(
                        formations > 0, "Registered formation placement produced no formations");
            helper.assertTrue(
                    connectedFraction > 0.75,
                    "Most sampled air should belong to a connected cavern network");
            helper.assertTrue(
                    stats.largestWalk > 1000 && stats.routeHeight >= 24,
                    "Missing substantial walking routes between elevations");
            helper.assertTrue(
                    stats.floatingStone < after.length * 0.01,
                    "Too much terrain is detached from walls, supports and the shell");
            verifyContent(helper, volume, after, seedIndex);
            for (int x = TerrainTestVolume.MIN;
                    x < TerrainTestVolume.MIN + TerrainTestVolume.WIDTH;
                    x++) {
                for (int z = TerrainTestVolume.MIN;
                        z < TerrainTestVolume.MIN + TerrainTestVolume.WIDTH;
                        z++) {
                    helper.assertTrue(
                            volume.get(new BlockPos(x, 0, z)).is(Blocks.BEDROCK)
                                    && volume.get(new BlockPos(x, 255, z)).is(Blocks.BEDROCK),
                            "Carving or formations breached bedrock");
                }
            }
        }
        Files.writeString(Path.of("../build/reports/terrain/integration.csv"), report);
        helper.succeed();
    }

    private static void writeSection(byte[] before, byte[] after, long seed) throws IOException {
        int width = TerrainTestVolume.WIDTH;
        BufferedImage image = new BufferedImage(width * 2 + 4, 256, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + width / 2) * width + x;
                image.setRGB(x, 255 - y, color(before[index]));
                image.setRGB(
                        x + width + 4,
                        255 - y,
                        after[index] == 1 && before[index] == 0 ? 0x38bdf8 : color(after[index]));
            }
        }
        Path directory = Path.of("../build/reports/terrain");
        Files.createDirectories(directory);
        ImageIO.write(image, "png", directory.resolve("integrated-" + seed + ".png").toFile());
    }

    private static boolean canStand(byte[] blocks, int index) {
        int layer = TerrainTestVolume.WIDTH * TerrainTestVolume.WIDTH;
        return index >= layer
                && index < blocks.length - layer
                && blocks[index] == 0
                && blocks[index + layer] == 0
                && blocks[index - layer] == 1;
    }

    private static void verifyContent(
            GameTestHelper helper, TerrainTestVolume volume, byte[] geometry, int biomeIndex) {
        if (biomeIndex == 1) {
            volume.feature("charred_grass_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 1);
            volume.feature(
                    "scorched_berry_bush_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 2);
        } else if (biomeIndex == 2) {
            volume.feature("glimmergrass_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 2);
            volume.feature("shroom_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 3);
        }
        var groundTag =
                biomeIndex == 0
                        ? CustomBlockTags.ROCKY_CREATURE_SPAWNABLE_ON
                        : biomeIndex == 1
                                ? CustomBlockTags.MOLTEN_CREATURE_SPAWNABLE_ON
                                : CustomBlockTags.GLIMMERSHROOM_CREATURE_SPAWNABLE_ON;
        int spawnFloors = 0;
        int luminite = 0;
        int platinum = 0;
        int hellstone = 0;
        int width = TerrainTestVolume.WIDTH;
        var pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < geometry.length; i++) {
            pos.set(
                    i % width + TerrainTestVolume.MIN,
                    i / (width * width),
                    i / width % width + TerrainTestVolume.MIN);
            var state = volume.get(pos);
            if (state.is(CustomBlocks.LUMINITE_ORE.get())) luminite++;
            if (state.is(CustomBlocks.PLATINUM_ORE.get())) platinum++;
            if (state.is(CustomBlocks.HELLSTONE_ORE.get())) hellstone++;
            if (canStand(geometry, i) && volume.get(pos.below()).is(groundTag)) spawnFloors++;
        }
        long plants =
                volume.featureWrites.entrySet().stream()
                        .filter(
                                e ->
                                        e.getValue().is(CustomBlocks.CHARRED_GRASS.get())
                                                || e.getValue()
                                                        .is(CustomBlocks.SCORCHED_BERRY_BUSH.get())
                                                || e.getValue().is(CustomBlocks.GLIMMERGRASS.get())
                                                || e.getValue()
                                                        .is(CustomBlocks.GLIMMERSHROOM.get()))
                        .count();
        for (var entry : volume.featureWrites.entrySet()) {
            var state = entry.getValue();
            if (state.is(CustomBlocks.CHARRED_GRASS.get())
                    || state.is(CustomBlocks.SCORCHED_BERRY_BUSH.get())) {
                helper.assertTrue(
                        volume.get(entry.getKey().below()).is(CustomBlocks.MOLTEN_CARFSTONE.get()),
                        "Molten vegetation lost its floor");
            }
            if (state.is(CustomBlocks.GLIMMERGRASS.get())
                    || state.is(CustomBlocks.GLIMMERSHROOM.get())) {
                helper.assertTrue(
                        volume.get(entry.getKey().below())
                                .is(CustomBlocks.GLIMMERGRASS_BLOCK.get()),
                        "Forest vegetation lost its floor");
            }
        }
        helper.assertTrue(
                luminite > 0 && platinum > 0, "Terrain must retain mineable luminite and platinum");
        if (biomeIndex == 1)
            helper.assertTrue(hellstone > 0, "Molten surfaces must retain hellstone");
        if (biomeIndex != 0) helper.assertTrue(plants > 0, "Floor-based vegetation did not place");
        helper.assertTrue(
                spawnFloors > 1000, "Biome creatures need sufficient tagged, walkable ground");
        helper.assertTrue(
                volume.biome.value().getBackgroundMusic().isPresent(),
                "Biome music must remain assigned");
        DarkCaverns.LOGGER.info(
                "Terrain content seed {}: luminite={}, platinum={}, hellstone={}, plants={},"
                        + " spawnFloors={}",
                volume.seed,
                luminite,
                platinum,
                hellstone,
                plants,
                spawnFloors);
    }

    private static int color(byte block) {
        return block == 0 ? 0x111827 : block == 2 ? 0xf97316 : 0xa8a29e;
    }
}
