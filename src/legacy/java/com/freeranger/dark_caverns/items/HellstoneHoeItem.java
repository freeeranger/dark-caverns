package com.freeranger.dark_caverns.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;

public class HellstoneHoeItem extends HoeItem {
    public HellstoneHoeItem(IItemTier tier, int damage, float attackSpeed, Properties p_i48530_4_) {
        super(tier, damage, attackSpeed, p_i48530_4_);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        int fireAspectLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);

        victim.setSecondsOnFire(8 + (fireAspectLevel * 4));

        return super.hurtEnemy(stack, victim, attacker);
    }
}
