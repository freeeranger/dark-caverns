package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Generates cracked bedrock exclusively on the top surface of the Overworld bottom bedrock layer,
 * ensuring it is never buried underneath other bedrock.
 */
public final class CrackedBedrockFeature extends Feature<NoneFeatureConfiguration> {
    public CrackedBedrockFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int minY = level.getMinBuildHeight();

        // Bottom bedrock layer in the Overworld is between minY (-64) and minY + 5 (-59).
        if (minY > -60) {
            return false;
        }

        int attempts = ServerConfig.crackedBedrockVeinCount();
        int maxPatchSize = ServerConfig.crackedBedrockVeinSize();
        BlockState crackedBedrock = CustomBlocks.CRACKED_BEDROCK.get().defaultBlockState();
        boolean placedAny = false;

        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos surfacePos = null;
            for (int trial = 0; trial < 8; trial++) {
                int testX = origin.getX() + random.nextInt(16);
                int testZ = origin.getZ() + random.nextInt(16);
                BlockPos found = findSurfaceBedrock(level, testX, testZ, minY);
                if (found != null) {
                    surfacePos = found;
                    break;
                }
            }

            if (surfacePos == null) {
                continue;
            }

            level.setBlock(surfacePos, crackedBedrock, 2);
            placedAny = true;
            int placedCount = 1;

            int radius = 1 + (maxPatchSize > 4 ? 1 : 0);
            for (int dx = -radius; dx <= radius && placedCount < maxPatchSize; dx++) {
                for (int dz = -radius; dz <= radius && placedCount < maxPatchSize; dz++) {
                    if (dx == 0 && dz == 0) {
                        continue;
                    }
                    if (dx * dx + dz * dz > radius * radius + 1) {
                        continue;
                    }
                    if (random.nextFloat() < 0.25F) {
                        continue;
                    }

                    BlockPos neighbor =
                            findSurfaceBedrock(
                                    level, surfacePos.getX() + dx, surfacePos.getZ() + dz, minY);
                    if (neighbor != null && Math.abs(neighbor.getY() - surfacePos.getY()) <= 2) {
                        level.setBlock(neighbor, crackedBedrock, 2);
                        placedCount++;
                    }
                }
            }
        }
        return placedAny;
    }

    private static BlockPos findSurfaceBedrock(WorldGenLevel level, int x, int z, int minY) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, minY + 8, z);
        for (int y = minY + 8; y >= minY; y--) {
            pos.setY(y);
            BlockState state = level.getBlockState(pos);
            if (state.is(CustomBlocks.CRACKED_BEDROCK.get())) {
                return null;
            }
            if (state.is(Blocks.BEDROCK)) {
                BlockState above = level.getBlockState(pos.above());
                if (!above.is(Blocks.BEDROCK) && !above.is(CustomBlocks.CRACKED_BEDROCK.get())) {
                    return pos.immutable();
                }
                return null;
            }
        }
        return null;
    }
}
