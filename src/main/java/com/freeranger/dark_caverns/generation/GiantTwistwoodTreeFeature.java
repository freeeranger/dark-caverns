package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** A rare cavern landmark with a thick trunk, grounded roots and a ceiling-aware crown. */
public final class GiantTwistwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    private static final ResourceLocation TANGLED_HALLOW = DarkCaverns.id("tangled_hallow");
    private static final int MIN_CLEARANCE = 54;
    private static final int MAX_CLEARANCE = 80;

    public GiantTwistwoodTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!level.getBiome(origin).is(TANGLED_HALLOW)
                || !ground(level.getBlockState(origin.below()))
                || !supportedBase(level, origin)) return false;

        int clearance = 0;
        while (clearance < MAX_CLEARANCE && writable(level, origin.above(clearance))) clearance++;
        if (clearance < MIN_CLEARANCE) return false;
        int height = Math.min(52 + context.random().nextInt(21), clearance - 5);
        if (height < 48) return false;

        TreePlan plan = plan(level, origin, context.random(), height);
        if (plan.roots() < 4) return false;
        plan.leaves().keySet().removeAll(plan.logs().keySet());
        for (BlockPos pos : plan.logs().keySet()) if (!writable(level, pos)) return false;
        // Cavern roofs are naturally uneven. Let foliage conform to nearby rock instead of
        // requiring a perfectly empty rectangular volume, while every structural log remains
        // strictly collision-safe.
        plan.leaves().keySet().removeIf(pos -> !writable(level, pos));

        supportLeaves(plan.logs(), plan.leaves());
        plan.leaves().entrySet().removeIf(entry -> entry.getValue() > 6);
        if (plan.leaves().size() < 900) return false;
        plan.logs().forEach((pos, state) -> level.setBlock(pos, state, 2));
        plan.leaves()
                .forEach(
                        (pos, distance) -> {
                            level.setBlock(
                                    pos,
                                    CustomBlocks.TWISTWOOD_LEAVES
                                            .get()
                                            .defaultBlockState()
                                            .setValue(LeavesBlock.DISTANCE, distance)
                                            .setValue(LeavesBlock.PERSISTENT, false),
                                    2);
                        });
        return true;
    }

    private static TreePlan plan(
            WorldGenLevel level, BlockPos origin, RandomSource random, int height) {
        var logs = new LinkedHashMap<BlockPos, BlockState>();
        var leaves = new LinkedHashMap<BlockPos, Integer>();
        var spine = new ArrayList<BlockPos>();
        double phase = random.nextDouble() * Math.PI * 2;
        double lean = random.nextBoolean() ? 1 : -1;
        BlockPos previous = origin;

        for (int y = 0; y <= height; y++) {
            double t = y / (double) height;
            int centerX = (int) Math.round(Math.sin(phase + t * 2.6) * t * 4.0);
            int centerZ = (int) Math.round(Math.cos(phase + lean * t * 2.4) * t * 4.0);
            BlockPos center = origin.offset(centerX, y, centerZ);
            connect(logs, previous, center);
            int radius =
                    y <= 5
                            ? 4
                            : y < height * .20
                                    ? 3
                                    : y < height * .58 ? 2 : y < height * .82 ? 1 : 0;
            trunkLayer(level, logs, center, origin.getY(), radius);
            spine.add(center);
            previous = center;
        }

        int rooted = roots(level, logs, origin, random);
        int branches = 12;
        for (int branch = 0; branch < branches; branch++) {
            int forkY = Math.min(height - 5, (int) (height * (.35 + branch * .045)));
            BlockPos fork = spine.get(forkY);
            BlockPos tip = fork;
            double angle = phase + branch * 2.399963229728653 + (random.nextDouble() - .5) * .35;
            double curve = (random.nextDouble() - .5) * .45;
            int length = 9 + random.nextInt(6);
            int rise = 4 + random.nextInt(5);
            for (int step = 1; step <= length; step++) {
                double progress = step / (double) length;
                double reach = step * .95;
                BlockPos next =
                        fork.offset(
                                (int) Math.round(Math.cos(angle + curve * progress) * reach),
                                (int) Math.round(rise * progress),
                                (int) Math.round(Math.sin(angle + curve * progress) * reach));
                connect(logs, tip, next);
                tip = next;
            }
            crown(leaves, tip, branch % 3 == 0 ? 5 : 4);
        }
        crown(leaves, spine.getLast().above(), 5);
        return new TreePlan(origin, logs, leaves, rooted);
    }

    private static void trunkLayer(
            WorldGenLevel level,
            Map<BlockPos, BlockState> logs,
            BlockPos center,
            int floorY,
            int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius + 1) continue;
                BlockPos pos = center.offset(x, 0, z);
                // Let the broad lowest layer conform to the cavern floor instead of floating.
                if (pos.getY() == floorY && !ground(level.getBlockState(pos.below()))) continue;
                log(logs, pos, Direction.Axis.Y);
            }
        }
    }

    private static int roots(
            WorldGenLevel level,
            Map<BlockPos, BlockState> logs,
            BlockPos origin,
            RandomSource random) {
        int complete = 0;
        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        for (int[] direction : directions) {
            BlockPos tip = origin;
            int reached = 0;
            int length = 9 + random.nextInt(7);
            for (int step = 1; step <= length; step++) {
                BlockPos target = origin.offset(direction[0] * step, 0, direction[1] * step);
                List<BlockPos> segment = path(tip, target);
                if (segment.stream()
                        .anyMatch(
                                pos ->
                                        !ground(level.getBlockState(pos.below()))
                                                || !writable(level, pos))) break;
                BlockPos last = tip;
                for (BlockPos pos : segment) {
                    Direction.Axis axis =
                            pos.getX() != last.getX() ? Direction.Axis.X : Direction.Axis.Z;
                    log(logs, pos, axis);
                    if (step <= 4) log(logs, pos.above(), axis);
                    last = pos;
                }
                tip = target;
                reached = step;
            }
            if (reached >= 3) complete++;
        }
        return complete;
    }

    private static List<BlockPos> path(BlockPos from, BlockPos to) {
        var result = new ArrayList<BlockPos>();
        while (from.getX() != to.getX()) {
            from = from.offset(Integer.signum(to.getX() - from.getX()), 0, 0);
            result.add(from);
        }
        while (from.getZ() != to.getZ()) {
            from = from.offset(0, 0, Integer.signum(to.getZ() - from.getZ()));
            result.add(from);
        }
        return result;
    }

    private static void connect(Map<BlockPos, BlockState> logs, BlockPos from, BlockPos to) {
        while (from.getY() != to.getY()) {
            from = from.offset(0, Integer.signum(to.getY() - from.getY()), 0);
            log(logs, from, Direction.Axis.Y);
        }
        while (from.getX() != to.getX()) {
            from = from.offset(Integer.signum(to.getX() - from.getX()), 0, 0);
            log(logs, from, Direction.Axis.X);
        }
        while (from.getZ() != to.getZ()) {
            from = from.offset(0, 0, Integer.signum(to.getZ() - from.getZ()));
            log(logs, from, Direction.Axis.Z);
        }
    }

    private static void log(Map<BlockPos, BlockState> logs, BlockPos pos, Direction.Axis axis) {
        logs.putIfAbsent(
                pos,
                CustomBlocks.TWISTWOOD_LOG
                        .get()
                        .defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, axis));
    }

    private static void crown(Map<BlockPos, Integer> leaves, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius + 1; y < radius; y++) {
                    if (x * x + z * z + y * y * 2 <= radius * radius + 2)
                        leaves.put(center.offset(x, y, z), 7);
                }
            }
        }
    }

    private static void supportLeaves(
            Map<BlockPos, BlockState> logs, Map<BlockPos, Integer> leaves) {
        var frontier = new ArrayDeque<BlockPos>(logs.keySet());
        while (!frontier.isEmpty()) {
            BlockPos pos = frontier.remove();
            int distance = logs.containsKey(pos) ? 0 : leaves.get(pos);
            if (distance >= 6) continue;
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                if (leaves.getOrDefault(neighbor, 0) > distance + 1) {
                    leaves.put(neighbor, distance + 1);
                    frontier.add(neighbor);
                }
            }
        }
    }

    private static boolean supportedBase(WorldGenLevel level, BlockPos origin) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = origin.relative(direction);
            if (!ground(level.getBlockState(side.below())) || !writable(level, side)) return false;
        }
        return true;
    }

    private static boolean ground(BlockState state) {
        return state.getFluidState().isEmpty()
                && (state.is(CustomBlocks.OVERGROWN_CARFSTONE.get())
                        || state.is(BlockTags.DIRT)
                        || state.is(net.minecraft.world.level.block.Blocks.FARMLAND));
    }

    private static boolean writable(WorldGenLevel level, BlockPos pos) {
        if (pos.getY() < level.getMinBuildHeight()
                || pos.getY() >= level.getMaxBuildHeight()
                || !level.ensureCanWrite(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return state.getFluidState().isEmpty()
                && (state.isAir()
                        || state.is(CustomBlocks.UNDERSPROUTS.get())
                        || state.is(CustomBlocks.TALL_UNDERSPROUTS.get())
                        || state.is(CustomBlocks.MIGHTY_UNDERSPROUTS.get()));
    }

    /** The stable anchor is intentionally retained for a future root-room/template pass. */
    private record TreePlan(
            BlockPos anchor,
            Map<BlockPos, BlockState> logs,
            Map<BlockPos, Integer> leaves,
            int roots) {}
}
