package com.freeranger.dark_caverns.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;

final class CavernSpawnRules {
    private CavernSpawnRules() {}

    static boolean creatureOn(
            ServerLevelAccessor level,
            MobSpawnType reason,
            BlockPos pos,
            RandomSource random,
            int oneInChance,
            TagKey<Block> validGround) {
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) {
            return true;
        }
        return random.nextInt(oneInChance) == 0 && level.getBlockState(pos.below()).is(validGround);
    }
}
