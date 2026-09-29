package com.freeranger.dark_caverns.generation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.HugeRedMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

/** A tall glimmershroom that refuses to generate unless its entire crown fits. */
public final class TallGlimmershroomFeature extends HugeRedMushroomFeature {
    public TallGlimmershroomFeature() {
        super(HugeMushroomFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<HugeMushroomFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        HugeMushroomFeatureConfiguration config = context.config();
        int height = getTreeHeight(random);

        if (!hasClearance(level, origin, height, config.foliageRadius)) return false;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        makeCap(level, random, origin, height, mutable, config);
        placeTrunk(level, random, origin, config, height, mutable);
        return true;
    }

    private static boolean hasClearance(
            LevelAccessor level, BlockPos origin, int height, int foliageRadius) {
        int baseY = origin.getY();
        if (baseY < level.getMinBuildHeight() + 1
                || baseY + height + 1 >= level.getMaxBuildHeight()) return false;

        BlockState ground = level.getBlockState(origin.below());
        if (!isDirt(ground) && !ground.is(BlockTags.MUSHROOM_GROW_BLOCK)) return false;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int y = 0; y < height; y++) {
            if (!canReplace(level, mutable.setWithOffset(origin, 0, y, 0))) return false;
        }

        for (int y = height - 3; y <= height; y++) {
            int radius = y < height ? foliageRadius : foliageRadius - 1;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    boolean xEdge = x == -radius || x == radius;
                    boolean zEdge = z == -radius || z == radius;
                    if (y >= height || xEdge != zEdge) {
                        if (!canReplace(level, mutable.setWithOffset(origin, x, y, z))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean canReplace(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES);
    }
}
