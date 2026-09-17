package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** A preflighted, gap-aware crooked tree. Failed attempts leave no partial trunks or roots. */
public final class TwistwoodTreeFeature extends Feature<NoneFeatureConfiguration> {
    public TwistwoodTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var level = context.level();
        var origin = context.origin();
        var random = context.random();
        if (!ground(level.getBlockState(origin.below()))) return false;
        int gap = 0;
        while (gap < 32 && writable(level, origin.above(gap))) gap++;
        if (gap < 7) return false;
        int height = Math.min(8 + random.nextInt(8), gap - 4);
        var logs = new LinkedHashMap<BlockPos, BlockState>();
        var leaves = new LinkedHashMap<BlockPos, Integer>();
        double phase = random.nextDouble() * Math.PI * 2;
        double turn = (1.35 + random.nextDouble() * .45) * Math.PI;
        if (random.nextBoolean()) turn = -turn;
        double radius = .9 + height * .07;
        var spine = new ArrayList<BlockPos>();
        BlockPos trunk = origin;
        log(logs, trunk, Direction.Axis.Y);
        spine.add(trunk);
        for (int y = 1; y <= height; y++) {
            double t = y / (double) height;
            double spread = radius * Math.sin(t * Math.PI / 2);
            int x = (int) Math.round(spread * (Math.cos(phase + turn * t) - Math.cos(phase)));
            int z = (int) Math.round(spread * (Math.sin(phase + turn * t) - Math.sin(phase)));
            BlockPos next = origin.offset(Math.clamp(x, -3, 3), y, Math.clamp(z, -3, 3));
            connect(logs, trunk, next);
            trunk = next;
            spine.add(trunk);
        }
        // Stagger hooked boughs along the spine instead of hiding them in one round crown.
        int branches = height >= 9 ? 4 : 2;
        for (int branch = 0; branch < branches; branch++) {
            int forkY = Math.max(3, (int) (height * (.45 + .45 * branch / (branches - 1.0))));
            BlockPos fork = spine.get(forkY);
            BlockPos tip = fork;
            double angle = phase + branch * 2.4;
            int length = height >= 9 ? 4 + random.nextInt(2) : 3;
            double curl = Math.copySign(.35 + random.nextDouble() * .2, turn);
            for (int step = 1; step <= length; step++) {
                double reach = step * .7;
                int x =
                        Math.clamp(
                                fork.getX()
                                        - origin.getX()
                                        + (int) Math.round(Math.cos(angle + step * curl) * reach),
                                -5,
                                5);
                int z =
                        Math.clamp(
                                fork.getZ()
                                        - origin.getZ()
                                        + (int) Math.round(Math.sin(angle + step * curl) * reach),
                                -5,
                                5);
                BlockPos next = origin.offset(x, Math.min(height + 1, forkY + step / 2), z);
                connect(logs, tip, next);
                tip = next;
            }
            crown(leaves, tip, 1 + random.nextInt(2));
        }
        crown(leaves, trunk.above(), 2);
        // Short exposed roots follow dry, supported floor positions; never bridge a drop.
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int step = 1; step <= 2; step++) {
                BlockPos root = origin.relative(direction, step);
                if (!ground(level.getBlockState(root.below())) || !writable(level, root)) break;
                log(logs, root, direction.getAxis());
            }
        }
        leaves.keySet().removeAll(logs.keySet());
        for (BlockPos pos : logs.keySet()) if (!writable(level, pos)) return false;
        for (BlockPos pos : leaves.keySet()) if (!writable(level, pos)) return false;
        // Compute real leaf support distances so natural leaves decay after harvesting logs.
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
        logs.forEach((pos, state) -> level.setBlock(pos, state, 2));
        leaves.forEach(
                (pos, distance) -> {
                    if (distance <= 6)
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

    private static void log(Map<BlockPos, BlockState> logs, BlockPos pos, Direction.Axis axis) {
        logs.put(
                pos,
                CustomBlocks.TWISTWOOD_LOG
                        .get()
                        .defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, axis));
    }

    private static void connect(Map<BlockPos, BlockState> logs, BlockPos from, BlockPos to) {
        // Every bend is face-connected, including diagonals, with correctly oriented log ends.
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

    private static void crown(Map<BlockPos, Integer> leaves, BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++)
            for (int z = -radius; z <= radius; z++)
                for (int y = -1; y <= 1; y++)
                    if (x * x + z * z + y * y * 2 <= radius * radius + 1)
                        leaves.put(center.offset(x, y, z), 7);
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
                        || state.is(CustomBlocks.TWISTWOOD_SAPLING.get()));
    }
}
