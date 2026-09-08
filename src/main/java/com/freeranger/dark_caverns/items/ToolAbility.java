package com.freeranger.dark_caverns.items;

import net.minecraft.SharedConstants;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public enum ToolAbility {
    NONE,
    IGNITE,
    LAUNCH;

    private static final int BASE_FIRE_SECONDS = 8;
    private static final int FIRE_ASPECT_BONUS_SECONDS = 4;
    private static final double LAUNCH_VELOCITY = 0.6;
    private static final float MINIMUM_PLAYER_ATTACK_STRENGTH = 0.9F;

    public void apply(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this == NONE
                || attacker.level().isClientSide()
                || attacker instanceof Player player
                        && player.getAttackStrengthScale(0.5F) < MINIMUM_PLAYER_ATTACK_STRENGTH) {
            return;
        }
        switch (this) {
            case NONE -> {}
            case IGNITE -> ignite(stack, target, attacker);
            case LAUNCH -> launch(target);
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
        feedback(target, ParticleTypes.FLAME, SoundEvents.FIRECHARGE_USE, 1.0F);
    }

    private static void launch(LivingEntity target) {
        target.setDeltaMovement(target.getDeltaMovement().add(0.0, LAUNCH_VELOCITY, 0.0));
        target.hurtMarked = true;
        feedback(target, ParticleTypes.POOF, SoundEvents.SLIME_BLOCK_FALL, 0.8F);
    }

    private static void feedback(
            LivingEntity target, SimpleParticleType particle, SoundEvent sound, float pitch) {
        if (target.level() instanceof ServerLevel level) {
            level.sendParticles(
                    particle,
                    target.getX(),
                    target.getY() + target.getBbHeight() * 0.5,
                    target.getZ(),
                    8,
                    target.getBbWidth() * 0.25,
                    target.getBbHeight() * 0.2,
                    target.getBbWidth() * 0.25,
                    0.03);
            level.playSound(null, target.blockPosition(), sound, SoundSource.PLAYERS, 0.45F, pitch);
        }
    }
}
