package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/** Data-driven size distribution for recessed lava ponds in Molten Depths. */
public record MoltenPondConfiguration(
        int smallMinRadius,
        int smallMaxRadius,
        int largeMinRadius,
        int largeMaxRadius,
        float largeChance,
        float rimMagmaChance)
        implements FeatureConfiguration {
    public static final Codec<MoltenPondConfiguration> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            Codec.intRange(2, 12)
                                                    .fieldOf("small_min_radius")
                                                    .forGetter(
                                                            MoltenPondConfiguration
                                                                    ::smallMinRadius),
                                            Codec.intRange(2, 12)
                                                    .fieldOf("small_max_radius")
                                                    .forGetter(
                                                            MoltenPondConfiguration
                                                                    ::smallMaxRadius),
                                            Codec.intRange(3, 16)
                                                    .fieldOf("large_min_radius")
                                                    .forGetter(
                                                            MoltenPondConfiguration
                                                                    ::largeMinRadius),
                                            Codec.intRange(3, 16)
                                                    .fieldOf("large_max_radius")
                                                    .forGetter(
                                                            MoltenPondConfiguration
                                                                    ::largeMaxRadius),
                                            Codec.floatRange(0, 1)
                                                    .fieldOf("large_chance")
                                                    .forGetter(
                                                            MoltenPondConfiguration::largeChance),
                                            Codec.floatRange(0, 1)
                                                    .fieldOf("rim_magma_chance")
                                                    .forGetter(
                                                            MoltenPondConfiguration
                                                                    ::rimMagmaChance))
                                    .apply(instance, MoltenPondConfiguration::new));

    public MoltenPondConfiguration {
        if (smallMaxRadius < smallMinRadius || largeMaxRadius < largeMinRadius) {
            throw new IllegalArgumentException("Maximum pond radii must not be below their minima");
        }
    }
}
