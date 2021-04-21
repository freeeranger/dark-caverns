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

public class CustomFlatBigMushroomFeature extends Feature<BigMushroomFeatureConfig> {
    public CustomFlatBigMushroomFeature(Codec<BigMushroomFeatureConfig> p_i231923_1_) {
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
            if (!isDirt(block) && !block.is(CustomBlocks.SHROOMGRASS_BLOCK.get())) {
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
        int i = 0;
        if (p_225563_4_ < p_225563_2_ && p_225563_4_ >= p_225563_2_ - 3) {
            i = p_225563_3_;
        } else if (p_225563_4_ == p_225563_2_) {
            i = p_225563_3_;
        }

        return i;
    }

    protected void makeCap(IWorld world, Random rand, BlockPos pos, int i1, BlockPos.Mutable pos2, BigMushroomFeatureConfig config) {
        int i = config.foliageRadius;

        for(int j = -i; j <= i; ++j) {
            for(int k = -i; k <= i; ++k) {
                boolean flag = j == -i;
                boolean flag1 = j == i;
                boolean flag2 = k == -i;
                boolean flag3 = k == i;
                boolean flag4 = flag || flag1;
                boolean flag5 = flag2 || flag3;
                if (!flag4 || !flag5) {
                    pos2.setWithOffset(pos, j, i1, k);
                    if (world.getBlockState(pos2).canBeReplacedByLeaves(world, pos2)) {
                        boolean flag6 = flag || flag5 && j == 1 - i;
                        boolean flag7 = flag1 || flag5 && j == i - 1;
                        boolean flag8 = flag2 || flag4 && k == 1 - i;
                        boolean flag9 = flag3 || flag4 && k == i - 1;
                        this.setBlock(world, pos2, config.capProvider.getState(rand, pos).setValue(HugeMushroomBlock.WEST, Boolean.valueOf(flag6)).setValue(HugeMushroomBlock.EAST, Boolean.valueOf(flag7)).setValue(HugeMushroomBlock.NORTH, Boolean.valueOf(flag8)).setValue(HugeMushroomBlock.SOUTH, Boolean.valueOf(flag9)));
                    }
                }
            }
        }

    }
}

