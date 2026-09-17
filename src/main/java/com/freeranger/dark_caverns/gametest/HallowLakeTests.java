package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HallowLakeTests {
    private static final BlockPos ORIGIN = new BlockPos(0, 40, 0);

    private HallowLakeTests() {}

    @GameTest(template = "sacred_torch")
    public static void lakesAreSealedDeterministicAndProtectTerrain(GameTestHelper helper)
            throws IOException {
        var first = new HashMap<BlockPos, BlockState>();
        var repeated = new HashMap<BlockPos, BlockState>();
        helper.assertTrue(
                place(room(helper, first, false, false), ORIGIN), "Lake failed on a broad shelf");
        helper.assertTrue(
                place(room(helper, repeated, false, false), ORIGIN) && first.equals(repeated),
                "Lake is not deterministic");
        Set<BlockPos> water = new HashSet<>();
        Set<BlockPos> mud = new HashSet<>();
        Set<BlockPos> sproutlets = new HashSet<>();
        first.forEach(
                (pos, state) -> {
                    if (state.is(Blocks.WATER)) water.add(pos);
                    if (state.is(Blocks.MUD)) mud.add(pos);
                    if (state.is(CustomBlocks.WATER_SPROUTLETS.get())) sproutlets.add(pos);
                });
        helper.assertTrue(water.size() > 30, "Lake is too small to read as a basin");
        helper.assertTrue(mud.size() > 10, "Lake generated without a visible muddy shore");
        helper.assertTrue(!sproutlets.isEmpty(), "Lake generated without Water Sproutlets");
        helper.assertTrue(
                first.values().stream().noneMatch(state -> state.is(Blocks.LILY_PAD)),
                "Lake generated vanilla lily pads instead of Water Sproutlets");
        for (BlockPos pos : sproutlets)
            helper.assertTrue(
                    read(first, pos.below()).is(Blocks.WATER),
                    "Water Sproutlets generated away from the lake surface");
        for (BlockPos pos : water) {
            helper.assertTrue(
                    pos.getY() >= 37 && pos.getY() <= 39, "Lake depth escaped its bounded shelf");
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockState neighbor = read(first, pos.relative(direction));
                helper.assertTrue(
                        neighbor.is(Blocks.WATER)
                                || neighbor.is(Blocks.MUD)
                                || neighbor.is(CustomBlocks.CARFSTONE.get())
                                || neighbor.is(CustomBlocks.OVERGROWN_CARFSTONE.get()),
                        "Lake has an open retaining wall");
            }
        }
        for (BlockPos pos : mud)
            helper.assertTrue(
                    water.stream()
                            .anyMatch(
                                    waterPos ->
                                            waterPos.getY() == pos.getY()
                                                    && Math.abs(waterPos.getX() - pos.getX())
                                                                    + Math.abs(
                                                                            waterPos.getZ()
                                                                                    - pos.getZ())
                                                            <= 2),
                    "Lake mud escaped the shoreline");
        for (BlockState obstacle :
                new BlockState[] {
                    Blocks.CHEST.defaultBlockState(),
                    Blocks.LAVA.defaultBlockState(),
                    CustomBlocks.LUMINITE_ORE.get().defaultBlockState(),
                    Blocks.BEDROCK.defaultBlockState(),
                    Blocks.AIR.defaultBlockState()
                }) {
            var blocked = new HashMap<BlockPos, BlockState>();
            blocked.put(ORIGIN.below(4), obstacle);
            var before = Map.copyOf(blocked);
            helper.assertFalse(
                    place(room(helper, blocked, false, false), ORIGIN),
                    "Lake carved protected or unsupported ground");
            helper.assertTrue(blocked.equals(before), "Rejected lake left partial edits");
        }
        var clipped = new HashMap<BlockPos, BlockState>();
        helper.assertFalse(
                place(room(helper, clipped, true, false), ORIGIN), "Lake escaped writable bounds");
        helper.assertTrue(clipped.isEmpty(), "Clipped lake left partial edits");
        helper.assertFalse(
                place(room(helper, new HashMap<>(), false, true), ORIGIN),
                "Lake crossed into a non-Hallow biome");
        writePreview(first);
        helper.succeed();
    }

    @GameTest(template = "sacred_torch", timeoutTicks = 200)
    public static void lakeWaterRemainsContainedAfterRealFluidTicks(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos origin = helper.absolutePos(new BlockPos(0, 192, 0));
        var original = new HashMap<BlockPos, BlockState>();
        Set<ChunkPos> newlyForced = new HashSet<>();
        var biome =
                level.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .getHolderOrThrow(
                                ResourceKey.create(
                                        Registries.BIOME, DarkCaverns.id("tangled_hallow")));
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++) {
                var chunk = new ChunkPos((origin.getX() >> 4) + x, (origin.getZ() >> 4) + z);
                if (!level.getForcedChunks().contains(chunk.toLong())) {
                    newlyForced.add(chunk);
                    level.setChunkForced(chunk.x, chunk.z, true);
                }
            }
        Runnable restore =
                () -> {
                    original.forEach((pos, state) -> level.setBlock(pos, state, 2));
                    newlyForced.forEach(chunk -> level.setChunkForced(chunk.x, chunk.z, false));
                };
        var scheduled = new AtomicInteger();
        WorldGenLevel view =
                (WorldGenLevel)
                        Proxy.newProxyInstance(
                                WorldGenLevel.class.getClassLoader(),
                                new Class<?>[] {WorldGenLevel.class},
                                (proxy, method, args) -> {
                                    if (method.getName().equals("getBiome")) return biome;
                                    if (method.getName().equals("scheduleTick"))
                                        scheduled.incrementAndGet();
                                    return method.invoke(level, args);
                                });
        try {
            for (BlockPos pos :
                    BlockPos.betweenClosed(origin.offset(-11, -9, -11), origin.offset(11, 6, 11))) {
                original.put(pos.immutable(), level.getBlockState(pos));
                level.setBlock(
                        pos,
                        pos.getY() < origin.getY()
                                ? CustomBlocks.CARFSTONE.get().defaultBlockState()
                                : Blocks.AIR.defaultBlockState(),
                        2);
            }
            helper.assertTrue(place(view, origin), "Live lake placement failed");
            Set<BlockPos> water = new HashSet<>();
            original.keySet()
                    .forEach(
                            pos -> {
                                if (level.getBlockState(pos).is(Blocks.WATER)) water.add(pos);
                            });
            helper.assertTrue(
                    scheduled.get() == water.size() && !water.isEmpty(),
                    "Lake did not schedule its fluid updates");
            helper.runAfterDelay(
                    40,
                    () -> {
                        try {
                            for (BlockPos pos : original.keySet()) {
                                helper.assertTrue(
                                        level.getBlockState(pos).is(Blocks.WATER)
                                                == water.contains(pos),
                                        "Water escaped or drained after ticking");
                                if (water.contains(pos)) {
                                    helper.assertTrue(
                                            level.getFluidState(pos).isSource(),
                                            "Lake source became a flowing block");
                                    helper.assertFalse(
                                            level.getFluidTicks()
                                                    .hasScheduledTick(pos, Fluids.WATER),
                                            "Fluid ticks did not execute");
                                }
                            }
                            helper.succeed();
                        } finally {
                            restore.run();
                        }
                    });
        } catch (Throwable error) {
            restore.run();
            throw error;
        }
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
                                                wrongBiome ? "rocky_caverns" : "tangled_hallow")));
        return TerrainTestWorld.create(
                pos -> read(edits, pos),
                (pos, state) -> edits.put(pos, state),
                pos -> Math.abs(pos.getX()) <= (clipped ? 0 : 15) && Math.abs(pos.getZ()) <= 15,
                biome,
                7);
    }

    private static BlockState read(Map<BlockPos, BlockState> edits, BlockPos pos) {
        return edits.getOrDefault(
                pos,
                pos.getY() >= 40
                        ? Blocks.AIR.defaultBlockState()
                        : pos.getY() == 39
                                ? CustomBlocks.OVERGROWN_CARFSTONE.get().defaultBlockState()
                                : CustomBlocks.CARFSTONE.get().defaultBlockState());
    }

    private static boolean place(WorldGenLevel level, BlockPos pos) {
        return CustomFeatures.HALLOW_LAKE
                .get()
                .place(
                        new FeaturePlaceContext<>(
                                Optional.empty(),
                                level,
                                null,
                                RandomSource.create(7),
                                pos,
                                NoneFeatureConfiguration.INSTANCE));
    }

    private static void writePreview(Map<BlockPos, BlockState> edits) throws IOException {
        var image = new BufferedImage(480, 270, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        g.setColor(new Color(0x101723));
        g.fillRect(0, 0, 480, 270);
        g.setColor(new Color(0xe5edf5));
        g.drawString("Hallow lake: plan view", 16, 20);
        g.drawString("Center cross-section", 250, 20);
        for (int x = -10; x <= 10; x++)
            for (int z = -10; z <= 10; z++) {
                BlockState state = read(edits, new BlockPos(x, 39, z));
                g.setColor(
                        new Color(
                                state.is(Blocks.WATER)
                                        ? 0x367e88
                                        : state.is(Blocks.MUD) ? 0x5b493c : 0x819744));
                g.fillRect(16 + (x + 10) * 10, 36 + (z + 10) * 10, 9, 9);
            }
        for (int x = -10; x <= 10; x++)
            for (int y = 32; y <= 44; y++) {
                BlockState state = read(edits, new BlockPos(x, y, 0));
                g.setColor(
                        new Color(
                                state.isAir()
                                        ? 0x101723
                                        : state.is(Blocks.WATER)
                                                ? 0x367e88
                                                : state.is(Blocks.MUD)
                                                        ? 0x5b493c
                                                        : state.is(
                                                                        CustomBlocks
                                                                                .OVERGROWN_CARFSTONE
                                                                                .get())
                                                                ? 0x819744
                                                                : 0x85899b));
                g.fillRect(250 + (x + 10) * 10, 70 + (44 - y) * 10, 9, 9);
            }
        g.dispose();
        Path dir = Path.of("../build/reports/terrain");
        Files.createDirectories(dir);
        ImageIO.write(image, "png", dir.resolve("hallow-lake.png").toFile());
    }
}
