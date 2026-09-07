package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = DarkCaverns.MOD_ID)
public final class ArmorEffects {
    private static final String LAST_X = "dark_caverns_scorch_x";
    private static final String LAST_Y = "dark_caverns_scorch_y";
    private static final String LAST_Z = "dark_caverns_scorch_z";
    private static final String STILL_TICKS = "dark_caverns_scorch_timer";

    private ArmorEffects() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        applyHellstoneSetBonus(player);
        applyShroomstoneBonus(player);
        applyScorchsteelBonus(player);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            int pieces =
                    countArmor(
                            player,
                            CustomItems.SHROOMSTONE_HELMET.get(),
                            CustomItems.SHROOMSTONE_CHESTPLATE.get(),
                            CustomItems.SHROOMSTONE_LEGGINGS.get(),
                            CustomItems.SHROOMSTONE_BOOTS.get());
            event.setNewDamage(event.getNewDamage() * Math.max(0.0F, 1.0F - pieces * 0.25F));
        } else if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            int pieces =
                    countArmor(
                            player,
                            CustomItems.HELLSTONE_HELMET.get(),
                            CustomItems.HELLSTONE_CHESTPLATE.get(),
                            CustomItems.HELLSTONE_LEGGINGS.get(),
                            CustomItems.HELLSTONE_BOOTS.get());
            event.setNewDamage(event.getNewDamage() * Math.max(0.0F, 1.0F - pieces * 0.25F));
        }
    }

    @SubscribeEvent
    public static void onMonsterTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Monster monster)
                || !(monster.getTarget() instanceof Player player)
                || !player.hasEffect(MobEffects.INVISIBILITY)) {
            return;
        }
        if (countArmor(
                        player,
                        CustomItems.SCORCHSTEEL_HELMET.get(),
                        CustomItems.SCORCHSTEEL_CHESTPLATE.get(),
                        CustomItems.SCORCHSTEEL_LEGGINGS.get(),
                        CustomItems.SCORCHSTEEL_BOOTS.get())
                > 0) {
            monster.setTarget(null);
        }
    }

    private static void applyHellstoneSetBonus(Player player) {
        if (wearing(player, EquipmentSlot.HEAD, CustomItems.HELLSTONE_HELMET.get())
                && wearing(player, EquipmentSlot.CHEST, CustomItems.HELLSTONE_CHESTPLATE.get())
                && wearing(player, EquipmentSlot.LEGS, CustomItems.HELLSTONE_LEGGINGS.get())
                && wearing(player, EquipmentSlot.FEET, CustomItems.HELLSTONE_BOOTS.get())) {
            player.addEffect(
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 0, false, false, false));
        }
    }

    private static void applyShroomstoneBonus(Player player) {
        int pieces =
                countArmor(
                        player,
                        CustomItems.SHROOMSTONE_HELMET.get(),
                        CustomItems.SHROOMSTONE_CHESTPLATE.get(),
                        CustomItems.SHROOMSTONE_LEGGINGS.get(),
                        CustomItems.SHROOMSTONE_BOOTS.get());
        if (pieces > 0) {
            player.addEffect(
                    new MobEffectInstance(MobEffects.JUMP, 20, pieces - 1, false, false, false));
        }
    }

    private static void applyScorchsteelBonus(Player player) {
        int pieces =
                countArmor(
                        player,
                        CustomItems.SCORCHSTEEL_HELMET.get(),
                        CustomItems.SCORCHSTEEL_CHESTPLATE.get(),
                        CustomItems.SCORCHSTEEL_LEGGINGS.get(),
                        CustomItems.SCORCHSTEEL_BOOTS.get());
        CompoundTag data = player.getPersistentData();
        if (pieces == 0) {
            data.putInt(STILL_TICKS, 0);
            return;
        }

        boolean stationary =
                data.contains(LAST_X)
                        && data.getDouble(LAST_X) == player.getX()
                        && data.getDouble(LAST_Y) == player.getY()
                        && data.getDouble(LAST_Z) == player.getZ();
        int stillTicks = stationary ? data.getInt(STILL_TICKS) + 1 : 0;

        data.putDouble(LAST_X, player.getX());
        data.putDouble(LAST_Y, player.getY());
        data.putDouble(LAST_Z, player.getZ());
        data.putInt(STILL_TICKS, stillTicks);

        if (stillTicks >= DarkCavernsConfig.COMMON.scorchsteelStealthStandstillTicks.get()) {
            int effectDuration =
                    switch (pieces) {
                        case 2 -> 40;
                        case 3 -> 60;
                        case 4 -> 120;
                        default -> 20;
                    };
            player.addEffect(
                    new MobEffectInstance(
                            MobEffects.INVISIBILITY, effectDuration, 0, false, false, true));
        }
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
