package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Small mud patches on broad, open Hallow shelves. */
public final class HallowMudFeature extends Feature<NoneFeatureConfiguration> {
    public HallowMudFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!openFloor(level, origin)) return false;

        int rx = 2 + random.nextInt(4);
        int rz = 2 + random.nextInt(4);
        double phase = random.nextDouble() * Math.PI * 2;
        Set<BlockPos> mud = new LinkedHashSet<>();
        for (int x = -rx; x <= rx; x++) {
            for (int z = -rz; z <= rz; z++) {
                double angle = Math.atan2(z, x);
                double edge =
                        1 + .16 * Math.sin(angle * 3 + phase) + .08 * Math.cos(angle * 5 - phase);
                double radial = Math.sqrt(x * x / (double) (rx * rx) + z * z / (double) (rz * rz));
                BlockPos air = origin.offset(x, 0, z);
                if (radial <= edge && openFloor(level, air)) mud.add(air.below());
            }
        }
        if (mud.size() < 8) return false;
        mud.forEach(pos -> level.setBlock(pos, Blocks.MUD.defaultBlockState(), 2));
        return true;
    }

    private static boolean openFloor(WorldGenLevel level, BlockPos air) {
        if (!level.getBiome(air).is(DarkCaverns.id("tangled_hallow"))
                || !level.getBlockState(air).isAir()
                || !level.getBlockState(air.above()).isAir()
                || !level.ensureCanWrite(air.below())
                || !level.getBlockState(air.below()).is(CustomBlocks.OVERGROWN_CARFSTONE.get()))
            return false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = air.relative(direction);
            if (!level.getBlockState(neighbor).isAir()
                    || !level.getBlockState(neighbor.below())
                            .is(CustomBlocks.OVERGROWN_CARFSTONE.get())) return false;
        }
        return true;
    }
}
