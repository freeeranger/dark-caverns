package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.blocks.ScorchedBerryBushBlock;
import com.freeranger.dark_caverns.generation.CustomFlatBigMushroomFeature;
import com.freeranger.dark_caverns.generation.CustomHighBigMushroomFeature;
import com.freeranger.dark_caverns.generation.SpikeFeature;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
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
            "spike_feature", () -> new SpikeFeature(NoFeatureConfig.CODEC, CustomBlocks.CARFSTONE.get()));

    public static final RegistryObject<Feature<NoFeatureConfig>> MOLTEN_SPIKE_FEATURE = FEATURES.register(
            "molten_spike_feature", () -> new SpikeFeature(NoFeatureConfig.CODEC, CustomBlocks.MOLTEN_CARFSTONE.get()));

    public static final RegistryObject<Feature<BigMushroomFeatureConfig>> HUGE_MUSHROOM_FEATURE = FEATURES.register(
            "huge_mushroom_feature", () -> new CustomFlatBigMushroomFeature(BigMushroomFeatureConfig.CODEC));

    public static final RegistryObject<Feature<BigMushroomFeatureConfig>> HUGE_HIGH_MUSHROOM_FEATURE = FEATURES.register(
            "huge_high_mushroom_feature", () -> new CustomHighBigMushroomFeature(BigMushroomFeatureConfig.CODEC));


    public static final class ConfiguredFeatures {
        public static final ConfiguredFeature<?, ?> SPIKE_FEATURE = CustomFeatures.SPIKE_FEATURE.get().configured(
                IFeatureConfig.NONE
        );

        public static final ConfiguredFeature<?, ?> MOLTEN_SPIKE_FEATURE = CustomFeatures.MOLTEN_SPIKE_FEATURE.get().configured(
                IFeatureConfig.NONE
        );

        public static final ConfiguredFeature<?, ?> FIRE_PATCH = Feature.RANDOM_PATCH.configured(
                (new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(Blocks.FIRE.defaultBlockState()), new SimpleBlockPlacer()))
                        .tries(64).whitelist(ImmutableSet.of(CustomBlocks.MOLTEN_CARFSTONE.get())).noProjection().build()
        );

        public static final ConfiguredFeature<?, ?> SCORCHED_BERRY_BUSH_PATCH = Feature.RANDOM_PATCH.configured(
                (new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(CustomBlocks.SCORCHED_BERRY_BUSH.get().defaultBlockState()
                        .setValue(ScorchedBerryBushBlock.AGE, 3)), new SimpleBlockPlacer()))
                        .tries(32).whitelist(ImmutableSet.of(CustomBlocks.MOLTEN_CARFSTONE.get())).noProjection().build()
        );

        public static final ConfiguredFeature<?, ?> GLIMMERGRASS_PATCH = Feature.RANDOM_PATCH.configured(
                (new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(CustomBlocks.GLIMMERGRASS.get().defaultBlockState()), new SimpleBlockPlacer()))
                        .tries(32).whitelist(ImmutableSet.of(CustomBlocks.GLIMMERGRASS_BLOCK.get())).noProjection().build()
        );

        public static final ConfiguredFeature<?, ?> CHARRED_GRASS_PATCH = Feature.RANDOM_PATCH.configured(
                (new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(CustomBlocks.CHARRED_GRASS.get().defaultBlockState()), new SimpleBlockPlacer()))
                        .tries(32).whitelist(ImmutableSet.of(CustomBlocks.MOLTEN_CARFSTONE.get())).noProjection().build()
        );

        public static final ConfiguredFeature<?, ?> GLIMMERSHROOM_PATCH = Feature.RANDOM_PATCH.configured(
                (new BlockClusterFeatureConfig.Builder(
                        new SimpleBlockStateProvider(CustomBlocks.GLIMMERSHROOM.get().defaultBlockState()), new SimpleBlockPlacer()))
                        .tries(32).whitelist(ImmutableSet.of(CustomBlocks.GLIMMERGRASS_BLOCK.get())).noProjection().build()
        );

        static final RuleTest BASE_BLOCKS_TAG = new TagMatchRuleTest(CustomTags.Blocks.BASE_STONE);

        public static final ConfiguredFeature<?, ?> LUMINITE_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.LUMINITE_ORE.get().defaultBlockState(), 12)
        );

        public static final ConfiguredFeature<?, ?> MAGMA_PATCH = Feature.ORE.configured(
                new OreFeatureConfig(new BlockMatchRuleTest(CustomBlocks.MOLTEN_CARFSTONE.get()),
                        Blocks.MAGMA_BLOCK.defaultBlockState(), 24)
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
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_DIAMOND_ORE.get().defaultBlockState(), 11)
        );

        public static final ConfiguredFeature<?, ?> REDSTONE_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_REDSTONE_ORE.get().defaultBlockState(), 12)
        );

        public static final ConfiguredFeature<?, ?> PLATINUM_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.PLATINUM_ORE.get().defaultBlockState(), 5)
        );

        public static final ConfiguredFeature<?, ?> LAPIS_ORE_FEATURE = Feature.ORE.configured(
                new OreFeatureConfig(BASE_BLOCKS_TAG, CustomBlocks.CARFSTONE_LAPIS_ORE.get().defaultBlockState(), 12)
        );

        public static final ConfiguredFeature<?, ?> HUGE_MUSHROOM = CustomFeatures.HUGE_MUSHROOM_FEATURE.get().configured(
                        new BigMushroomFeatureConfig(
                                new SimpleBlockStateProvider(
                                        CustomBlocks.GLIMMERSHROOM_BLOCK.get().defaultBlockState()
                                                .setValue(HugeMushroomBlock.UP, Boolean.TRUE)
                                                .setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)
                                ),
                                new SimpleBlockStateProvider(
                                        Blocks.MUSHROOM_STEM.defaultBlockState()
                                                .setValue(HugeMushroomBlock.UP, Boolean.FALSE)
                                                .setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)
                                ),
                                3
                        )
        );

        public static final ConfiguredFeature<?, ?> HUGE_HIGH_MUSHROOM = CustomFeatures.HUGE_HIGH_MUSHROOM_FEATURE.get().configured(
                new BigMushroomFeatureConfig(
                        new SimpleBlockStateProvider(
                                CustomBlocks.GLIMMERSHROOM_BLOCK.get().defaultBlockState()
                                        .setValue(HugeMushroomBlock.UP, Boolean.TRUE)
                                        .setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)
                        ),
                        new SimpleBlockStateProvider(
                                Blocks.MUSHROOM_STEM.defaultBlockState()
                                        .setValue(HugeMushroomBlock.UP, Boolean.FALSE)
                                        .setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)
                        ),
                        3
                )
        );
    }

    public static void register(){
        registerConfiguredFeature("spike_feature", ConfiguredFeatures.SPIKE_FEATURE.decorated(
                Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2))
        ));

        registerConfiguredFeature("molten_spike_feature", ConfiguredFeatures.MOLTEN_SPIKE_FEATURE.decorated(
                Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(1))
        ));

        registerConfiguredFeature("huge_mushroom_feature", ConfiguredFeatures.HUGE_MUSHROOM.decorated(
                Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2))
        ));

        registerConfiguredFeature("huge_high_mushroom_feature", ConfiguredFeatures.HUGE_HIGH_MUSHROOM.decorated(
                Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2))
        ));

        registerConfiguredFeature("fire_patch", ConfiguredFeatures.FIRE_PATCH.range(
                256).squared().count(16)
        );

        registerConfiguredFeature("scorched_berry_bush_patch", ConfiguredFeatures.SCORCHED_BERRY_BUSH_PATCH.range(
                256).squared().count(36)
        );

        registerConfiguredFeature("glimmergrass_patch", ConfiguredFeatures.GLIMMERGRASS_PATCH.range(
                256).squared().count(128)
        );

        registerConfiguredFeature("charred_grass_patch", ConfiguredFeatures.CHARRED_GRASS_PATCH.range(
                256).squared().count(128)
        );

        registerConfiguredFeature("shroom_patch", ConfiguredFeatures.GLIMMERSHROOM_PATCH.range(
                256).squared().count(48)
        );

        registerConfiguredFeature("luminite_ore_feature", ConfiguredFeatures.LUMINITE_ORE_FEATURE.range(
                256).squared().count(44)
        );

        registerConfiguredFeature("magma_patch", ConfiguredFeatures.MAGMA_PATCH.range(
                256).squared().count(32)
        );

        registerConfiguredFeature("coal_ore_feature", ConfiguredFeatures.COAL_ORE_FEATURE.range(
                256).squared().count(23)
        );

        registerConfiguredFeature("iron_ore_feature", ConfiguredFeatures.IRON_ORE_FEATURE.range(
                256).squared().count(21)
        );

        registerConfiguredFeature("gold_ore_feature", ConfiguredFeatures.GOLD_ORE_FEATURE.range(
                256).squared().count(16)
        );

        registerConfiguredFeature("diamond_ore_feature", ConfiguredFeatures.DIAMOND_ORE_FEATURE.range(
                256).squared().count(7)
        );

        registerConfiguredFeature("platinum_ore_feature", ConfiguredFeatures.PLATINUM_ORE_FEATURE.range(
                256).squared().count(4)
        );

        registerConfiguredFeature("redstone_ore_feature", ConfiguredFeatures.REDSTONE_ORE_FEATURE.range(
                256).squared().count(16)
        );

        registerConfiguredFeature("lapis_ore_feature", ConfiguredFeatures.LAPIS_ORE_FEATURE.range(
                256).squared().count(16)
        );
    }

    private static <FC extends IFeatureConfig> void registerConfiguredFeature(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(DarkCaverns.MOD_ID, name), feature);
    }
}
