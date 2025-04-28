package com.freeranger.darkcaverns.registries;

import com.freeranger.darkcaverns.DarkCaverns;
import com.freeranger.darkcaverns.features.SpringLikeFeature;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FeatureRegistry {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, DarkCaverns.MODID);

    // note: unused, left as reference for future features
    DeferredHolder<Feature<?>, Feature<?>> SPRING_LIKE_FEATURE = FEATURES.register(
        "spring_like_feature", () -> new SpringLikeFeature(SpringConfiguration.CODEC)
    );

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
