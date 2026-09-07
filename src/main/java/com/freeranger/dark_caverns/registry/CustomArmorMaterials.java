package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, DarkCaverns.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LUMINITE =
            register(
                    "luminite",
                    2,
                    6,
                    5,
                    2,
                    15,
                    SoundEvents.ARMOR_EQUIP_TURTLE,
                    0.0F,
                    0.0F,
                    () -> Ingredient.of(CustomItems.LUMINITE_DUST.get()));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PLATINUM =
            register(
                    "platinum",
                    3,
                    8,
                    6,
                    3,
                    20,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    2.5F,
                    0.0F,
                    () -> Ingredient.of(CustomItems.PLATINUM_INGOT.get()));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HELLSTONE =
            register(
                    "hellstone",
                    3,
                    8,
                    6,
                    3,
                    20,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    2.5F,
                    0.0F,
                    () -> Ingredient.of(CustomItems.HELLSTONE.get()));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SHROOMSTONE =
            register(
                    "shroomstone",
                    3,
                    8,
                    6,
                    3,
                    20,
                    Holder.direct(SoundEvents.SLIME_BLOCK_PLACE),
                    2.5F,
                    0.0F,
                    () -> Ingredient.of(CustomItems.SHROOMSTONE.get()));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCORCHSTEEL =
            register(
                    "scorchsteel",
                    3,
                    8,
                    6,
                    3,
                    20,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    2.5F,
                    0.0F,
                    () -> Ingredient.of(CustomItems.SCORCHSTEEL_INGOT.get()));

    private CustomArmorMaterials() {}

    public static void register(IEventBus modBus) {
        ARMOR_MATERIALS.register(modBus);
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(
            String name,
            int helmet,
            int chestplate,
            int leggings,
            int boots,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.HELMET, helmet);
        defense.put(ArmorItem.Type.CHESTPLATE, chestplate);
        defense.put(ArmorItem.Type.LEGGINGS, leggings);
        defense.put(ArmorItem.Type.BOOTS, boots);
        defense.put(ArmorItem.Type.BODY, chestplate);

        return ARMOR_MATERIALS.register(
                name,
                () ->
                        new ArmorMaterial(
                                defense,
                                enchantmentValue,
                                equipSound,
                                repairIngredient,
                                // The original 1.16.5 assets intentionally live under
                                // assets/minecraft/textures/models/armor.
                                List.of(
                                        new ArmorMaterial.Layer(
                                                ResourceLocation.withDefaultNamespace(name))),
                                toughness,
                                knockbackResistance));
    }
}
