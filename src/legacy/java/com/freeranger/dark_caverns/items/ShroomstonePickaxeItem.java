package com.freeranger.dark_caverns.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

public class ShroomstonePickaxeItem extends PickaxeItem {
    public ShroomstonePickaxeItem(IItemTier tier, int damage, float attackSpeed, Properties p_i48530_4_) {
        super(tier, damage, attackSpeed, p_i48530_4_);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        victim.setDeltaMovement(victim.getDeltaMovement().add(0.0D, .6F, 0.0D));

        return super.hurtEnemy(stack, victim, attacker);
    }
}
