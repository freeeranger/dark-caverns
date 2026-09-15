package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

/** Shallow, sealed lakes cut into broad dry shelves. Never fills a ravine or plugs a leak. */
public final class HallowLakeFeature extends Feature<NoneFeatureConfiguration> {
    public HallowLakeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var origin = context.origin();
        var level = context.level();
        // Keep a substantial dry separation from the dimension's Y < 11 lava band.
        if (origin.getY() < 28
                || origin.getY() > level.getMaxBuildHeight() - 8
                || !level.getBlockState(origin).isAir()
                || !natural(level.getBlockState(origin.below()))) return false;
        // Most successful placements should read as lakes. Small ponds remain as occasional
        // accents rather than winning simply because they fit almost every shelf.
        boolean large = context.random().nextFloat() < 0.90F;
        int rx = large ? 9 + context.random().nextInt(5) : 4 + context.random().nextInt(3);
        int rz = large ? 7 + context.random().nextInt(5) : 4 + context.random().nextInt(3);
        int minRadius = large ? 6 : 3;
        int minArea = large ? 55 : 18;
        int minSpan = large ? 7 : 4;
        double phase = context.random().nextDouble() * Math.PI * 2;
        // The water surface replaces the exposed shelf itself. The previous implementation
        // started one or two blocks below it, which made otherwise successful lakes look buried.
        for (int shrink = 0; shrink <= (large ? 2 : 1); shrink++) {
            if (basin(
                    level,
                    origin.below(),
                    Math.max(minRadius, rx - shrink),
                    Math.max(minRadius, rz - shrink),
                    phase,
                    minArea,
                    minSpan,
                    context.random())) return true;
        }
        return false;
    }

    private static boolean basin(
            WorldGenLevel level,
            BlockPos center,
            int rx,
            int rz,
            double phase,
            int minArea,
            int minSpan,
            net.minecraft.util.RandomSource random) {
        Set<BlockPos> water = new LinkedHashSet<>();
        Set<BlockPos> surfaceWater = new LinkedHashSet<>();
        Set<BlockPos> air = new LinkedHashSet<>();
        Set<BlockPos> candidates = new LinkedHashSet<>();
        for (int x = -rx; x <= rx; x++) {
            for (int z = -rz; z <= rz; z++) {
                double angle = Math.atan2(z, x);
                double edge =
                        1 + .10 * Math.sin(angle * 3 + phase) + .06 * Math.cos(angle * 5 - phase);
                double radial = Math.sqrt(x * x / (double) (rx * rx) + z * z / (double) (rz * rz));
                if (radial > edge) continue;
                BlockPos surface = center.offset(x, 0, z);
                if (level.getBiome(surface).is(DarkCaverns.id("tangled_hallow"))
                        && safeExposedFloor(level, surface)) candidates.add(surface);
            }
        }
        if (!candidates.contains(center)) return false;
        var queue = new ArrayDeque<BlockPos>();
        queue.add(center);
        surfaceWater.add(center);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = pos.relative(direction);
                if (candidates.contains(neighbor) && surfaceWater.add(neighbor))
                    queue.add(neighbor);
            }
        }
        int minX = surfaceWater.stream().mapToInt(BlockPos::getX).min().orElse(0);
        int maxX = surfaceWater.stream().mapToInt(BlockPos::getX).max().orElse(0);
        int minZ = surfaceWater.stream().mapToInt(BlockPos::getZ).min().orElse(0);
        int maxZ = surfaceWater.stream().mapToInt(BlockPos::getZ).max().orElse(0);
        if (surfaceWater.size() < minArea || maxX - minX < minSpan || maxZ - minZ < minSpan)
            return false;
        for (BlockPos surface : surfaceWater) {
            int x = surface.getX() - center.getX();
            int z = surface.getZ() - center.getZ();
            double radial = Math.sqrt(x * x / (double) (rx * rx) + z * z / (double) (rz * rz));
            int depth = 1 + (int) Math.floor(Math.max(0, 1 - radial) * 2.5);
            for (int y = 0; y < depth; y++) water.add(surface.below(y));
            for (int y = 1; y <= 3; y++) air.add(surface.above(y));
        }
        for (BlockPos pos : water) {
            if (!level.ensureCanWrite(pos) || !natural(level.getBlockState(pos))) return false;
            // Require an existing natural retaining wall. We do not build dams, replace
            // ores/structures, or let water touch existing lava or other lakes.
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockPos wall = pos.relative(direction);
                if (!water.contains(wall) && !natural(level.getBlockState(wall))) return false;
            }
        }
        for (BlockPos pos : air) {
            BlockState state = level.getBlockState(pos);
            // A surface lake may replace the exposed shelf, never excavate a hidden chamber.
            if (!level.ensureCanWrite(pos) || !state.isAir()) return false;
        }
        // Commit only once every water cell has a dry, solid containment envelope.
        air.forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2));
        water.forEach(pos -> level.setBlock(pos, Blocks.WATER.defaultBlockState(), 2));
        water.forEach(
                pos -> level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level)));
        placeLilyPads(level, center, surfaceWater, random);
        return true;
    }

    private static boolean safeExposedFloor(WorldGenLevel level, BlockPos surface) {
        if (!natural(level.getBlockState(surface))) return false;
        for (int y = 1; y <= 4; y++)
            if (!level.getBlockState(surface.above(y)).isAir()) return false;
        // Leave an existing block-wide shore at shelf edges instead of rejecting the whole lake
        // later because one selected water tile borders a drop.
        for (Direction direction : Direction.Plane.HORIZONTAL)
            if (!natural(level.getBlockState(surface.relative(direction)))) return false;
        return true;
    }

    private static void placeLilyPads(
            WorldGenLevel level,
            BlockPos center,
            Set<BlockPos> surfaceWater,
            net.minecraft.util.RandomSource random) {
        var pads = new LinkedHashSet<BlockPos>();
        for (BlockPos water : surfaceWater) {
            int neighbors = 0;
            for (Direction direction : Direction.Plane.HORIZONTAL)
                if (surfaceWater.contains(water.relative(direction))) neighbors++;
            if (neighbors >= 2 && random.nextFloat() < 0.18F) pads.add(water.above());
        }
        // Even a small lake should communicate its identity immediately.
        if (pads.size() < 3) {
            surfaceWater.stream()
                    .sorted(
                            Comparator.comparingInt(
                                            (BlockPos pos) ->
                                                    Math.abs(pos.getX() - center.getX())
                                                            + Math.abs(pos.getZ() - center.getZ()))
                                    .thenComparingLong(BlockPos::asLong))
                    .limit(3 - pads.size())
                    .map(BlockPos::above)
                    .forEach(pads::add);
        }
        pads.forEach(pos -> level.setBlock(pos, Blocks.LILY_PAD.defaultBlockState(), 2));
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }
}
