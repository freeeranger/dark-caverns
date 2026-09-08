package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.config.ServerConfig;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ArmorEffects {
    private ArmorEffects() {}

    public static void register(IEventBus gameBus) {
        gameBus.addListener(ArmorEffects::onPlayerTick);
        gameBus.addListener(ArmorEffects::onLivingDamage);
        gameBus.addListener(ArmorEffects::onMonsterTick);
        gameBus.addListener(ArmorEffects::onAttackEntity);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        applyShroomstoneBonus(player);
        applyScorchsteelBonus(player);
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            breakScorchsteelStealth(attacker);
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        breakScorchsteelStealth(player);

        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            int pieces =
                    countArmor(
                            player,
                            CustomEquipment.SHROOMSTONE_HELMET.get(),
                            CustomEquipment.SHROOMSTONE_CHESTPLATE.get(),
                            CustomEquipment.SHROOMSTONE_LEGGINGS.get(),
                            CustomEquipment.SHROOMSTONE_BOOTS.get());
            event.setNewDamage(event.getNewDamage() * Math.max(0.0F, 1.0F - pieces * 0.25F));
        } else if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            int pieces =
                    countArmor(
                            player,
                            CustomEquipment.HELLSTONE_HELMET.get(),
                            CustomEquipment.HELLSTONE_CHESTPLATE.get(),
                            CustomEquipment.HELLSTONE_LEGGINGS.get(),
                            CustomEquipment.HELLSTONE_BOOTS.get());
            event.setNewDamage(event.getNewDamage() * Math.max(0.0F, 1.0F - pieces * 0.25F));
        }
    }

    private static void onAttackEntity(AttackEntityEvent event) {
        breakScorchsteelStealth(event.getEntity());
    }

    private static void onMonsterTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Monster monster)
                || !(monster.getTarget() instanceof Player player)
                || !player.hasEffect(MobEffects.INVISIBILITY)) {
            return;
        }
        ScorchsteelStealthState state =
                player.getExistingDataOrNull(CustomAttachments.SCORCHSTEEL_STEALTH);
        if (state != null && state.isActive()) {
            monster.setTarget(null);
        }
    }

    private static void applyShroomstoneBonus(Player player) {
        int pieces =
                countArmor(
                        player,
                        CustomEquipment.SHROOMSTONE_HELMET.get(),
                        CustomEquipment.SHROOMSTONE_CHESTPLATE.get(),
                        CustomEquipment.SHROOMSTONE_LEGGINGS.get(),
                        CustomEquipment.SHROOMSTONE_BOOTS.get());
        if (pieces == 4) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 1, false, false, false));
        }
    }

    private static void applyScorchsteelBonus(Player player) {
        int pieces =
                countArmor(
                        player,
                        CustomEquipment.SCORCHSTEEL_HELMET.get(),
                        CustomEquipment.SCORCHSTEEL_CHESTPLATE.get(),
                        CustomEquipment.SCORCHSTEEL_LEGGINGS.get(),
                        CustomEquipment.SCORCHSTEEL_BOOTS.get());
        if (pieces != 4) {
            breakScorchsteelStealth(player);
            player.removeData(CustomAttachments.SCORCHSTEEL_STEALTH);
            return;
        }

        ScorchsteelStealthState state = player.getData(CustomAttachments.SCORCHSTEEL_STEALTH);
        int stillTicks = state.update(player.position());

        if (state.isActive() && stillTicks == 0) {
            breakScorchsteelStealth(player);
            return;
        }

        if (stillTicks >= ServerConfig.scorchsteelStealthStandstillTicks()) {
            if (state.activate()) {
                player.displayClientMessage(
                        Component.translatable("message.dark_caverns.scorchsteel.stealth_active"),
                        true);
                player.level()
                        .playSound(
                                null,
                                player.blockPosition(),
                                SoundEvents.BEACON_ACTIVATE,
                                SoundSource.PLAYERS,
                                0.35F,
                                1.5F);
            }
            player.addEffect(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 10, 0, false, false, true));
        }
    }

    private static void breakScorchsteelStealth(Player player) {
        ScorchsteelStealthState state =
                player.getExistingDataOrNull(CustomAttachments.SCORCHSTEEL_STEALTH);
        if (state == null || !state.deactivate()) {
            return;
        }
        player.removeEffect(MobEffects.INVISIBILITY);
        player.displayClientMessage(
                Component.translatable("message.dark_caverns.scorchsteel.stealth_broken"), true);
        player.level()
                .playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.BEACON_DEACTIVATE,
                        SoundSource.PLAYERS,
                        0.25F,
                        1.4F);
    }

    private static int countArmor(
            Player player, Item helmet, Item chestplate, Item leggings, Item boots) {
        int count = 0;
        count += wearing(player, EquipmentSlot.HEAD, helmet) ? 1 : 0;
        count += wearing(player, EquipmentSlot.CHEST, chestplate) ? 1 : 0;
        count += wearing(player, EquipmentSlot.LEGS, leggings) ? 1 : 0;
        count += wearing(player, EquipmentSlot.FEET, boots) ? 1 : 0;
        return count;
    }

    private static boolean wearing(Player player, EquipmentSlot slot, Item item) {
        return player.getItemBySlot(slot).is(item);
    }
}
