package com.freeranger.dark_caverns.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public final class AbilitySwordItem extends SwordItem {
    private final ToolAbility ability;

    public AbilitySwordItem(Tier tier, ToolAbility ability, Properties properties) {
        super(tier, properties);
        this.ability = ability;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ability.apply(stack, target, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }
}
