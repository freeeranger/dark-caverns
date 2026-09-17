package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.CavernFormationConfiguration;
import com.freeranger.dark_caverns.generation.CavernLandmarkConfiguration;
import com.freeranger.dark_caverns.generation.CavernLandmarkFeature;
import com.freeranger.dark_caverns.generation.CavernRouteFeature;
import com.freeranger.dark_caverns.generation.CrackedBedrockFeature;
import com.freeranger.dark_caverns.generation.HallowClutterFeature;
import com.freeranger.dark_caverns.generation.HallowLakeFeature;
import com.freeranger.dark_caverns.generation.HallowMudFeature;
import com.freeranger.dark_caverns.generation.SpikeFeature;
import com.freeranger.dark_caverns.generation.TwistwoodTreeFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
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
    public static final DeferredHolder<Feature<?>, Feature<CavernFormationConfiguration>> SPIKE =
            FEATURES.register(
                    "spike_feature", () -> new SpikeFeature(CavernFormationConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<CavernFormationConfiguration>>
            MOLTEN_SPIKE =
                    FEATURES.register(
                            "molten_spike_feature",
                            () -> new SpikeFeature(CavernFormationConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>>
            TWISTWOOD_TREE = FEATURES.register("twistwood_tree", TwistwoodTreeFeature::new);

    private CustomFeatures() {}

    public static final DeferredHolder<Feature<?>, Feature<CavernLandmarkConfiguration>>
            CAVERN_LANDMARK = FEATURES.register("cavern_landmark", CavernLandmarkFeature::new);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> HALLOW_LAKE =
            FEATURES.register("hallow_lake", HallowLakeFeature::new);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>>
            HALLOW_CLUTTER = FEATURES.register("hallow_clutter", HallowClutterFeature::new);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> HALLOW_MUD =
            FEATURES.register("hallow_mud", HallowMudFeature::new);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>>
            CAVERN_ROUTES = FEATURES.register("cavern_routes", CavernRouteFeature::new);

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
