package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.generation.SpikeFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(
            ForgeRegistries.FEATURES, DarkCaverns.MOD_ID
    );

    public static final RegistryObject<Feature<NoFeatureConfig>> SPIKE_FEATURE = FEATURES.register(
            "spike_feature", () -> new SpikeFeature(NoFeatureConfig.CODEC));

    public static final class ConfiguredFeatures {
        public static final ConfiguredFeature<?, ?> SPIKE_FEATURE = CustomFeatures.SPIKE_FEATURE.get().configured(
                IFeatureConfig.NONE
        );

        static final RuleTest BASE_BLOCKS_TAG = new TagMatchRuleTest(CustomTags.Blocks.BASE_STONE);

        public static final ConfiguredFeature<?, ?> LUMINITE_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.LUMINITE_ORE.get().defaultBlockState(), 12)
        );
    }

    public static void register(){
        registerConfiguredFeature("spike_feature", ConfiguredFeatures.SPIKE_FEATURE.decorated(
                Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2))
        ));

        registerConfiguredFeature("luminite_ore_feature", ConfiguredFeatures.LUMINITE_ORE_FEATURE.range(
                256).squared().count(60)
        );
    }

    private static <FC extends IFeatureConfig> void registerConfiguredFeature(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(DarkCaverns.MOD_ID, name), feature);
    }
}
