package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/** Optional fields keep existing state-only configured features compatible. */
public record CavernFormationConfiguration(
        BlockState state,
        int minGap,
        int maxHeight,
        int maxRadius,
        float columnChance,
        float opposingChance,
        float ceilingChance)
        implements FeatureConfiguration {
    public static final Codec<CavernFormationConfiguration> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            BlockState.CODEC
                                                    .fieldOf("state")
                                                    .forGetter(CavernFormationConfiguration::state),
                                            Codec.intRange(6, 64)
                                                    .optionalFieldOf("min_gap", 8)
                                                    .forGetter(
                                                            CavernFormationConfiguration::minGap),
                                            Codec.intRange(4, 96)
                                                    .optionalFieldOf("max_height", 64)
                                                    .forGetter(
                                                            CavernFormationConfiguration
                                                                    ::maxHeight),
                                            Codec.intRange(1, 10)
                                                    .optionalFieldOf("max_radius", 8)
                                                    .forGetter(
                                                            CavernFormationConfiguration
                                                                    ::maxRadius),
                                            Codec.floatRange(0, 1)
                                                    .optionalFieldOf("column_chance", 0.025F)
                                                    .forGetter(
                                                            CavernFormationConfiguration
                                                                    ::columnChance),
                                            Codec.floatRange(0, 1)
                                                    .optionalFieldOf("opposing_chance", 0.18F)
                                                    .forGetter(
                                                            CavernFormationConfiguration
                                                                    ::opposingChance),
                                            Codec.floatRange(0, 1)
                                                    .optionalFieldOf("ceiling_chance", 0.45F)
                                                    .forGetter(
                                                            CavernFormationConfiguration
                                                                    ::ceilingChance))
                                    .apply(instance, CavernFormationConfiguration::new));
}
