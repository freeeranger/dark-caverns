package com.freeranger.darkcaverns.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

public class SpringLikeFeature extends Feature<SpringConfiguration> {
    public SpringLikeFeature(Codec<SpringConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SpringConfiguration> context) {
        SpringConfiguration config = context.config();
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();

        var surroundingBlocks = new BlockState[]{
            world.getBlockState(pos.below()),
            world.getBlockState(pos.above()),
            world.getBlockState(pos.north()),
            world.getBlockState(pos.south()),
            world.getBlockState(pos.west()),
            world.getBlockState(pos.east())
        };

        int filledNeighbors = 0;
        for(BlockState surroundingBlock : surroundingBlocks) {
            if(surroundingBlock.is(config.validBlocks))
                filledNeighbors++;
        }

        if(filledNeighbors < config.rockCount || (6 - filledNeighbors) < config.holeCount)
            return false;

        world.setBlock(pos, config.state.createLegacyBlock(), 2);
        world.scheduleTick(pos, config.state.getType(), 0);

        return true;
    }
}
