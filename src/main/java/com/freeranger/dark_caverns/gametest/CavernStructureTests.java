package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernFloorFinder;
import com.freeranger.dark_caverns.generation.CavernsJigsawStructure;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CavernStructureTests {
    private CavernStructureTests() {}

    @GameTest(template = "sacred_torch")
    public static void structuresNeedSupportedDryFootprintsAndHeadroom(GameTestHelper helper) {
        BlockState[] blocks = room(40, 56);
        var column = new NoiseColumn(0, blocks);
        var footprint = new ArrayList<>(List.of(column, column, column));
        var floor = CavernFloorFinder.find(footprint, 35, 60, 10, RandomSource.create(0));
        helper.assertTrue(
                floor.isPresent() && floor.getAsInt() >= 40 && floor.getAsInt() <= 41,
                "Structure ground must align to the shelf, not 15 blocks below it");
        helper.assertTrue(
                CavernFloorFinder.find(footprint, 35, 60, 18, RandomSource.create(0)).isEmpty(),
                "A tall structure cannot fit in a low cave");
        BlockState[] obstructed = blocks.clone();
        obstructed[45] = CustomBlocks.CARFSTONE.get().defaultBlockState();
        footprint.set(2, new NoiseColumn(0, obstructed));
        helper.assertTrue(
                CavernFloorFinder.find(footprint, 35, 60, 10, RandomSource.create(0)).isEmpty(),
                "Clearance must cover the complete footprint, not just its center");
        BlockState[] unsupported = blocks.clone();
        Arrays.fill(unsupported, 25, 41, Blocks.AIR.defaultBlockState());
        footprint.set(2, new NoiseColumn(0, unsupported));
        helper.assertTrue(
                CavernFloorFinder.find(footprint, 35, 60, 10, RandomSource.create(0)).isEmpty(),
                "A thin or unsupported shelf cannot carry the template");
        BlockState[] wet = blocks.clone();
        wet[40] = Blocks.LAVA.defaultBlockState();
        footprint.set(2, new NoiseColumn(0, wet));
        helper.assertTrue(
                CavernFloorFinder.find(footprint, 35, 60, 10, RandomSource.create(0)).isEmpty(),
                "Structures must not start on lava");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 600)
    public static void registeredCavernStructuresFitGeneratedTerrain(GameTestHelper helper) {
        String[] names = {"sacred_torch", "territory_marker", "shroomie_house"};
        String[] biomeNames = {"molten_depths", "molten_depths", "glimmershroom_forest"};
        var registries = helper.getLevel().registryAccess();
        var settings =
                registries
                        .registryOrThrow(Registries.NOISE_SETTINGS)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.NOISE_SETTINGS, DarkCaverns.id("dark_caverns")));
        for (int i = 0; i < names.length; i++) {
            var biome =
                    registries
                            .registryOrThrow(Registries.BIOME)
                            .getHolderOrThrow(
                                    ResourceKey.create(
                                            Registries.BIOME, DarkCaverns.id(biomeNames[i])));
            var generator = new NoiseBasedChunkGenerator(new FixedBiomeSource(biome), settings);
            var random =
                    RandomState.create(
                            settings.value(), registries.lookupOrThrow(Registries.NOISE), 8675309);
            var structure =
                    registries
                            .registryOrThrow(Registries.STRUCTURE)
                            .getOrThrow(
                                    ResourceKey.create(
                                            Registries.STRUCTURE, DarkCaverns.id(names[i])));
            int placed = 0;
            for (int cx = -12; cx <= 12; cx += 3) {
                var context =
                        new Structure.GenerationContext(
                                registries,
                                generator,
                                generator.getBiomeSource(),
                                random,
                                helper.getLevel().getStructureManager(),
                                8675309,
                                new ChunkPos(cx, 0),
                                TerrainTestVolume.HEIGHT,
                                holder -> holder.is(biome.unwrapKey().orElseThrow()));
                var stub = structure.findValidGenerationPoint(context);
                if (stub.isEmpty()) continue;
                placed++;
                var bounds = stub.get().getPiecesBuilder().getBoundingBox();
                helper.assertTrue(
                        bounds.minY() >= 15 && bounds.maxY() < 236,
                        "Structure escaped the usable cavern range");
                for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
                    for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                        var column =
                                generator.getBaseColumn(x, z, TerrainTestVolume.HEIGHT, random);
                        helper.assertTrue(
                                CavernFloorFinder.foundationDepth(column, bounds.minY()) > 0,
                                "Structure has no dry foundation within four blocks");
                        for (int y = bounds.minY() + 1; y <= bounds.maxY() + 1; y++) {
                            helper.assertTrue(
                                    column.getBlock(y).isAir(),
                                    "Structure would intersect the cavern ceiling or wall");
                        }
                    }
                }
            }
            DarkCaverns.LOGGER.info(
                    "Terrain structure {}: {} supported placements in 9 candidate chunks",
                    names[i],
                    placed);
            helper.assertTrue(placed > 0, "No valid generated location for " + names[i]);
        }
        helper.succeed();
    }

    private static BlockState[] room(int floor, int ceiling) {
        BlockState[] blocks = new BlockState[256];
        Arrays.fill(blocks, CustomBlocks.CARFSTONE.get().defaultBlockState());
        Arrays.fill(blocks, floor + 1, ceiling, Blocks.AIR.defaultBlockState());
        return blocks;
    }

    @GameTest(template = "sacred_torch")
    public static void structureFoundationsAreShortAndPreserveFluids(GameTestHelper helper) {
        var biome =
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.BIOME, DarkCaverns.id("rocky_caverns")));
        var stone = CustomBlocks.CARFSTONE.get().defaultBlockState();
        BlockPos floor = new BlockPos(0, 40, 0);
        for (int depth : new int[] {2, 4, 6}) {
            Map<BlockPos, BlockState> blocks = new HashMap<>();
            blocks.put(floor, CustomBlocks.GLIMMERGRASS_BLOCK.get().defaultBlockState());
            var world =
                    TerrainTestWorld.create(
                            pos ->
                                    blocks.getOrDefault(
                                            pos,
                                            pos.getY() <= 40 - depth
                                                    ? stone
                                                    : Blocks.AIR.defaultBlockState()),
                            blocks::put,
                            pos -> pos.getY() >= 0 && pos.getY() < 256,
                            biome,
                            0);
            CavernsJigsawStructure.fillFoundation(world, floor);
            helper.assertTrue(
                    blocks.size() == (depth <= 4 ? depth : 1),
                    "Foundation exceeded the supported four-block depth");
            if (depth <= 4)
                helper.assertTrue(
                        world.getBlockState(floor.below()).is(CustomBlocks.CARFSTONE.get()),
                        "Grass-topped structures need stone foundations");
            blocks.clear();
            blocks.put(floor, stone);
            blocks.put(floor.below(), Blocks.LAVA.defaultBlockState());
            CavernsJigsawStructure.fillFoundation(world, floor);
            helper.assertTrue(
                    blocks.size() == 2 && world.getBlockState(floor.below()).is(Blocks.LAVA),
                    "Foundation overwrote fluid");
        }
        helper.succeed();
    }
}
