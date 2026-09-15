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
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

/** Bounded, read-only site search followed by a fully preflighted landing placement. */
public final class CavernArrival {
    public static final int SEARCH_RADIUS = 16;
    public static final int MAX_PLACED_BLOCKS = 3072;
    private static final int MAX_ATTEMPTS = 512;
    private static final int MAX_SHAFT_ATTEMPTS = 64;

    private CavernArrival() {}

    public record Plan(
            BlockPos gateway,
            BlockPos feet,
            BlockPos exit,
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
                                level.getMaxBuildHeight()
                                        - p.getY()
                                        + 3
                                                * (Math.abs(p.getX() - column.getX())
                                                        + Math.abs(p.getZ() - column.getZ()))));
        int attempts = 0;
        int shafts = 0;
        for (BlockPos floor : candidates) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                if (++attempts > MAX_ATTEMPTS) return null;
                Plan plan = plan(level, floor, facing);
                if (plan != null) {
                    if (++shafts > MAX_SHAFT_ATTEMPTS) return null;
                    Plan connected = connectRoof(level, floor, plan);
                    if (connected != null) return connected;
                }
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
                floor.relative(facing, 2).above(),
                facing,
                Map.copyOf(before),
                java.util.Collections.unmodifiableMap(after));
    }

    /** A roof-bound vestibule and a sealed, climbable shaft to the surveyed cave-floor exit. */
    @Nullable private static Plan connectRoof(WorldGenLevel level, BlockPos floor, Plan landing) {
        Direction facing = landing.facing();
        int roofY = level.getMaxBuildHeight() - 1;
        BlockPos roof = new BlockPos(floor.getX(), roofY, floor.getZ());
        BlockPos upperFloor = roof.below(3);
        var before = new LinkedHashMap<>(landing.before());
        var after = new LinkedHashMap<>(landing.after());
        // The portal replaces actual ceiling bedrock, never a freestanding imitation of it.
        for (int forward = -4; forward <= 2; forward++) {
            int width = forward < -2 ? 1 : 2;
            for (int side = -width; side <= width; side++) {
                BlockPos cap = local(roof, facing, side, 0, forward);
                BlockState state = level.getBlockState(cap);
                if (!bedrock(state)) return null;
                before.put(cap, state);
            }
        }
        BlockState bricks = CustomBlocks.CARFSTONE_BRICKS.get().defaultBlockState();
        BlockState paving = CustomBlocks.SMOOTH_CARFSTONE.get().defaultBlockState();
        // Remove the lower return portal from this new plan; the only portal belongs in the roof.
        after.put(landing.gateway(), bricks);
        for (int forward = -2; forward <= 2; forward++) {
            for (int side = -2; side <= 2; side++) {
                for (int y = 0; y <= 2; y++) {
                    BlockPos pos = local(upperFloor, facing, side, y, forward);
                    boolean wall = Math.abs(side) == 2 || Math.abs(forward) == 2;
                    BlockState state =
                            y == 0 ? paving : wall ? bricks : Blocks.AIR.defaultBlockState();
                    if (y == 1 && forward == 0 && Math.abs(side) == 2)
                        state = CustomBlocks.LUMINITE_BLOCK.get().defaultBlockState();
                    if (!addRoofBlock(level, before, after, pos, state, roofY)) return null;
                }
            }
        }
        // One-wide ladder well: solid walls prevent side falls and seal neighbouring fluids.
        // Its front opens only into the upper vestibule and the lower walk-out chamber.
        for (int y = floor.getY(); y < roofY; y++) {
            for (int forward = -4; forward <= -2; forward++) {
                for (int side = -1; side <= 1; side++) {
                    BlockPos pos = local(floor, facing, side, y - floor.getY(), forward);
                    boolean center = side == 0 && forward == -3;
                    boolean door =
                            side == 0
                                    && forward == -2
                                    && (y == floor.getY() + 1
                                            || y == floor.getY() + 2
                                            || y == upperFloor.getY() + 1
                                            || y == upperFloor.getY() + 2);
                    BlockState state =
                            center && y > floor.getY()
                                    ? Blocks.LADDER
                                            .defaultBlockState()
                                            .setValue(LadderBlock.FACING, facing)
                                    : door ? Blocks.AIR.defaultBlockState() : bricks;
                    if (Math.abs(side) == 1 && forward == -3 && (y - floor.getY()) % 12 == 4)
                        state = CustomBlocks.LUMINITE_BLOCK.get().defaultBlockState();
                    if (!addRoofBlock(level, before, after, pos, state, roofY)) return null;
                }
            }
        }
        after.put(roof, CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get().defaultBlockState());
        if (after.size() > MAX_PLACED_BLOCKS) return null;
        // Check openings against the final sealed plan, including the untouched floor exit.
        for (var entry : after.entrySet()) {
            if (entry.getValue().isAir() || entry.getValue().is(Blocks.LADDER)) {
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = entry.getKey().relative(direction);
                    BlockState state = after.get(neighbor);
                    if (state == null) {
                        state = level.getBlockState(neighbor);
                        before.putIfAbsent(neighbor, state);
                    }
                    if (!state.getFluidState().isEmpty()) return null;
                }
            }
        }
        // Install every wall before ladders, so neighbour updates cannot pop their supports.
        var ordered = new LinkedHashMap<BlockPos, BlockState>();
        after.forEach(
                (pos, state) -> {
                    if (!state.is(Blocks.LADDER)) ordered.put(pos, state);
                });
        after.forEach(
                (pos, state) -> {
                    if (state.is(Blocks.LADDER)) ordered.put(pos, state);
                });
        return new Plan(
                roof,
                upperFloor.relative(facing).above(),
                landing.exit(),
                facing,
                Map.copyOf(before),
                java.util.Collections.unmodifiableMap(ordered));
    }

    private static boolean addRoofBlock(
            WorldGenLevel level,
            Map<BlockPos, BlockState> before,
            Map<BlockPos, BlockState> after,
            BlockPos pos,
            BlockState replacement,
            int roofY) {
        BlockState existing = level.getBlockState(pos);
        // An ore already forming a solid shaft wall is safe to retain, not replace or mine.
        if (existing.is(Tags.Blocks.ORES)
                && !existing.hasBlockEntity()
                && existing.isCollisionShapeFullBlock(level, pos)
                && !replacement.isAir()
                && !replacement.is(Blocks.LADDER)) {
            before.putIfAbsent(pos, existing);
            after.put(pos, existing);
            return true;
        }
        // Only the chamber immediately inside the bedrock shell may excavate bedrock.
        if (!natural(existing)
                && !clearable(existing)
                && !(pos.getY() >= roofY - 4 && bedrock(existing))) return false;
        if (!existing.getFluidState().isEmpty()) return false;
        before.putIfAbsent(pos, existing);
        after.put(pos, replacement);
        return true;
    }

    private static boolean bedrock(BlockState state) {
        return state.is(Blocks.BEDROCK) || state.is(CustomBlocks.CRACKED_BEDROCK.get());
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
                || state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }

    private static boolean passable(BlockState state) {
        return state.isAir()
                || state.is(CustomBlocks.GLIMMERGRASS.get())
                || state.is(CustomBlocks.CHARRED_GRASS.get())
                || state.is(CustomBlocks.ASHY_CHARRED_GRASS.get())
                || state.is(CustomBlocks.UNDERSPROUTS.get())
                || state.is(CustomBlocks.GLIMMERSHROOM.get());
    }

    private static boolean clearable(BlockState state) {
        return passable(state) || state.is(CustomBlocks.SCORCHED_BERRY_BUSH.get());
    }
}
