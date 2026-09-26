package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ArmorEffects {
    private static final float FORTY_PERCENT_REDUCTION_MULTIPLIER = 0.6F;
    private static final AttributeModifier SHROOMSTONE_SPRINT_SPEED =
            new AttributeModifier(
                    DarkCaverns.id("shroomstone_sprint_speed"),
                    0.1,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    private static final AttributeModifier SHROOMSTONE_JUMP_HEIGHT =
            new AttributeModifier(
                    DarkCaverns.id("shroomstone_jump_height"),
                    0.15,
                    AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier HELLSTONE_LAVA_SWIM_SPEED =
            new AttributeModifier(
                    DarkCaverns.id("hellstone_lava_swim_speed"),
                    0.5,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    private ArmorEffects() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ArmorEffects::onPlayerTick);
        gameBus.addListener(ArmorEffects::onLivingIncomingDamage);
        gameBus.addListener(ArmorEffects::onLivingDamage);
        gameBus.addListener(ArmorEffects::onLivingKnockBack);
        gameBus.addListener(ArmorEffects::onExplosionKnockback);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        applyShroomstoneMovement(player);
        applyHellstoneLavaMovement(player);

        if (player.level().isClientSide()) {
            return;
        }

        shortenHellstoneBurnTime(player);
    }

    private static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || !wearing(player, EquipmentSlot.FEET, CustomEquipment.HELLSTONE_BOOTS.get())) {
            return;
        }

        if (event.getSource().is(DamageTypes.IN_FIRE)
                || event.getSource().is(DamageTypes.CAMPFIRE)
                || event.getSource().is(DamageTypes.HOT_FLOOR)) {
            event.setCanceled(true);
        }
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getSource().is(DamageTypeTags.IS_FALL)
                && wearing(player, EquipmentSlot.FEET, CustomEquipment.SHROOMSTONE_BOOTS.get())) {
            event.setNewDamage(0.0F);
        } else if (event.getSource().is(DamageTypeTags.IS_FIRE)
                && wearing(
                        player, EquipmentSlot.CHEST, CustomEquipment.HELLSTONE_CHESTPLATE.get())) {
            event.setNewDamage(event.getNewDamage() * FORTY_PERCENT_REDUCTION_MULTIPLIER);
        }
    }

    private static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof Player player
                && wearing(
                        player,
                        EquipmentSlot.CHEST,
                        CustomEquipment.SHROOMSTONE_CHESTPLATE.get())) {
            event.setStrength(event.getStrength() * FORTY_PERCENT_REDUCTION_MULTIPLIER);
        }
    }

    private static void onExplosionKnockback(ExplosionKnockbackEvent event) {
        if (event.getAffectedEntity() instanceof Player player
                && wearing(
                        player,
                        EquipmentSlot.CHEST,
                        CustomEquipment.SHROOMSTONE_CHESTPLATE.get())) {
            event.setKnockbackVelocity(
                    event.getKnockbackVelocity().scale(FORTY_PERCENT_REDUCTION_MULTIPLIER));
        }
    }

    private static void applyShroomstoneMovement(Player player) {
        setTransientModifier(
                player,
                Attributes.MOVEMENT_SPEED,
                SHROOMSTONE_SPRINT_SPEED,
                player.isSprinting()
                        && wearing(
                                player,
                                EquipmentSlot.HEAD,
                                CustomEquipment.SHROOMSTONE_HELMET.get()));
        setTransientModifier(
                player,
                Attributes.JUMP_STRENGTH,
                SHROOMSTONE_JUMP_HEIGHT,
                wearing(player, EquipmentSlot.LEGS, CustomEquipment.SHROOMSTONE_LEGGINGS.get()));
    }

    private static void applyHellstoneLavaMovement(Player player) {
        boolean active =
                !player.isSpectator()
                        && !player.isPassenger()
                        && isPlayerInLava(player)
                        && wearing(
                                player,
                                EquipmentSlot.LEGS,
                                CustomEquipment.HELLSTONE_LEGGINGS.get());
        setTransientModifier(player, NeoForgeMod.SWIM_SPEED, HELLSTONE_LAVA_SWIM_SPEED, active);
        if (!active) {
            return;
        }

        Vec3 delta = player.getDeltaMovement();
        double hSpeed = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        boolean hasInput = player.xxa != 0.0F || player.zza != 0.0F;
        boolean shouldBoostHorizontal =
                hasInput || (!player.level().isClientSide() && hSpeed > 0.005);

        double newX = delta.x;
        double newZ = delta.z;
        if (shouldBoostHorizontal && hSpeed > 0.001) {
            double maxSpeed = player.isSprinting() ? 0.28 : 0.20;
            double targetSpeed = Math.min(maxSpeed, Math.max(hSpeed * 2.5, 0.12));
            double scale = targetSpeed / hSpeed;
            newX = delta.x * scale;
            newZ = delta.z * scale;
        }

        double newY = delta.y;
        if (delta.y > 0.001) {
            newY = Math.min(0.18, Math.max(delta.y * 3.0, 0.15));
        } else if (player.isShiftKeyDown()) {
            newY = -0.14;
        }

        player.setDeltaMovement(newX, newY, newZ);
    }

    private static void shortenHellstoneBurnTime(Player player) {
        if (player.getRemainingFireTicks() <= 0
                || isPlayerInLava(player)
                || isTouchingFire(player)
                || !wearing(player, EquipmentSlot.HEAD, CustomEquipment.HELLSTONE_HELMET.get())) {
            return;
        }
        player.setRemainingFireTicks(Math.max(0, player.getRemainingFireTicks() - 1));
    }

    private static boolean isPlayerInLava(Player player) {
        return player.isInLava()
                || player.isEyeInFluidType(NeoForgeMod.LAVA_TYPE.value())
                || player.level().getFluidState(player.blockPosition()).is(FluidTags.LAVA)
                || BlockPos.betweenClosedStream(player.getBoundingBox().deflate(1.0E-6))
                        .anyMatch(pos -> player.level().getFluidState(pos).is(FluidTags.LAVA));
    }

    private static void setTransientModifier(
            Player player,
            Holder<Attribute> attribute,
            AttributeModifier modifier,
            boolean active) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (active) {
            instance.addOrUpdateTransientModifier(modifier);
        } else {
            instance.removeModifier(modifier.id());
        }
    }

    private static boolean isTouchingFire(Player player) {
        return BlockPos.betweenClosedStream(player.getBoundingBox().deflate(1.0E-6))
                .anyMatch(pos -> player.level().getBlockState(pos).is(BlockTags.FIRE));
    }

    private static boolean wearing(Player player, EquipmentSlot slot, Item item) {
        return player.getItemBySlot(slot).is(item);
    }
}
