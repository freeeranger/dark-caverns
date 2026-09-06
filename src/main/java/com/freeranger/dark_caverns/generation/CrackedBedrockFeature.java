package com.freeranger.dark_caverns.generation;

import com.freeranger.dark_caverns.core.DarkCavernsConfig;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

/** Config-driven equivalent of the legacy bedrock ore feature. */
public final class CrackedBedrockFeature extends Feature<NoneFeatureConfiguration> {
    public CrackedBedrockFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        int attempts = DarkCavernsConfig.COMMON.crackedBedrockVeinCount.get();
        OreConfiguration ore = new OreConfiguration(
                new BlockMatchTest(Blocks.BEDROCK),
                CustomBlocks.CRACKED_BEDROCK.get().defaultBlockState(),
                DarkCavernsConfig.COMMON.crackedBedrockVeinSize.get()
        );
        boolean placed = false;
        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos origin = context.origin().offset(
                    context.random().nextInt(16),
                    context.random().nextInt(5),
                    context.random().nextInt(16)
            );
            placed |= Feature.ORE.place(
                    ore,
                    context.level(),
                    context.chunkGenerator(),
                    context.random(),
                    origin
            );
        }
        return placed;
    }
}
