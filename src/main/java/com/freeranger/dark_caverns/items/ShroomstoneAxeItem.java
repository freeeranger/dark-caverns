package com.freeranger.dark_caverns.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ShroomstoneAxeItem extends AxeItem {
    public ShroomstoneAxeItem(IItemTier tier, float damage, float attackSpeed, Properties p_i48530_4_) {
        super(tier, damage, attackSpeed, p_i48530_4_);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        victim.setDeltaMovement(victim.getDeltaMovement().add(0.0D, .6F, 0.0D));

        return super.hurtEnemy(stack, victim, attacker);
    }
}
