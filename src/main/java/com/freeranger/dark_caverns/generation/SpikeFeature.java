package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class SpikeFeature extends Feature<NoFeatureConfig> {
    public SpikeFeature(Codec<NoFeatureConfig> noFeatureConfig) {
        super(noFeatureConfig);
    }

    public boolean place(ISeedReader seedReader, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig noFeatureConfig) {
        while(seedReader.isEmptyBlock(pos) && pos.getY() > 2) {
            pos = pos.below();
        }

        if (!seedReader.getBlockState(pos).is(CustomBlocks.LUMINITE_BLOCK.get())) {
            return false;
        } else {
            pos = pos.above(rand.nextInt(4));
            int i = rand.nextInt(4) + 7;
            int j = i / 4 + rand.nextInt(2);

            for(int k = 0; k < i; ++k) {
                float f = (1.0f - (float)k / (float)i) * (float)j;
                int l = MathHelper.ceil(f);

                for(int i1 = -l; i1 <= l; ++i1) {
                    float f1 = (float)MathHelper.abs(i1) - 0.25f;

                    for(int j1 = -l; j1 <= l; ++j1) {
                        float f2 = (float)MathHelper.abs(j1) - 0.25f;
                        if ((i1 == 0 && j1 == 0 || !(f1 * f1 + f2 * f2 > f * f)) && (i1 != -l && i1 != l && j1 != -l && j1 != l || !(rand.nextFloat() > 0.999f))) {
                            BlockState blockstate = seedReader.getBlockState(pos.offset(i1, k, j1));
                            Block block = blockstate.getBlock();
                            if (blockstate.isAir(seedReader, pos.offset(i1, k, j1)) || isDirt(block) || block == CustomBlocks.LUMINITE_BLOCK.get()) {
                                this.setBlock(seedReader, pos.offset(i1, k, j1), CustomBlocks.LUMINITE_BLOCK.get().defaultBlockState());
                            }

                            if (k != 0 && l > 1) {
                                blockstate = seedReader.getBlockState(pos.offset(i1, -k, j1));
                                block = blockstate.getBlock();
                                if (blockstate.isAir(seedReader, pos.offset(i1, -k, j1)) || isDirt(block) || block == CustomBlocks.LUMINITE_BLOCK.get()) {
                                    this.setBlock(seedReader, pos.offset(i1, -k, j1), CustomBlocks.LUMINITE_BLOCK.get().defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }

            int k1 = j - 1;
            if (k1 < 0) {
                k1 = 0;
            } else if (k1 > 1) {
                k1 = 1;
            }

            for(int l1 = -k1; l1 <= k1; ++l1) {
                for(int i2 = -k1; i2 <= k1; ++i2) {
                    BlockPos blockpos = pos.offset(l1, -1, i2);
                    int j2 = 50;
                    if (Math.abs(l1) == 1 && Math.abs(i2) == 1) {
                        j2 = rand.nextInt(5);
                    }

                    while(blockpos.getY() > 50) {
                        BlockState blockstate1 = seedReader.getBlockState(blockpos);
                        Block block1 = blockstate1.getBlock();
                        if (!blockstate1.isAir(seedReader, blockpos) && !isDirt(block1) && block1 != CustomBlocks.LUMINITE_BLOCK.get()) {
                            break;
                        }

                        this.setBlock(seedReader, blockpos, CustomBlocks.LUMINITE_BLOCK.get().defaultBlockState());
                        blockpos = blockpos.below();
                        --j2;
                        if (j2 <= 0) {
                            blockpos = blockpos.below(rand.nextInt(5) + 1);
                            j2 = rand.nextInt(5);
                        }
                    }
                }
            }

            return true;
        }
    }
}
