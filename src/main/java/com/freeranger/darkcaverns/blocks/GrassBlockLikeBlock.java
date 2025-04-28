package com.freeranger.darkcaverns.blocks;

import com.freeranger.darkcaverns.DarkCaverns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;

public class GrassBlockLikeBlock extends GrassBlock {
    private String growFeature;

    public GrassBlockLikeBlock(Properties properties, String growFeature) {
        super(properties);
        this.growFeature = growFeature;
    }

    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.above();
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(DarkCaverns.MODID, growFeature);
        Optional<Holder.Reference<PlacedFeature>> val = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).get(
            ResourceKey.create(Registries.PLACED_FEATURE, loc)
        );

        label51:
        for(int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;

            for(int j = 0; j < i / 16; ++j) {
                blockpos1 = blockpos1.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if(!level.getBlockState(blockpos1.below()).is(this) || level.getBlockState(blockpos1).isCollisionShapeFullBlock(level, blockpos1)) {
                    continue label51;
                }
            }

            BlockState blockstate1 = level.getBlockState(blockpos1);
            if(blockstate1.isAir() && val.isPresent()) {
                val.get().value().place(
                    level,
                    level.getChunkSource().getGenerator(),
                    random,
                    blockpos1
                );
            }
        }

    }
}
