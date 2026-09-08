package com.freeranger.dark_caverns.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

public final class AbilityPickaxeItem extends PickaxeItem {
    private final ToolAbility ability;

    public AbilityPickaxeItem(Tier tier, ToolAbility ability, Properties properties) {
        super(tier, properties);
        this.ability = ability;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean hurt = super.hurtEnemy(stack, target, attacker);
        if (hurt) {
            ability.apply(stack, target, attacker);
        }
        return hurt;
    }
}
