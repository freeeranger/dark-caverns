package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlockTags;
import com.freeranger.dark_caverns.registry.CustomBlocks;
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

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_slow",
            template = "sacred_torch",
            timeoutTicks = 1200)
    public static void carvedAndDecoratedTerrainRetainsRoutes(GameTestHelper helper) {
        long[] seeds = {0, 8675309, -7046029254386353131L};
        String[] biomes = {"rocky_caverns", "molten_depths", "glimmershroom_forest"};
        for (int seedIndex : new int[] {1}) {
            var volume = new TerrainTestVolume(helper, seeds[seedIndex], biomes[seedIndex]);
            byte[] before = volume.snapshot();
            volume.carve();
            byte[] carved = volume.snapshot();
            volume.feature("luminite_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 0);
            volume.feature("platinum_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 7);
            if (seedIndex == 1)
                volume.feature(
                        "hellstone_ore_feature", GenerationStep.Decoration.UNDERGROUND_ORES, 8);
            if (seedIndex == 1)
                volume.feature(
                        "ashy_molten_carfstone_patch",
                        GenerationStep.Decoration.UNDERGROUND_ORES,
                        10);
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
        helper.succeed();
    }

    static boolean canStand(byte[] blocks, int index) {
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
            volume.feature(
                    "ashy_charred_grass_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 6);
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
        int ashyGround = 0;
        int ashyPlants = 0;
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
            if (state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())) ashyGround++;
            if (state.is(CustomBlocks.ASHY_CHARRED_GRASS.get())) ashyPlants++;
            if (canStand(geometry, i) && volume.get(pos.below()).is(groundTag)) spawnFloors++;
        }
        long plants =
                volume.featureWrites.entrySet().stream()
                        .filter(
                                e ->
                                        e.getValue().is(CustomBlocks.CHARRED_GRASS.get())
                                                || e.getValue()
                                                        .is(CustomBlocks.ASHY_CHARRED_GRASS.get())
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
            if (state.is(CustomBlocks.ASHY_CHARRED_GRASS.get())) {
                helper.assertTrue(
                        volume.get(entry.getKey().below())
                                .is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get()),
                        "Ashy Charred Grass escaped its ashy floor");
            }
        }
        helper.assertTrue(luminite > 0, "Generated terrain contains no Luminite");
        helper.assertTrue(platinum > 0, "Terrain must retain mineable platinum");
        if (biomeIndex == 1)
            helper.assertTrue(hellstone > 0, "Molten surfaces must retain hellstone");
        if (biomeIndex == 1) {
            helper.assertTrue(ashyGround > 0, "Molten Depths generated no ash fields");
            helper.assertTrue(ashyPlants > 0, "Ash fields generated without vegetation");
        } else {
            helper.assertTrue(
                    ashyGround == 0 && ashyPlants == 0,
                    "Ash-field worldgen leaked outside Molten Depths");
        }
        if (biomeIndex != 0) helper.assertTrue(plants > 0, "Floor-based vegetation did not place");
        helper.assertTrue(spawnFloors > 0, "Biome creatures have no tagged, walkable ground");
        DarkCaverns.LOGGER.info(
                "Terrain content seed {}: luminite={}, platinum={}, hellstone={}, ash={},"
                        + " ashyPlants={}, plants={}, spawnFloors={}",
                volume.seed,
                luminite,
                platinum,
                hellstone,
                ashyGround,
                ashyPlants,
                plants,
                spawnFloors);
    }
}
