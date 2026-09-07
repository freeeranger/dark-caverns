package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CrackedBedrockFeature;
import com.freeranger.dark_caverns.generation.SpikeFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomFeatures {
    private static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, DarkCaverns.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>>
            CRACKED_BEDROCK =
                    FEATURES.register(
                            "cracked_bedrock",
                            () -> new CrackedBedrockFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockStateConfiguration>> SPIKE =
            FEATURES.register(
                    "spike_feature", () -> new SpikeFeature(BlockStateConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockStateConfiguration>> MOLTEN_SPIKE =
            FEATURES.register(
                    "molten_spike_feature", () -> new SpikeFeature(BlockStateConfiguration.CODEC));

    private CustomFeatures() {}

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
