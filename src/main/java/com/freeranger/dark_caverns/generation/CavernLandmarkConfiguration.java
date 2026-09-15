package com.freeranger.dark_caverns.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record CavernLandmarkConfiguration(
        ResourceLocation template, float chance, boolean groups, int verticalSpacing)
        implements FeatureConfiguration {
    public static final Codec<CavernLandmarkConfiguration> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            ResourceLocation.CODEC
                                                    .fieldOf("template")
                                                    .forGetter(
                                                            CavernLandmarkConfiguration::template),
                                            Codec.floatRange(0, 1)
                                                    .fieldOf("chance")
                                                    .forGetter(CavernLandmarkConfiguration::chance),
                                            Codec.BOOL
                                                    .optionalFieldOf("groups", false)
                                                    .forGetter(CavernLandmarkConfiguration::groups),
                                            Codec.intRange(8, 64)
                                                    .optionalFieldOf("vertical_spacing", 12)
                                                    .forGetter(
                                                            CavernLandmarkConfiguration
                                                                    ::verticalSpacing))
                                    .apply(instance, CavernLandmarkConfiguration::new));
}
