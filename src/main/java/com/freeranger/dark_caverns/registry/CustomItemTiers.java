package com.freeranger.dark_caverns.registry;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

public enum CustomItemTiers implements IItemTier{
    PLATINUM(4, 1843, 10f, 3.5f, 20, () -> {
        return Ingredient.of(CustomItems.PLATINUM_INGOT.get());
    }),
    HELLSTONE(4, 1843, 10f, 3.5f, 15, () -> {
        return Ingredient.of(CustomItems.HELLSTONE.get());
    }),
    SHROOMSTONE(4, 1843, 10f, 3.5f, 20, () -> {
        return Ingredient.of(CustomItems.SHROOMSTONE.get());
    });

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyValue<Ingredient> repairIngredient;

    CustomItemTiers(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = new LazyValue<>(repairIngredient);
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
