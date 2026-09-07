package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.generation.CrackedBedrockFeature;
import com.freeranger.dark_caverns.generation.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class CustomFeatures {
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>>
            CRACKED_BEDROCK =
                    ModRegistries.FEATURES.register(
                            "cracked_bedrock",
                            () -> new CrackedBedrockFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SPIKE =
            ModRegistries.FEATURES.register(
                    "spike_feature",
                    () -> new SpikeFeature(NoneFeatureConfiguration.CODEC, CustomBlocks.CARFSTONE));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MOLTEN_SPIKE =
            ModRegistries.FEATURES.register(
                    "molten_spike_feature",
                    () ->
                            new SpikeFeature(
                                    NoneFeatureConfiguration.CODEC, CustomBlocks.MOLTEN_CARFSTONE));

    private CustomFeatures() {}

    public static void bootstrap() {
        // Forces class initialization before the deferred register attaches to the mod event bus.
    }
}
