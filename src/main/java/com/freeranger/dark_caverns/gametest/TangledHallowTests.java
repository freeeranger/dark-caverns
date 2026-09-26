package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TangledHallowTests {
    private static final BlockPos ORIGIN = new BlockPos(0, 40, 0);

    private TangledHallowTests() {}

    @GameTest(template = "sacred_torch")
    public static void mightyUndersproutsHarvestAndRequireHallowGround(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlock(pos.below(), CustomBlocks.OVERGROWN_CARFSTONE.get().defaultBlockState(), 3);
        BlockState plant = CustomBlocks.MIGHTY_UNDERSPROUTS.get().defaultBlockState();
        level.setBlock(pos, plant, 3);

        var drops =
                net.minecraft.world.level.block.Block.getDrops(
                        plant,
                        level,
                        pos,
                        null,
                        null,
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.SHEARS));
        helper.assertTrue(
                drops.size() == 1
                        && drops.getFirst().is(CustomBlocks.MIGHTY_UNDERSPROUTS.get().asItem()),
                "Shears should harvest Mighty Undersprouts");

        level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 3);
        helper.assertTrue(
                level.getBlockState(pos).isAir(),
                "Mighty Undersprouts remained after their Hallow ground was removed");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void undersproutsGrowAndBreakLikeTallGrass(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlock(pos.below(), CustomBlocks.OVERGROWN_CARFSTONE.get().defaultBlockState(), 3);
        BlockState shortPlant = CustomBlocks.UNDERSPROUTS.get().defaultBlockState();
        level.setBlock(pos, shortPlant, 3);

        CustomBlocks.UNDERSPROUTS
                .get()
                .performBonemeal(level, RandomSource.create(7), pos, shortPlant);
        BlockState lower = level.getBlockState(pos);
        BlockState upper = level.getBlockState(pos.above());
        helper.assertTrue(
                lower.is(CustomBlocks.TALL_UNDERSPROUTS.get())
                        && lower.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER
                        && upper.is(CustomBlocks.TALL_UNDERSPROUTS.get())
                        && upper.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER,
                "Bone meal did not grow undersprouts into a linked tall plant");

        var drops =
                net.minecraft.world.level.block.Block.getDrops(
                        upper,
                        level,
                        pos.above(),
                        null,
                        null,
                        new net.minecraft.world.item.ItemStack(
                                net.minecraft.world.item.Items.SHEARS));
        helper.assertTrue(
                drops.size() == 1
                        && drops.getFirst().is(CustomBlocks.UNDERSPROUTS.get().asItem())
                        && drops.getFirst().getCount() == 2,
                "Shearing tall undersprouts should return two regular undersprouts");

        level.destroyBlock(pos.above(), false);
        helper.assertTrue(
                level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir(),
                "Breaking one tall-undersprouts half left the other behind");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void twistwoodSupportsRenewalStrippingAndHarvesting(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos base = helper.absolutePos(new BlockPos(0, 128, 0));
        var original = new HashMap<BlockPos, BlockState>();
        // A temporary isolated test room above the test structures; restore it even on failure.
        for (BlockPos pos :
                BlockPos.betweenClosed(base.offset(-8, -1, -8), base.offset(8, 18, 8))) {
            original.put(pos.immutable(), level.getBlockState(pos));
            level.setBlock(
                    pos,
                    pos.getY() == base.getY() - 1
                            ? CustomBlocks.OVERGROWN_CARFSTONE.get().defaultBlockState()
                            : Blocks.AIR.defaultBlockState(),
                    2);
        }
        try {
            var sapling = CustomBlocks.TWISTWOOD_SAPLING.get();
            BlockState mature =
                    sapling.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.SaplingBlock.STAGE, 1);
            level.setBlock(base, mature, 2);
            sapling.performBonemeal(level, RandomSource.create(7), base, mature);
            helper.assertTrue(
                    level.getBlockState(base).is(CustomBlocks.TWISTWOOD_LOG.get()),
                    "Bone meal failed to grow a renewable twistwood tree");
            var use =
                    new net.minecraft.world.item.context.UseOnContext(
                            level,
                            null,
                            net.minecraft.world.InteractionHand.MAIN_HAND,
                            new net.minecraft.world.item.ItemStack(
                                    net.minecraft.world.item.Items.IRON_AXE),
                            new net.minecraft.world.phys.BlockHitResult(
                                    net.minecraft.world.phys.Vec3.atCenterOf(base),
                                    Direction.UP,
                                    base,
                                    false));
            var horizontal =
                    CustomBlocks.TWISTWOOD_LOG
                            .get()
                            .defaultBlockState()
                            .setValue(
                                    net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                    Direction.Axis.X);
            BlockState stripped =
                    horizontal.getToolModifiedState(
                            use, net.neoforged.neoforge.common.ItemAbilities.AXE_STRIP, true);
            helper.assertTrue(
                    stripped != null
                            && stripped.is(CustomBlocks.STRIPPED_TWISTWOOD_LOG.get())
                            && stripped.getValue(
                                            net.minecraft.world.level.block.RotatedPillarBlock.AXIS)
                                    == Direction.Axis.X,
                    "Stripping lost the log axis");
            var leaf = CustomBlocks.TWISTWOOD_LEAVES.get().defaultBlockState();
            var drops =
                    net.minecraft.world.level.block.Block.getDrops(
                            leaf,
                            level,
                            base,
                            null,
                            null,
                            new net.minecraft.world.item.ItemStack(
                                    net.minecraft.world.item.Items.SHEARS));
            helper.assertTrue(
                    drops.size() == 1
                            && drops.getFirst().is(CustomBlocks.TWISTWOOD_LEAVES.get().asItem()),
                    "Shears should yield one leaf block");
            BlockState unsupported =
                    leaf.setValue(LeavesBlock.DISTANCE, 7).setValue(LeavesBlock.PERSISTENT, false);
            BlockPos decayPos = base.offset(7, 17, 7);
            level.setBlock(decayPos, unsupported, 2);
            unsupported.randomTick(level, decayPos, RandomSource.create(7));
            helper.assertTrue(
                    level.getBlockState(decayPos).isAir(),
                    "Unsupported natural leaves did not decay");
            helper.assertTrue(
                    CustomBlocks.TWISTWOOD_LOG
                                    .get()
                                    .asItem()
                                    .getBurnTime(
                                            new net.minecraft.world.item.ItemStack(
                                                    CustomBlocks.TWISTWOOD_LOG.get()),
                                            null)
                            > 0,
                    "Twistwood is not usable as fuel");
        } finally {
            original.forEach((pos, state) -> level.setBlock(pos, state, 2));
        }
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void twistwoodTreesScaleAttachAndProtectTheirSurroundings(GameTestHelper helper) {
        Room low = new Room(helper, 8, pos -> true);
        Room tall = new Room(helper, 32, pos -> true);
        helper.assertTrue(place(low) && place(tall), "Trees failed in clear supported rooms");
        helper.assertTrue(height(tall) > height(low), "Twistwood did not respond to cavern height");
        for (Room room : new Room[] {low, tall}) verifyAttached(helper, room);
        for (BlockState obstacle :
                new BlockState[] {
                    Blocks.CHEST.defaultBlockState(),
                    Blocks.LAVA.defaultBlockState(),
                    CustomBlocks.LUMINITE_ORE.get().defaultBlockState(),
                    Blocks.BEDROCK.defaultBlockState()
                }) {
            Room blocked = new Room(helper, 32, pos -> true);
            blocked.edits.put(ORIGIN.above(), obstacle);
            var before = Map.copyOf(blocked.edits);
            helper.assertFalse(place(blocked), "Tree accepted an obstructed trunk");
            helper.assertTrue(blocked.edits.equals(before), "Failed tree left partial writes");
        }
        Room clipped = new Room(helper, 32, pos -> pos.getX() == 0 && pos.getZ() == 0);
        helper.assertFalse(place(clipped), "Tree escaped writable bounds");
        helper.assertTrue(clipped.edits.isEmpty(), "Clipped tree left a partial trunk");
        Room tiny = new Room(helper, 6, pos -> true);
        helper.assertFalse(place(tiny), "Tree filled a low walking passage");
        Room unsupported = new Room(helper, 32, pos -> true);
        unsupported.edits.put(ORIGIN.below(), Blocks.AIR.defaultBlockState());
        helper.assertFalse(place(unsupported), "Tree floated over unsupported ground");
        helper.assertTrue(
                CustomBlocks.TWISTWOOD_LOG.get().defaultBlockState().is(BlockTags.LOGS),
                "Logs cannot support leaves");
        helper.assertTrue(
                CustomBlocks.TWISTWOOD_PLANKS
                        .get()
                        .asItem()
                        .getDefaultInstance()
                        .is(ItemTags.PLANKS),
                "Planks cannot use vanilla wood recipes");
        helper.assertTrue(
                CustomBlocks.TWISTWOOD_SAPLING
                        .get()
                        .defaultBlockState()
                        .canSurvive(low.world, ORIGIN),
                "Sapling cannot grow on Hallow ground");
        helper.assertFalse(
                CustomBlocks.TWISTWOOD_LOG
                        .get()
                        .defaultBlockState()
                        .isFlammable(low.world, ORIGIN, Direction.UP),
                "Twistwood logs must not be flammable");
        helper.assertFalse(
                CustomBlocks.TWISTWOOD_PLANKS
                        .get()
                        .defaultBlockState()
                        .isFlammable(low.world, ORIGIN, Direction.UP),
                "Twistwood planks must not be flammable");
        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void giantTwistwoodTreesAreGroundedConnectedAndAtomic(GameTestHelper helper) {
        Room giant = new Room(helper, 80, pos -> true);
        helper.assertTrue(placeGiant(giant), "Giant Twistwood failed in a clear tall cavern");
        int minX = giant.edits.keySet().stream().mapToInt(BlockPos::getX).min().orElse(0);
        int maxX = giant.edits.keySet().stream().mapToInt(BlockPos::getX).max().orElse(0);
        int minZ = giant.edits.keySet().stream().mapToInt(BlockPos::getZ).min().orElse(0);
        int maxZ = giant.edits.keySet().stream().mapToInt(BlockPos::getZ).max().orElse(0);
        helper.assertTrue(height(giant) >= 48, "Giant Twistwood is not at least twice as tall");
        helper.assertTrue(
                maxX - minX >= 24 && maxZ - minZ >= 24,
                "Giant Twistwood crown is not broad enough");
        helper.assertTrue(
                Direction.Plane.HORIZONTAL.stream()
                        .allMatch(
                                direction ->
                                        giant.read(ORIGIN.relative(direction))
                                                .is(CustomBlocks.TWISTWOOD_LOG.get())),
                "Giant Twistwood is missing its stable root anchor");
        verifyAttached(helper, giant);

        Room low = new Room(helper, 48, pos -> true);
        helper.assertFalse(placeGiant(low), "Giant Twistwood accepted a cramped cavern");
        helper.assertTrue(low.edits.isEmpty(), "Rejected giant Twistwood left partial writes");

        Room blocked = new Room(helper, 80, pos -> true);
        blocked.edits.put(ORIGIN, Blocks.CHEST.defaultBlockState());
        var before = Map.copyOf(blocked.edits);
        helper.assertFalse(placeGiant(blocked), "Giant Twistwood replaced a protected block");
        helper.assertTrue(blocked.edits.equals(before), "Failed giant tree left partial writes");

        Room unsupported = new Room(helper, 80, pos -> true);
        unsupported.edits.put(ORIGIN.below(), Blocks.AIR.defaultBlockState());
        helper.assertFalse(placeGiant(unsupported), "Giant Twistwood accepted unsupported ground");
        helper.assertTrue(
                unsupported.edits.size() == 1,
                "Unsupported giant Twistwood wrote blocks before validation");
        helper.succeed();
    }

    @GameTest(
            templateNamespace = DarkCaverns.MOD_ID + "_slow",
            template = "sacred_torch",
            timeoutTicks = 1200)
    public static void hallowDecoratesRealCavernsWithoutBlockingRoutes(GameTestHelper helper) {
        var volume = new TerrainTestVolume(helper, 8675309, "tangled_hallow");
        volume.carve();
        byte[] dryTerrain = volume.snapshot();
        var terrainStats = TerrainTopology.measure(dryTerrain);

        volume.feature("hallow_lake", GenerationStep.Decoration.LAKES, 0);
        long water = volume.featureWrites.values().stream().filter(s -> s.is(Blocks.WATER)).count();
        long sproutlets =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.WATER_SPROUTLETS.get()))
                        .count();
        helper.assertTrue(water > 0, "Tangled Hallow generated no lake water");
        helper.assertTrue(sproutlets > 0, "Tangled Hallow lakes generated no Water Sproutlets");
        helper.assertTrue(
                volume.featureWrites.values().stream().noneMatch(s -> s.is(Blocks.LILY_PAD)),
                "Tangled Hallow lakes still generated vanilla lily pads");
        int layer = TerrainTestVolume.WIDTH * TerrainTestVolume.WIDTH;
        for (var entry : volume.featureWrites.entrySet()) {
            BlockPos pos = entry.getKey();
            if (entry.getValue().is(CustomBlocks.WATER_SPROUTLETS.get()))
                helper.assertTrue(
                        volume.get(pos.below()).is(Blocks.WATER)
                                && entry.getValue().canSurvive(volume.world, pos),
                        "Water Sproutlets are not floating on a lake surface");
        }

        byte[] afterLakes = volume.snapshot();
        volume.feature("giant_twistwood_tree", GenerationStep.Decoration.VEGETAL_DECORATION, 0);
        long giantLogs =
                volume.featureWrites.values().stream()
                        .filter(state -> state.is(CustomBlocks.TWISTWOOD_LOG.get()))
                        .count();
        long giantLeaves =
                volume.featureWrites.values().stream()
                        .filter(state -> state.is(CustomBlocks.TWISTWOOD_LEAVES.get()))
                        .count();
        helper.assertTrue(
                giantLogs > 0 && giantLeaves > 0,
                "Registered Giant Twistwood placement produced no complete tree");
        volume.feature("twistwood_tree", GenerationStep.Decoration.VEGETAL_DECORATION, 1);
        byte[] afterTrees = volume.snapshot();
        long logs =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.TWISTWOOD_LOG.get()))
                        .count();
        long leaves =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.TWISTWOOD_LEAVES.get()))
                        .count();
        helper.assertTrue(
                logs > giantLogs && leaves > giantLeaves,
                "Registered normal Twistwood placement added no trees");
        for (int i = 0; i < afterLakes.length; i++)
            if (afterLakes[i] != 0)
                helper.assertTrue(
                        afterTrees[i] == afterLakes[i], "Trees changed terrain or lake fluids");

        volume.feature("hallow_clutter", GenerationStep.Decoration.VEGETAL_DECORATION, 1);
        byte[] afterClutter = volume.snapshot();
        int clutterLogs = 0;
        int clutterLeaves = 0;
        for (var entry : volume.featureWrites.entrySet()) {
            BlockPos pos = entry.getKey();
            int index =
                    (pos.getY() * TerrainTestVolume.WIDTH + pos.getZ() - TerrainTestVolume.MIN)
                                    * TerrainTestVolume.WIDTH
                            + pos.getX()
                            - TerrainTestVolume.MIN;
            if (afterTrees[index] != 0) continue;
            BlockState state = entry.getValue();
            if (state.is(CustomBlocks.TWISTWOOD_LOG.get())) clutterLogs++;
            if (state.is(CustomBlocks.TWISTWOOD_LEAVES.get())) {
                clutterLeaves++;
                helper.assertTrue(
                        state.getValue(LeavesBlock.PERSISTENT),
                        "Low Hallow thickets used decaying leaves");
            }
        }
        helper.assertTrue(
                clutterLogs > 0 && clutterLeaves > 0,
                "Registered Hallow clutter added neither woody debris nor foliage");
        for (int i = 0; i < afterTrees.length; i++)
            if (afterTrees[i] != 0)
                helper.assertTrue(
                        afterClutter[i] == afterTrees[i],
                        "Hallow clutter replaced terrain, water, or an existing tree");

        volume.feature("hallow_mud", GenerationStep.Decoration.VEGETAL_DECORATION, 2);
        long mud = volume.featureWrites.values().stream().filter(s -> s.is(Blocks.MUD)).count();
        helper.assertTrue(mud > 0, "Registered Hallow mud placement produced no mud");
        volume.feature(
                "mighty_undersprouts_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 8);
        long mightyPlants =
                volume.featureWrites.values().stream()
                        .filter(state -> state.is(CustomBlocks.MIGHTY_UNDERSPROUTS.get()))
                        .count();
        volume.feature("tall_undersprouts_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 9);
        long tallPlants =
                volume.featureWrites.values().stream()
                        .filter(
                                state ->
                                        state.is(CustomBlocks.TALL_UNDERSPROUTS.get())
                                                && state.getValue(DoublePlantBlock.HALF)
                                                        == DoubleBlockHalf.LOWER)
                        .count();
        long tallPlantBlocks =
                volume.featureWrites.values().stream()
                        .filter(state -> state.is(CustomBlocks.TALL_UNDERSPROUTS.get()))
                        .count();
        helper.assertTrue(
                tallPlants > 0 && tallPlantBlocks == tallPlants * 2,
                "Tangled Hallow tall undersprouts are missing or unpaired: plants="
                        + tallPlants
                        + ", blocks="
                        + tallPlantBlocks);
        helper.assertTrue(
                mightyPlants > 0, "Registered Mighty Undersprouts placement produced no plants");
        volume.feature("undersprouts_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 10);
        long plants =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.UNDERSPROUTS.get()))
                        .count();
        helper.assertTrue(plants > 0, "Registered Undersprouts placement produced no plants");

        byte[] decorated = volume.snapshot();
        var decoratedStats = TerrainTopology.measure(decorated);
        int originalFloors = 0;
        int lostFloors = 0;
        for (int i = layer; i < dryTerrain.length - layer; i++) {
            if (!TerrainIntegrationTests.canStand(dryTerrain, i)) continue;
            originalFloors++;
            if (!TerrainIntegrationTests.canStand(decorated, i)) lostFloors++;
        }
        helper.assertTrue(
                lostFloors < originalFloors * .2,
                "Complete Hallow decoration obstructed too many walking positions");
        helper.assertTrue(
                decoratedStats.largestWalk >= terrainStats.largestWalk * .6
                        && decoratedStats.routeHeight >= 16,
                "Complete Hallow decoration severed important walking routes");
        DarkCaverns.LOGGER.info(
                "Tangled Hallow acceptance: water={}, sproutlets={}, giantLogs={}, giantLeaves={},"
                    + " logs={}, leaves={}, clutterLogs={}, clutterLeaves={}, mud={}, plants={},"
                    + " lostFloors={}/{}, largestWalk={}->{}, routeHeight={}",
                water,
                sproutlets,
                giantLogs,
                giantLeaves,
                logs,
                leaves,
                clutterLogs,
                clutterLeaves,
                mud,
                plants,
                lostFloors,
                originalFloors,
                terrainStats.largestWalk,
                decoratedStats.largestWalk,
                decoratedStats.routeHeight);
        helper.succeed();
    }

    private static boolean place(Room room) {
        return CustomFeatures.TWISTWOOD_TREE
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                room.world,
                                null,
                                RandomSource.create(7),
                                ORIGIN,
                                NoneFeatureConfiguration.INSTANCE));
    }

    private static boolean placeGiant(Room room) {
        return CustomFeatures.GIANT_TWISTWOOD_TREE
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                room.world,
                                null,
                                RandomSource.create(7),
                                ORIGIN,
                                NoneFeatureConfiguration.INSTANCE));
    }

    private static int height(Room room) {
        return room.edits.keySet().stream().mapToInt(BlockPos::getY).max().orElse(40) - 40;
    }

    private static void verifyAttached(GameTestHelper helper, Room room) {
        var visited = new HashSet<BlockPos>();
        var queue = new ArrayDeque<BlockPos>();
        queue.add(ORIGIN);
        visited.add(ORIGIN);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                if (room.edits.containsKey(neighbor) && visited.add(neighbor)) queue.add(neighbor);
            }
        }
        helper.assertTrue(
                visited.size() == room.edits.size(), "Tree contains a detached branch or canopy");
        for (var entry : room.edits.entrySet()) {
            BlockState state = entry.getValue();
            helper.assertTrue(
                    entry.getKey().getY() >= 40 && entry.getKey().getY() < 40 + room.gap,
                    "Tree breached floor or roof");
            if (state.is(CustomBlocks.TWISTWOOD_LEAVES.get())) {
                helper.assertTrue(
                        !state.getValue(LeavesBlock.PERSISTENT)
                                && state.getValue(LeavesBlock.DISTANCE) <= 6,
                        "Natural foliage must be supported and decay after logging");
                int distance = state.getValue(LeavesBlock.DISTANCE);
                boolean support = false;
                for (Direction direction : Direction.values()) {
                    BlockState neighbor = room.read(entry.getKey().relative(direction));
                    if (distance == 1 && neighbor.is(BlockTags.LOGS)) support = true;
                    if (neighbor.is(CustomBlocks.TWISTWOOD_LEAVES.get())
                            && neighbor.getValue(LeavesBlock.DISTANCE) == distance - 1)
                        support = true;
                }
                helper.assertTrue(
                        support, "Leaf distance does not correspond to an actual support path");
            }
        }
    }

    private static final class Room {
        final int gap;
        final Map<BlockPos, BlockState> edits = new HashMap<>();
        final WorldGenLevel world;

        Room(GameTestHelper helper, int gap, Predicate<BlockPos> bounds) {
            this.gap = gap;
            var biome =
                    helper.getLevel()
                            .registryAccess()
                            .registryOrThrow(Registries.BIOME)
                            .getHolderOrThrow(
                                    ResourceKey.create(
                                            Registries.BIOME, DarkCaverns.id("tangled_hallow")));
            world =
                    TerrainTestWorld.create(
                            this::read, (pos, state) -> edits.put(pos, state), bounds, biome, 7);
        }

        BlockState read(BlockPos pos) {
            return edits.getOrDefault(
                    pos,
                    pos.getY() < 40
                            ? CustomBlocks.OVERGROWN_CARFSTONE.get().defaultBlockState()
                            : pos.getY() >= 40 + gap
                                    ? Blocks.STONE.defaultBlockState()
                                    : Blocks.AIR.defaultBlockState());
        }
    }
}
