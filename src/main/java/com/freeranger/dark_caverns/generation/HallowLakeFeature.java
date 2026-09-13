package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
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
        int rx = 5 + context.random().nextInt(3);
        int rz = 4 + context.random().nextInt(3);
        double phase = context.random().nextDouble() * Math.PI * 2;
        // Prefer a lake, but fit a smaller pool when a shelf is not broad enough.
        for (int shrink = 0; shrink <= 2; shrink++) {
            for (int drop = 2; drop <= 3; drop++) {
                if (basin(
                        level,
                        origin.below(drop),
                        Math.max(3, rx - shrink),
                        Math.max(3, rz - shrink),
                        phase)) return true;
            }
        }
        return false;
    }

    private static boolean basin(
            WorldGenLevel level, BlockPos center, int rx, int rz, double phase) {
        Set<BlockPos> water = new LinkedHashSet<>();
        Set<BlockPos> air = new LinkedHashSet<>();
        for (int x = -rx; x <= rx; x++) {
            for (int z = -rz; z <= rz; z++) {
                double angle = Math.atan2(z, x);
                double edge =
                        1 + .10 * Math.sin(angle * 3 + phase) + .06 * Math.cos(angle * 5 - phase);
                double radial = Math.sqrt(x * x / (double) (rx * rx) + z * z / (double) (rz * rz));
                if (radial > edge) continue;
                BlockPos surface = center.offset(x, 0, z);
                if (!level.getBiome(surface).is(DarkCaverns.id("tangled_hallow"))) return false;
                int depth = 1 + (int) Math.floor(Math.max(0, 1 - radial) * 2.5);
                for (int y = 0; y < depth; y++) water.add(surface.below(y));
                for (int y = 1; y <= 3; y++) air.add(surface.above(y));
                if (!level.getBlockState(surface.above(4)).isAir()) return false;
            }
        }
        for (BlockPos pos : water) {
            if (!level.ensureCanWrite(pos) || !natural(level.getBlockState(pos))) return false;
            // Check both the retaining wall and its backing. Do not manufacture dams over air,
            // replace ores/structures, or let water touch existing lava or other lakes.
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockPos wall = pos.relative(direction);
                if (!water.contains(wall)
                        && (!natural(level.getBlockState(wall))
                                || !natural(level.getBlockState(wall.relative(direction)))))
                    return false;
            }
        }
        for (BlockPos pos : air) {
            BlockState state = level.getBlockState(pos);
            if (!level.ensureCanWrite(pos) || (!state.isAir() && !natural(state))) return false;
        }
        // Commit only once every water cell has a dry, solid containment envelope.
        air.forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2));
        water.forEach(pos -> level.setBlock(pos, Blocks.WATER.defaultBlockState(), 2));
        water.forEach(
                pos -> level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level)));
        return true;
    }

    private static boolean natural(BlockState state) {
        return state.is(CustomBlocks.CARFSTONE.get())
                || state.is(CustomBlocks.OVERGROWN_CARFSTONE.get());
    }
}
