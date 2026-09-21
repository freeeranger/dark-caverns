package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

/**
 * Contained, volcanic calderas cut into elevated cavern shelves in Molten Depths. Lined with magma,
 * cooled ashy crust, and stepping stone crossings so navigation is preserved. Never spills over
 * cliffs or cascades into open air.
 */
public final class MoltenCalderaFeature extends Feature<NoneFeatureConfiguration> {
    public MoltenCalderaFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        var origin = context.origin();
        var level = context.level();
        var random = context.random();

        // Generate on mid to high elevated shelves well above the Y < 11 floor lava band
        if (origin.getY() < 24
                || origin.getY() > level.getMaxBuildHeight() - 16
                || !level.getBlockState(origin).isAir()
                || !natural(level.getBlockState(origin.below()))) {
            return false;
        }

        boolean large = random.nextFloat() < 0.85F;
        int rx = large ? 8 + random.nextInt(5) : 5 + random.nextInt(3);
        int rz = large ? 7 + random.nextInt(5) : 5 + random.nextInt(3);
        int minRadius = large ? 6 : 4;
        int minArea = large ? 45 : 22;
        int minSpan = large ? 7 : 4;
        double phase = random.nextDouble() * Math.PI * 2;

        for (int shrink = 0; shrink <= (large ? 2 : 1); shrink++) {
            if (buildCaldera(
                    level,
                    origin.below(),
                    Math.max(minRadius, rx - shrink),
                    Math.max(minRadius, rz - shrink),
                    phase,
                    minArea,
                    minSpan,
                    random)) {
                return true;
            }
        }
        return false;
    }

    private static boolean buildCaldera(
            WorldGenLevel level,
            BlockPos center,
            int rx,
            int rz,
            double phase,
            int minArea,
            int minSpan,
            RandomSource random) {
        Set<BlockPos> surfaceLava = new LinkedHashSet<>();
        Set<BlockPos> candidates = new LinkedHashSet<>();

        for (int x = -rx; x <= rx; x++) {
            for (int z = -rz; z <= rz; z++) {
                double angle = Math.atan2(z, x);
                double edge =
                        1 + 0.12 * Math.sin(angle * 3 + phase) + 0.08 * Math.cos(angle * 4 - phase);
                double radial = Math.sqrt(x * x / (double) (rx * rx) + z * z / (double) (rz * rz));
                if (radial > edge) continue;

                BlockPos surface = center.offset(x, 0, z);
                if (level.getBiome(surface).is(DarkCaverns.id("molten_depths"))
                        && safeExposedFloor(level, surface)) {
                    candidates.add(surface);
                }
            }
        }

        if (!candidates.contains(center)) return false;

        // Flood fill from center to keep one contiguous basin
        var queue = new ArrayDeque<BlockPos>();
        queue.add(center);
        surfaceLava.add(center);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = pos.relative(direction);
                if (candidates.contains(neighbor) && surfaceLava.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        int minX = surfaceLava.stream().mapToInt(BlockPos::getX).min().orElse(0);
        int maxX = surfaceLava.stream().mapToInt(BlockPos::getX).max().orElse(0);
        int minZ = surfaceLava.stream().mapToInt(BlockPos::getZ).min().orElse(0);
        int maxZ = surfaceLava.stream().mapToInt(BlockPos::getZ).max().orElse(0);
        if (surfaceLava.size() < minArea || maxX - minX < minSpan || maxZ - minZ < minSpan) {
            return false;
        }

        Set<BlockPos> lava = new LinkedHashSet<>();
        Set<BlockPos> air = new LinkedHashSet<>();
        Set<BlockPos> bed = new LinkedHashSet<>();
        Set<BlockPos> rim = new LinkedHashSet<>();
        Set<BlockPos> steppingStones = new LinkedHashSet<>();

        // Select a crossing line for stepping stones across larger calderas
        boolean hasCrossing = surfaceLava.size() >= 30;
        int crossingAxis = random.nextBoolean() ? 0 : 1; // 0 = X axis, 1 = Z axis

        for (BlockPos surface : surfaceLava) {
            int dx = surface.getX() - center.getX();
            int dz = surface.getZ() - center.getZ();
            double radial = Math.sqrt(dx * dx / (double) (rx * rx) + dz * dz / (double) (rz * rz));
            int depth = 1 + (radial < 0.65 ? 1 : 0);

            // Stepping stones: place occasional crust stepping stones across the lake
            boolean isSteppingStone =
                    hasCrossing
                            && ((crossingAxis == 0 && Math.abs(dz) <= 1 && (Math.abs(dx) % 3 == 0))
                                    || (crossingAxis == 1
                                            && Math.abs(dx) <= 1
                                            && (Math.abs(dz) % 3 == 0)))
                            && radial > 0.20
                            && radial < 0.85;

            if (isSteppingStone) {
                steppingStones.add(surface);
                // Under the stepping stone, place solid magma/carfstone
                bed.add(surface.below());
            } else {
                for (int y = 0; y < depth; y++) {
                    lava.add(surface.below(y));
                }
                bed.add(surface.below(depth));
            }

            for (int y = 1; y <= 3; y++) {
                air.add(surface.above(y));
            }

            // Find surrounding rim blocks
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = surface.relative(dir);
                if (!surfaceLava.contains(neighbor)) {
                    rim.add(neighbor);
                }
            }
        }

        // Strict containment check: every single lava cell must be completely contained
        for (BlockPos pos : lava) {
            if (!level.ensureCanWrite(pos) || !natural(level.getBlockState(pos))) {
                return false;
            }
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockPos wall = pos.relative(direction);
                if (!lava.contains(wall)
                        && !steppingStones.contains(wall)
                        && !natural(level.getBlockState(wall))) {
                    return false;
                }
            }
        }

        for (BlockPos pos : air) {
            BlockState state = level.getBlockState(pos);
            if (!level.ensureCanWrite(pos) || !state.isAir()) {
                return false;
            }
        }

        // All checks passed! Apply planned blocks
        air.forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2));

        // Basin bed: glowing magma and molten carfstone foundation
        for (BlockPos pos : bed) {
            if (level.ensureCanWrite(pos)) {
                BlockState bedState =
                        random.nextFloat() < 0.45F
                                ? Blocks.MAGMA_BLOCK.defaultBlockState()
                                : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
                level.setBlock(pos, bedState, 2);
            }
        }

        // Basin rim: only touch blocks that are directly exposed to air on the surface
        for (BlockPos pos : rim) {
            if (level.ensureCanWrite(pos)
                    && natural(level.getBlockState(pos))
                    && level.getBlockState(pos.above()).isAir()) {
                if (random.nextFloat() < 0.35F) {
                    level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                }
            }
        }

        // Stepping stones: natural volcanic rocks across the basin
        for (BlockPos pos : steppingStones) {
            BlockState stone =
                    random.nextBoolean()
                            ? Blocks.MAGMA_BLOCK.defaultBlockState()
                            : CustomBlocks.MOLTEN_CARFSTONE.get().defaultBlockState();
            level.setBlock(pos, stone, 2);
        }

        // Lava fluid
        lava.forEach(pos -> level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2));
        int tickDelay = lavaTickDelay(level);
        lava.forEach(pos -> level.scheduleTick(pos, Fluids.LAVA, tickDelay));

        return true;
    }

    private static int lavaTickDelay(WorldGenLevel level) {
        try {
            return Fluids.LAVA.getTickDelay(level);
        } catch (Throwable ignored) {
            return 30;
        }
    }

    private static boolean safeExposedFloor(WorldGenLevel level, BlockPos surface) {
        if (!natural(level.getBlockState(surface))) return false;
        for (int y = 1; y <= 3; y++) {
            if (!level.getBlockState(surface.above(y)).isAir()) return false;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!natural(level.getBlockState(surface.relative(direction)))) return false;
        }
        return true;
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.MOLTEN_CARFSTONE.get())
                || state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.ASHY_MOLTEN_CARFSTONE.get())
                || state.is(Blocks.MAGMA_BLOCK);
    }
}
