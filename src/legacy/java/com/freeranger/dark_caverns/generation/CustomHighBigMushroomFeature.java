package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BigMushroomFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class CustomHighBigMushroomFeature extends Feature<BigMushroomFeatureConfig> {
    public CustomHighBigMushroomFeature(Codec<BigMushroomFeatureConfig> p_i231923_1_) {
        super(p_i231923_1_);
    }

    protected void placeTrunk(IWorld p_227210_1_, Random p_227210_2_, BlockPos p_227210_3_, BigMushroomFeatureConfig p_227210_4_, int p_227210_5_, BlockPos.Mutable p_227210_6_) {
        for(int i = 0; i < p_227210_5_; ++i) {
            p_227210_6_.set(p_227210_3_).move(Direction.UP, i);
            if (p_227210_1_.getBlockState(p_227210_6_).canBeReplacedByLogs(p_227210_1_, p_227210_6_)) {
                this.setBlock(p_227210_1_, p_227210_6_, p_227210_4_.stemProvider.getState(p_227210_2_, p_227210_3_));
            }
        }

    }

    protected int getTreeHeight(Random p_227211_1_) {
        int i = p_227211_1_.nextInt(3) + 4;
        if (p_227211_1_.nextInt(12) == 0) {
            i *= 2;
        }

        return i;
    }

    protected boolean isValidPosition(IWorld p_227209_1_, BlockPos p_227209_2_, int p_227209_3_, BlockPos.Mutable p_227209_4_, BigMushroomFeatureConfig p_227209_5_) {
        int i = p_227209_2_.getY();
        if (i >= 1 && i + p_227209_3_ + 1 < 256) {
            Block block = p_227209_1_.getBlockState(p_227209_2_.below()).getBlock();
            if (!isDirt(block) && !block.is(CustomBlocks.GLIMMERGRASS_BLOCK.get())) {
                return false;
            } else {
                for(int j = 0; j <= p_227209_3_; ++j) {
                    int k = this.getTreeRadiusForHeight(-1, -1, p_227209_5_.foliageRadius, j);

                    for(int l = -k; l <= k; ++l) {
                        for(int i1 = -k; i1 <= k; ++i1) {
                            BlockState blockstate = p_227209_1_.getBlockState(p_227209_4_.setWithOffset(p_227209_2_, l, j, i1));
                            if (!blockstate.isAir(p_227209_1_, p_227209_4_.setWithOffset(p_227209_2_, l, j, i1)) && !blockstate.is(BlockTags.LEAVES)) {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, BigMushroomFeatureConfig p_241855_5_) {
        int i = this.getTreeHeight(p_241855_3_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        if (!this.isValidPosition(p_241855_1_, p_241855_4_, i, blockpos$mutable, p_241855_5_)) {
            return false;
        } else {
            this.makeCap(p_241855_1_, p_241855_3_, p_241855_4_, i, blockpos$mutable, p_241855_5_);
            this.placeTrunk(p_241855_1_, p_241855_3_, p_241855_4_, p_241855_5_, i, blockpos$mutable);
            return true;
        }
    }

    protected int getTreeRadiusForHeight(int p_225563_1_, int p_225563_2_, int p_225563_3_, int p_225563_4_) {
        return p_225563_4_ <= 3 ? 0 : p_225563_3_;
    }

    protected void makeCap(IWorld world, Random random, BlockPos pos, int i2, BlockPos.Mutable pos2, BigMushroomFeatureConfig config) {
        for(int i = i2 - 3; i <= i2; ++i) {
            int j = i < i2 ? config.foliageRadius : config.foliageRadius - 1;
            int k = config.foliageRadius - 2;

            for(int l = -j; l <= j; ++l) {
                for(int i1 = -j; i1 <= j; ++i1) {
                    boolean flag = l == -j;
                    boolean flag1 = l == j;
                    boolean flag2 = i1 == -j;
                    boolean flag3 = i1 == j;
                    boolean flag4 = flag || flag1;
                    boolean flag5 = flag2 || flag3;
                    if (i >= i2 || flag4 != flag5) {
                        pos2.setWithOffset(pos, l, i, i1);
                        if (world.getBlockState(pos2).canBeReplacedByLeaves(world, pos2)) {
                            this.setBlock(world, pos2, config.capProvider.getState(random, pos).setValue(HugeMushroomBlock.UP, Boolean.valueOf(i >= i2 - 1)).setValue(HugeMushroomBlock.WEST, Boolean.valueOf(l < -k)).setValue(HugeMushroomBlock.EAST, Boolean.valueOf(l > k)).setValue(HugeMushroomBlock.NORTH, Boolean.valueOf(i1 < -k)).setValue(HugeMushroomBlock.SOUTH, Boolean.valueOf(i1 > k)));
                        }
                    }
                }
            }
        }

    }
}

