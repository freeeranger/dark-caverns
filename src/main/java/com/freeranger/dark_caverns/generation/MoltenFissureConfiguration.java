package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/** Regional parameters for deterministic, multi-chunk lava fissures. */
public record MoltenFissureConfiguration(
        int regionSize,
        int minLength,
        int maxLength,
        int verticalSpacing,
        int floorSearchRange,
        float chancePerBand,
        float bendStrength,
        float branchChance,
        float bulgeChance,
        float magmaChance)
        implements FeatureConfiguration {
    public static final Codec<MoltenFissureConfiguration> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            Codec.intRange(32, 256)
                                                    .fieldOf("region_size")
                                                    .forGetter(
                                                            MoltenFissureConfiguration::regionSize),
                                            Codec.intRange(24, 256)
                                                    .fieldOf("min_length")
                                                    .forGetter(
                                                            MoltenFissureConfiguration::minLength),
                                            Codec.intRange(24, 320)
                                                    .fieldOf("max_length")
                                                    .forGetter(
                                                            MoltenFissureConfiguration::maxLength),
                                            Codec.intRange(1, 32)
                                                    .fieldOf("vertical_spacing")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::verticalSpacing),
                                            Codec.intRange(1, 16)
                                                    .fieldOf("floor_search_range")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::floorSearchRange),
                                            Codec.floatRange(0, 1)
                                                    .fieldOf("chance_per_band")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::chancePerBand),
                                            Codec.floatRange(0, 0.75F)
                                                    .fieldOf("bend_strength")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::bendStrength),
                                            Codec.floatRange(0, 1)
                                                    .fieldOf("branch_chance")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::branchChance),
                                            Codec.floatRange(0, 0.35F)
                                                    .fieldOf("bulge_chance")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::bulgeChance),
                                            Codec.floatRange(0, 1)
                                                    .fieldOf("magma_chance")
                                                    .forGetter(
                                                            MoltenFissureConfiguration
                                                                    ::magmaChance))
                                    .apply(instance, MoltenFissureConfiguration::new));

    public MoltenFissureConfiguration {
        if (maxLength < minLength) {
            throw new IllegalArgumentException("Maximum fissure length must not be below minimum");
        }
    }
}
