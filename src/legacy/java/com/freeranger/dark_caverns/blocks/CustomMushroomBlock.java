package com.freeranger.dark_caverns.blocks;

import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class CustomMushroomBlock extends MushroomBlock {
    public CustomMushroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean growMushroom(ServerWorld world, BlockPos pos, BlockState state, Random rand) {
        world.removeBlock(pos, false);
        ConfiguredFeature<?, ?> feature;
        if (this == CustomBlocks.GLIMMERSHROOM.get()) {
            if(rand.nextBoolean())
                feature = CustomFeatures.ConfiguredFeatures.HUGE_MUSHROOM;
            else feature = CustomFeatures.ConfiguredFeatures.HUGE_HIGH_MUSHROOM;
        } else {
            world.setBlock(pos, state, 3);
            return false;
        }

        if (feature.place(world, world.getChunkSource().getGenerator(), rand, pos)) {
            return true;
        } else {
            world.setBlock(pos, state, 3);
            return false;
        }
    }
}
