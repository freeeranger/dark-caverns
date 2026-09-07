package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/** Builds the mirrored floor-and-ceiling stone spikes used by the original dimension. */
public final class SpikeFeature extends Feature<BlockStateConfiguration> {
    public SpikeFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        while (level.isEmptyBlock(origin) && origin.getY() > level.getMinBuildHeight() + 2) {
            origin = origin.below();
        }

        BlockState spikeState = context.config().state;
        Block source = spikeState.getBlock();
        if (!level.getBlockState(origin).is(source)) {
            return false;
        }

        origin = origin.above(random.nextInt(4));
        int height = random.nextInt(4) + 7;
        int radius = height / 4 + random.nextInt(2);

        for (int yOffset = 0; yOffset < height; yOffset++) {
            float scaledRadius = (1.0F - (float) yOffset / (float) height) * (float) radius;
            int extent = Mth.ceil(scaledRadius);

            for (int xOffset = -extent; xOffset <= extent; xOffset++) {
                float xDistance = (float) Mth.abs(xOffset) - 0.25F;
                for (int zOffset = -extent; zOffset <= extent; zOffset++) {
                    float zDistance = (float) Mth.abs(zOffset) - 0.25F;
                    boolean insideRadius =
                            xOffset == 0 && zOffset == 0
                                    || xDistance * xDistance + zDistance * zDistance
                                            <= scaledRadius * scaledRadius;
                    boolean keepEdge =
                            xOffset != -extent
                                            && xOffset != extent
                                            && zOffset != -extent
                                            && zOffset != extent
                                    || random.nextFloat() <= 0.999F;
                    if (!insideRadius || !keepEdge) {
                        continue;
                    }

                    replace(level, origin.offset(xOffset, yOffset, zOffset), spikeState);
                    if (yOffset != 0 && extent > 1) {
                        replace(level, origin.offset(xOffset, -yOffset, zOffset), spikeState);
                    }
                }
            }
        }

        int foundationRadius = Mth.clamp(radius - 1, 0, 1);
        for (int xOffset = -foundationRadius; xOffset <= foundationRadius; xOffset++) {
            for (int zOffset = -foundationRadius; zOffset <= foundationRadius; zOffset++) {
                BlockPos foundation = origin.offset(xOffset, -1, zOffset);
                int remaining =
                        Math.abs(xOffset) == 1 && Math.abs(zOffset) == 1 ? random.nextInt(5) : 50;

                while (foundation.getY() > level.getMinBuildHeight() + 50) {
                    BlockState state = level.getBlockState(foundation);
                    if (!state.isAir() && !isDirt(state) && !state.is(source)) {
                        break;
                    }

                    setBlock(level, foundation, spikeState);
                    foundation = foundation.below();
                    if (--remaining <= 0) {
                        foundation = foundation.below(random.nextInt(5) + 1);
                        remaining = random.nextInt(5);
                    }
                }
            }
        }

        return true;
    }

    private void replace(WorldGenLevel level, BlockPos pos, BlockState spikeState) {
        BlockState state = level.getBlockState(pos);
        Block source = spikeState.getBlock();
        if (state.isAir() || isDirt(state) || state.is(source)) {
            setBlock(level, pos, spikeState);
        }
    }
}
