package com.freeranger.dark_caverns.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public final class CustomItemTiers {
    public static final Tier PLATINUM = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1843,
            10.0F,
            3.5F,
            20,
            () -> Ingredient.of(CustomItems.PLATINUM_INGOT.get())
    );
    public static final Tier HELLSTONE = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1912,
            10.0F,
            3.5F,
            15,
            () -> Ingredient.of(CustomItems.HELLSTONE.get())
    );
    public static final Tier SHROOMSTONE = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1912,
            10.0F,
            3.5F,
            20,
            () -> Ingredient.of(CustomItems.SHROOMSTONE.get())
    );

    private CustomItemTiers() {
    }
}
