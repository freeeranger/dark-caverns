package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
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
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
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
    public static void twistwoodTreesScaleAttachAndProtectTheirSurroundings(GameTestHelper helper)
            throws IOException {
        Room low = new Room(helper, 8, pos -> true);
        Room tall = new Room(helper, 32, pos -> true);
        helper.assertTrue(place(low) && place(tall), "Trees failed in clear supported rooms");
        helper.assertTrue(height(tall) > height(low), "Twistwood did not respond to cavern height");
        Room repeat = new Room(helper, 32, pos -> true);
        place(repeat);
        helper.assertTrue(repeat.edits.equals(tall.edits), "Tree shape is not deterministic");
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
        writeGallery(low, tall);
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 1200)
    public static void hallowDecoratesRealCavernsWithoutBlockingRoutes(GameTestHelper helper)
            throws IOException {
        var volume = new TerrainTestVolume(helper, 8675309, "tangled_hallow");
        volume.carve();
        byte[] dryTerrain = volume.snapshot();
        var terrainStats = TerrainTopology.measure(dryTerrain);
        volume.feature("hallow_lake", GenerationStep.Decoration.LAKES, 0);
        long water = volume.featureWrites.values().stream().filter(s -> s.is(Blocks.WATER)).count();
        long lilies =
                volume.featureWrites.values().stream().filter(s -> s.is(Blocks.LILY_PAD)).count();
        helper.assertTrue(
                water > 500 && water < 6000,
                "Surface lakes are missing or overwhelm Tangled Hallow: " + water);
        helper.assertTrue(lilies >= 20, "Surface lakes generated without enough lily pads");
        int layer = TerrainTestVolume.WIDTH * TerrainTestVolume.WIDTH;
        int surfaceWater = 0;
        var waterSurface = new HashSet<BlockPos>();
        for (var entry : volume.featureWrites.entrySet()) {
            BlockPos pos = entry.getKey();
            if (entry.getValue().is(Blocks.LILY_PAD)) {
                helper.assertTrue(
                        volume.get(pos.below()).is(Blocks.WATER)
                                && entry.getValue().canSurvive(volume.world, pos),
                        "Lily pad is not floating on a lake surface");
            }
            if (!entry.getValue().is(Blocks.WATER) || volume.get(pos.above()).is(Blocks.WATER))
                continue;
            surfaceWater++;
            waterSurface.add(pos);
            int index =
                    (pos.getY() * TerrainTestVolume.WIDTH + pos.getZ() - TerrainTestVolume.MIN)
                                    * TerrainTestVolume.WIDTH
                            + pos.getX()
                            - TerrainTestVolume.MIN;
            helper.assertTrue(
                    dryTerrain[index] == 1 && dryTerrain[index + layer] == 0,
                    "Lake was buried below the exposed cavern floor");
        }
        helper.assertTrue(surfaceWater > 100, "Lakes have too little visible surface water");
        WaterShape shape = waterShape(waterSurface);
        helper.assertTrue(
                shape.lakes() >= 3
                        && shape.lakes() <= 24
                        && shape.largest() >= 55
                        && shape.small() >= 1,
                "Lake size mix is sparse or cluttered: visible=" + surfaceWater + ", " + shape);
        byte[] before = volume.snapshot();
        var oldStats = TerrainTopology.measure(before);
        helper.assertTrue(
                oldStats.largestWalk >= terrainStats.largestWalk * .75,
                "Surface lakes severed too much of the forest floor");
        volume.feature("twistwood_tree", GenerationStep.Decoration.VEGETAL_DECORATION, 0);
        byte[] afterTrees = volume.snapshot();
        var stats = TerrainTopology.measure(afterTrees);
        long logs =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.TWISTWOOD_LOG.get()))
                        .count();
        long leaves =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.TWISTWOOD_LEAVES.get()))
                        .count();
        helper.assertTrue(
                logs > 1000 && leaves > 2500,
                "Tangled Hallow generated too few twistwood trees: logs="
                        + logs
                        + ", leaves="
                        + leaves);
        int lostFloors = 0;
        int originalFloors = 0;
        int added = 0;
        for (int i = layer; i < before.length - layer; i++) {
            if (before[i] == 0 && afterTrees[i] == 1) added++;
            if (before[i] == 0 && before[i + layer] == 0 && before[i - layer] == 1) {
                originalFloors++;
                if (afterTrees[i] != 0 || afterTrees[i + layer] != 0) lostFloors++;
            }
            if (before[i] != 0)
                helper.assertTrue(before[i] == afterTrees[i], "Trees changed terrain or fluids");
        }
        helper.assertTrue(
                lostFloors < originalFloors * .1, "Forest obstructed too many walking positions");
        helper.assertTrue(
                added < before.length * .02, "Forest canopy consumed too much cave volume");
        helper.assertTrue(
                stats.largestWalk >= terrainStats.largestWalk * .7 && stats.routeHeight >= 24,
                "Forest severed important walking routes");
        volume.feature("undersprouts_patch", GenerationStep.Decoration.VEGETAL_DECORATION, 7);
        long plants =
                volume.featureWrites.values().stream()
                        .filter(s -> s.is(CustomBlocks.UNDERSPROUTS.get()))
                        .count();
        helper.assertTrue(
                plants > 2000,
                "Tangled Hallow generated too little undergrowth: undersprouts=" + plants);
        DarkCaverns.LOGGER.info(
                "Tangled Hallow lakes: water={}, surface={}, lilies={}, lakes={}, largest={},"
                        + " small={}, originalWalk={}, afterLakesWalk={}",
                water,
                surfaceWater,
                lilies,
                shape.lakes(),
                shape.largest(),
                shape.small(),
                terrainStats.largestWalk,
                oldStats.largestWalk);
        DarkCaverns.LOGGER.info(
                "Tangled Hallow: logs={}, leaves={}, plants={}, lostFloors={}/{}, added={},"
                        + " largestWalk={}->{}, routeHeight={}",
                logs,
                leaves,
                plants,
                lostFloors,
                originalFloors,
                added,
                oldStats.largestWalk,
                stats.largestWalk,
                stats.routeHeight);
        Path dir = Path.of("../build/reports/terrain");
        Files.createDirectories(dir);
        Files.writeString(
                dir.resolve("tangled-hallow.txt"),
                "seed=8675309\nlogs="
                        + logs
                        + "\nwater="
                        + water
                        + "\nlilies="
                        + lilies
                        + "\nlakes="
                        + shape.lakes()
                        + "\nlargest_lake="
                        + shape.largest()
                        + "\nleaves="
                        + leaves
                        + "\nplants="
                        + plants
                        + "\nlost_floors="
                        + lostFloors
                        + "/"
                        + originalFloors
                        + "\nroute_height="
                        + stats.routeHeight
                        + "\n");
        helper.succeed();
    }

    private static WaterShape waterShape(Set<BlockPos> surface) {
        var remaining = new HashSet<>(surface);
        int lakes = 0;
        int largest = 0;
        int small = 0;
        while (!remaining.isEmpty()) {
            var queue = new ArrayDeque<BlockPos>();
            queue.add(remaining.iterator().next());
            int size = 0;
            while (!queue.isEmpty()) {
                BlockPos pos = queue.remove();
                if (!remaining.remove(pos)) continue;
                size++;
                for (Direction direction : Direction.Plane.HORIZONTAL)
                    if (remaining.contains(pos.relative(direction)))
                        queue.add(pos.relative(direction));
            }
            lakes++;
            largest = Math.max(largest, size);
            if (size < 50) small++;
        }
        return new WaterShape(lakes, largest, small);
    }

    private record WaterShape(int lakes, int largest, int small) {}

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

    private static void writeGallery(Room low, Room tall) throws IOException {
        var image = new BufferedImage(960, 440, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        g.setColor(new Color(0x101723));
        g.fillRect(0, 0, 960, 440);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        for (int index = 0; index < 3; index++) {
            Room room = index == 0 ? low : tall;
            int offset = index * 320;
            g.setColor(new Color(0xe5edf5));
            g.drawString(
                    index == 0
                            ? "Low cavern"
                            : index == 1 ? "Twisting vault tree" : "Exposed branch structure",
                    offset + 12,
                    25);
            g.setColor(new Color(0x414e30));
            g.fillPolygon(
                    new int[] {offset + 160, offset + 280, offset + 160, offset + 40},
                    new int[] {286, 346, 406, 346},
                    4);
            boolean woodOnly = index == 2;
            var blocks =
                    room.edits.entrySet().stream()
                            .filter(
                                    e ->
                                            !woodOnly
                                                    || e.getValue()
                                                            .is(CustomBlocks.TWISTWOOD_LOG.get()))
                            .sorted(
                                    java.util.Comparator.comparingInt(
                                            e ->
                                                    (e.getKey().getX() + e.getKey().getZ()) * 3
                                                            + (e.getKey().getY() - 40) * 2))
                            .toList();
            for (var entry : blocks) {
                BlockPos pos = entry.getKey();
                int x = offset + 160 + (pos.getX() - pos.getZ()) * 8;
                int y = 330 + (pos.getX() + pos.getZ()) * 4 - (pos.getY() - 40) * 12;
                Color color =
                        new Color(
                                entry.getValue().is(CustomBlocks.TWISTWOOD_LOG.get())
                                        ? 0xada18a
                                        : 0x8d9d4c);
                g.setColor(color.brighter());
                g.fillPolygon(new int[] {x, x + 8, x, x - 8}, new int[] {y - 4, y, y + 4, y}, 4);
                g.setColor(color.darker());
                g.fillPolygon(
                        new int[] {x - 8, x, x, x - 8}, new int[] {y, y + 4, y + 16, y + 12}, 4);
                g.setColor(color);
                g.fillPolygon(
                        new int[] {x, x + 8, x + 8, x}, new int[] {y + 4, y, y + 12, y + 16}, 4);
            }
        }
        g.dispose();
        Path dir = Path.of("../build/reports/terrain");
        Files.createDirectories(dir);
        ImageIO.write(image, "png", dir.resolve("twistwood-trees.png").toFile());
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
