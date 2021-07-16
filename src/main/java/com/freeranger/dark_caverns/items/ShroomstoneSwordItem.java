package com.freeranger.dark_caverns.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class ShroomstoneSwordItem extends SwordItem {
    public ShroomstoneSwordItem(IItemTier tier, int damage, float attackSpeed, Properties properties) {
        super(tier, damage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        victim.setDeltaMovement(victim.getDeltaMovement().add(0.0D, .6F, 0.0D));

        return super.hurtEnemy(stack, victim, attacker);
    }
}
