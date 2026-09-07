package com.freeranger.dark_caverns.items;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public enum ToolAbility {
    NONE,
    IGNITE,
    LAUNCH;

    private static final int BASE_FIRE_SECONDS = 8;
    private static final int FIRE_ASPECT_BONUS_SECONDS = 4;
    private static final double LAUNCH_VELOCITY = 0.6;

    public void apply(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        switch (this) {
            case NONE -> {
                // No additional on-hit behavior.
            }
            case IGNITE -> ignite(stack, target, attacker);
            case LAUNCH ->
                    target.setDeltaMovement(
                            target.getDeltaMovement().add(0.0, LAUNCH_VELOCITY, 0.0));
        }
    }

    private static void ignite(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int fireAspect =
                attacker.level()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .get(Enchantments.FIRE_ASPECT)
                        .map(stack::getEnchantmentLevel)
                        .orElse(0);
        int fireSeconds = BASE_FIRE_SECONDS + fireAspect * FIRE_ASPECT_BONUS_SECONDS;
        target.setRemainingFireTicks(
                Math.max(
                        target.getRemainingFireTicks(),
                        fireSeconds * SharedConstants.TICKS_PER_SECOND));
    }
}
