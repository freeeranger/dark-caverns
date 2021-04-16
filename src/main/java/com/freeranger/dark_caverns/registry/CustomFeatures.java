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

        public static final ConfiguredFeature<?, ?> COAL_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_COAL_ORE.get().defaultBlockState(), 20)
        );

        public static final ConfiguredFeature<?, ?> IRON_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_IRON_ORE.get().defaultBlockState(), 14)
        );

        public static final ConfiguredFeature<?, ?> GOLD_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_GOLD_ORE.get().defaultBlockState(), 12)
        );

        public static final ConfiguredFeature<?, ?> DIAMOND_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_DIAMOND_ORE.get().defaultBlockState(), 12)
        );

        public static final ConfiguredFeature<?, ?> REDSTONE_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_REDSTONE_ORE.get().defaultBlockState(), 8)
        );

        public static final ConfiguredFeature<?, ?> LAPIS_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_LAPIS_ORE.get().defaultBlockState(), 8)
        );
    }

    public static void register(){
        registerConfiguredFeature("spike_feature", ConfiguredFeatures.SPIKE_FEATURE.decorated(
                Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2))
        ));

        registerConfiguredFeature("luminite_ore_feature", ConfiguredFeatures.LUMINITE_ORE_FEATURE.range(
                256).squared().count(42)
        );

        registerConfiguredFeature("coal_ore_feature", ConfiguredFeatures.COAL_ORE_FEATURE.range(
                256).squared().count(20)
        );

        registerConfiguredFeature("iron_ore_feature", ConfiguredFeatures.IRON_ORE_FEATURE.range(
                256).squared().count(20)
        );

        registerConfiguredFeature("gold_ore_feature", ConfiguredFeatures.GOLD_ORE_FEATURE.range(
                256).squared().count(15)
        );

        registerConfiguredFeature("diamond_ore_feature", ConfiguredFeatures.DIAMOND_ORE_FEATURE.range(
                256).squared().count(5)
        );

        registerConfiguredFeature("redstone_ore_feature", ConfiguredFeatures.REDSTONE_ORE_FEATURE.range(
                256).squared().count(10)
        );

        registerConfiguredFeature("lapis_ore_feature", ConfiguredFeatures.LAPIS_ORE_FEATURE.range(
                256).squared().count(10)
        );
    }

    private static <FC extends IFeatureConfig> void registerConfiguredFeature(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(DarkCaverns.MOD_ID, name), feature);
    }
}
