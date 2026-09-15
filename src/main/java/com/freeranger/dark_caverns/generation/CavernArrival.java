package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Bounded, read-only site search followed by a fully preflighted landing placement. */
public final class CavernArrival {
    public static final int SEARCH_RADIUS = 16;
    private static final int MAX_ATTEMPTS = 512;

    private CavernArrival() {}

    public record Plan(
            BlockPos gateway,
            BlockPos feet,
            Direction facing,
            Map<BlockPos, BlockState> before,
            Map<BlockPos, BlockState> after) {
        public boolean place(WorldGenLevel level) {
            // Never partially excavate a site whose preflight has become stale.
            if (before.entrySet().stream()
                    .anyMatch(e -> !level.getBlockState(e.getKey()).equals(e.getValue())))
                return false;
            after.forEach((pos, state) -> level.setBlock(pos, state, Block.UPDATE_ALL));
            return true;
        }
    }

    @Nullable public static Plan find(WorldGenLevel level, BlockPos column) {
        var candidates = new ArrayList<BlockPos>();
        int middle = (level.getMinBuildHeight() + level.getMaxBuildHeight()) / 2;
        for (int x = -8; x <= 8; x += 2) {
            for (int z = -8; z <= 8; z += 2) {
                for (int y = level.getMinBuildHeight() + 32;
                        y < level.getMaxBuildHeight() - 24;
                        y++) {
                    BlockPos floor = new BlockPos(column.getX() + x, y, column.getZ() + z);
                    if (natural(level.getBlockState(floor))
                            && clearable(level.getBlockState(floor.above()))
                            && clearable(level.getBlockState(floor.above(2)))
                            && clearable(level.getBlockState(floor.above(3))))
                        candidates.add(floor);
                }
            }
        }
        candidates.sort(
                Comparator.comparingInt(
                        p ->
                                Math.abs(p.getY() - middle)
                                        + 3
                                                * (Math.abs(p.getX() - column.getX())
                                                        + Math.abs(p.getZ() - column.getZ()))));
        int attempts = 0;
        for (BlockPos floor : candidates) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                if (++attempts > MAX_ATTEMPTS) return null;
                Plan plan = plan(level, floor, facing);
                if (plan != null) return plan;
            }
        }
        return null;
    }

    @Nullable private static Plan plan(WorldGenLevel level, BlockPos floor, Direction facing) {
        var before = new LinkedHashMap<BlockPos, BlockState>();
        var after = new LinkedHashMap<BlockPos, BlockState>();
        // The path must meet existing ground, not finish on the edge of another platform.
        for (int forward = 6; forward <= 7; forward++) {
            for (int side = -1; side <= 1; side++) {
                BlockPos exit = local(floor, facing, side, 0, forward);
                if (!walkableExit(level, exit, forward == 6 && side == 0)) return null;
            }
        }
        if (!level.getBlockState(local(floor, facing, 0, 2, 8)).isAir()) return null;
        for (int forward = -2; forward <= 5; forward++) {
            int width = forward <= 2 ? 2 : 1;
            for (int side = -width; side <= width; side++) {
                BlockPos base = local(floor, facing, side, 0, forward);
                // Only short, solidly founded steps; never a bridge across a deep shaft.
                int depth = 0;
                while (depth <= 3 && level.getBlockState(base.below(depth)).isAir()) depth++;
                if (depth > 3
                        || !natural(level.getBlockState(base.below(depth)))
                        || !natural(level.getBlockState(base.below(depth + 1)))) return null;
                for (int y = -depth; y <= 3; y++) {
                    BlockPos pos = base.above(y);
                    BlockState existing = level.getBlockState(pos);
                    if ((!clearable(existing) && !natural(existing))
                            || !existing.getFluidState().isEmpty()) return null;
                    before.put(pos, existing);
                    BlockState replacement =
                            y <= 0
                                    ? CustomBlocks.SMOOTH_CARFSTONE.get().defaultBlockState()
                                    : Blocks.AIR.defaultBlockState();
                    if (y > 0 && (forward == -2 || (forward == 0 && Math.abs(side) == 1))) {
                        replacement = CustomBlocks.CARFSTONE_BRICKS.get().defaultBlockState();
                    }
                    if (forward == 0 && side == 0 && y == 3)
                        replacement =
                                CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get().defaultBlockState();
                    if (forward == 0 && Math.abs(side) == 2 && y == 1)
                        replacement = CustomBlocks.LUMINITE_LANTERN.get().defaultBlockState();
                    after.put(pos, replacement);
                }
            }
        }
        // Excavation must not open a lake/lava pocket next to the landing.
        for (var entry : after.entrySet()) {
            if (entry.getValue().isAir()
                    || entry.getValue().is(CustomBlocks.LUMINITE_LANTERN.get())
                    || entry.getValue().is(CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get())) {
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = entry.getKey().relative(direction);
                    if (!level.getFluidState(neighbor).isEmpty()) return null;
                }
            }
        }
        return new Plan(
                floor.above(3),
                floor.relative(facing, 2).above(),
                facing,
                Map.copyOf(before),
                java.util.Collections.unmodifiableMap(after));
    }

    private static boolean walkableExit(WorldGenLevel level, BlockPos floor, boolean exact) {
        for (int dy = exact ? 0 : -1; dy <= (exact ? 0 : 1); dy++) {
            BlockPos pos = floor.above(dy);
            if (natural(level.getBlockState(pos))
                    && passable(level.getBlockState(pos.above()))
                    && passable(level.getBlockState(pos.above(2)))
                    && passable(level.getBlockState(pos.above(3)))) return true;
        }
        return false;
    }

    private static BlockPos local(BlockPos floor, Direction facing, int side, int y, int forward) {
        return floor.relative(facing, forward).relative(facing.getClockWise(), side).above(y);
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }

    private static boolean passable(BlockState state) {
        return state.isAir()
                || state.is(CustomBlocks.GLIMMERGRASS.get())
                || state.is(CustomBlocks.CHARRED_GRASS.get())
                || state.is(CustomBlocks.UNDERSPROUTS.get())
                || state.is(CustomBlocks.GLIMMERSHROOM.get());
    }

    private static boolean clearable(BlockState state) {
        return passable(state) || state.is(CustomBlocks.SCORCHED_BERRY_BUSH.get());
    }
}
