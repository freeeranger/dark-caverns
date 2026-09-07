package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.items.AbilityAxeItem;
import com.freeranger.dark_caverns.items.AbilityHoeItem;
import com.freeranger.dark_caverns.items.AbilityPickaxeItem;
import com.freeranger.dark_caverns.items.AbilityShovelItem;
import com.freeranger.dark_caverns.items.AbilitySwordItem;
import com.freeranger.dark_caverns.items.ToolAbility;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomEquipment {
    private static final int LUMINITE_DURABILITY_MULTIPLIER = 15;
    private static final int UPGRADED_DURABILITY_MULTIPLIER = 36;
    private static final float SWORD_ATTACK_DAMAGE = 3.0F;
    private static final float SWORD_ATTACK_SPEED = -2.4F;
    private static final float AXE_ATTACK_DAMAGE = 5.0F;
    private static final float AXE_ATTACK_SPEED = -3.0F;
    private static final float PICKAXE_ATTACK_DAMAGE = 1.0F;
    private static final float PICKAXE_ATTACK_SPEED = -2.8F;
    private static final float SHOVEL_ATTACK_DAMAGE = 1.5F;
    private static final float SHOVEL_ATTACK_SPEED = -3.0F;
    private static final float HOE_ATTACK_DAMAGE = -4.0F;
    private static final float HOE_ATTACK_SPEED = 0.0F;

    private static final DeferredRegister.Items EQUIPMENT =
            DeferredRegister.createItems(DarkCaverns.MOD_ID);

    private static final ToolSet PLATINUM_TOOLS =
            new ToolSet(CustomItemTiers.PLATINUM, ToolAbility.NONE, false);
    private static final ToolSet HELLSTONE_TOOLS =
            new ToolSet(CustomItemTiers.HELLSTONE, ToolAbility.IGNITE, true);
    private static final ToolSet SHROOMSTONE_TOOLS =
            new ToolSet(CustomItemTiers.SHROOMSTONE, ToolAbility.LAUNCH, false);

    private static final ArmorSet LUMINITE_ARMOR =
            new ArmorSet(CustomArmorMaterials.LUMINITE, LUMINITE_DURABILITY_MULTIPLIER, false);
    private static final ArmorSet PLATINUM_ARMOR =
            new ArmorSet(CustomArmorMaterials.PLATINUM, UPGRADED_DURABILITY_MULTIPLIER, false);
    private static final ArmorSet HELLSTONE_ARMOR =
            new ArmorSet(CustomArmorMaterials.HELLSTONE, UPGRADED_DURABILITY_MULTIPLIER, true);
    private static final ArmorSet SHROOMSTONE_ARMOR =
            new ArmorSet(CustomArmorMaterials.SHROOMSTONE, UPGRADED_DURABILITY_MULTIPLIER, false);
    private static final ArmorSet SCORCHSTEEL_ARMOR =
            new ArmorSet(CustomArmorMaterials.SCORCHSTEEL, UPGRADED_DURABILITY_MULTIPLIER, true);

    public static final DeferredItem<AbilitySwordItem> PLATINUM_SWORD =
            sword("platinum_sword", PLATINUM_TOOLS);
    public static final DeferredItem<AbilityAxeItem> PLATINUM_AXE =
            axe("platinum_axe", PLATINUM_TOOLS);
    public static final DeferredItem<AbilityPickaxeItem> PLATINUM_PICKAXE =
            pickaxe("platinum_pickaxe", PLATINUM_TOOLS);
    public static final DeferredItem<AbilityShovelItem> PLATINUM_SHOVEL =
            shovel("platinum_shovel", PLATINUM_TOOLS);
    public static final DeferredItem<AbilityHoeItem> PLATINUM_HOE =
            hoe("platinum_hoe", PLATINUM_TOOLS);

    public static final DeferredItem<AbilitySwordItem> HELLSTONE_SWORD =
            sword("hellstone_sword", HELLSTONE_TOOLS);
    public static final DeferredItem<AbilityAxeItem> HELLSTONE_AXE =
            axe("hellstone_axe", HELLSTONE_TOOLS);
    public static final DeferredItem<AbilityPickaxeItem> HELLSTONE_PICKAXE =
            pickaxe("hellstone_pickaxe", HELLSTONE_TOOLS);
    public static final DeferredItem<AbilityShovelItem> HELLSTONE_SHOVEL =
            shovel("hellstone_shovel", HELLSTONE_TOOLS);
    public static final DeferredItem<AbilityHoeItem> HELLSTONE_HOE =
            hoe("hellstone_hoe", HELLSTONE_TOOLS);

    public static final DeferredItem<AbilitySwordItem> SHROOMSTONE_SWORD =
            sword("shroomstone_sword", SHROOMSTONE_TOOLS);
    public static final DeferredItem<AbilityAxeItem> SHROOMSTONE_AXE =
            axe("shroomstone_axe", SHROOMSTONE_TOOLS);
    public static final DeferredItem<AbilityPickaxeItem> SHROOMSTONE_PICKAXE =
            pickaxe("shroomstone_pickaxe", SHROOMSTONE_TOOLS);
    public static final DeferredItem<AbilityShovelItem> SHROOMSTONE_SHOVEL =
            shovel("shroomstone_shovel", SHROOMSTONE_TOOLS);
    public static final DeferredItem<AbilityHoeItem> SHROOMSTONE_HOE =
            hoe("shroomstone_hoe", SHROOMSTONE_TOOLS);

    public static final DeferredItem<ArmorItem> LUMINITE_HELMET =
            armor("luminite_helmet", LUMINITE_ARMOR, ArmorItem.Type.HELMET);

    public static final DeferredItem<ArmorItem> PLATINUM_HELMET =
            armor("platinum_helmet", PLATINUM_ARMOR, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> PLATINUM_CHESTPLATE =
            armor("platinum_chestplate", PLATINUM_ARMOR, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> PLATINUM_LEGGINGS =
            armor("platinum_leggings", PLATINUM_ARMOR, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> PLATINUM_BOOTS =
            armor("platinum_boots", PLATINUM_ARMOR, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> HELLSTONE_HELMET =
            armor("hellstone_helmet", HELLSTONE_ARMOR, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> HELLSTONE_CHESTPLATE =
            armor("hellstone_chestplate", HELLSTONE_ARMOR, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> HELLSTONE_LEGGINGS =
            armor("hellstone_leggings", HELLSTONE_ARMOR, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> HELLSTONE_BOOTS =
            armor("hellstone_boots", HELLSTONE_ARMOR, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> SHROOMSTONE_HELMET =
            armor("shroomstone_helmet", SHROOMSTONE_ARMOR, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SHROOMSTONE_CHESTPLATE =
            armor("shroomstone_chestplate", SHROOMSTONE_ARMOR, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> SHROOMSTONE_LEGGINGS =
            armor("shroomstone_leggings", SHROOMSTONE_ARMOR, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> SHROOMSTONE_BOOTS =
            armor("shroomstone_boots", SHROOMSTONE_ARMOR, ArmorItem.Type.BOOTS);

    public static final DeferredItem<ArmorItem> SCORCHSTEEL_HELMET =
            armor("scorchsteel_helmet", SCORCHSTEEL_ARMOR, ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> SCORCHSTEEL_CHESTPLATE =
            armor("scorchsteel_chestplate", SCORCHSTEEL_ARMOR, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> SCORCHSTEEL_LEGGINGS =
            armor("scorchsteel_leggings", SCORCHSTEEL_ARMOR, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> SCORCHSTEEL_BOOTS =
            armor("scorchsteel_boots", SCORCHSTEEL_ARMOR, ArmorItem.Type.BOOTS);

    private CustomEquipment() {}

    public static void register(IEventBus modBus) {
        EQUIPMENT.register(modBus);
    }

    private static DeferredItem<AbilitySwordItem> sword(String name, ToolSet tools) {
        return EQUIPMENT.register(
                name,
                () ->
                        new AbilitySwordItem(
                                tools.tier(),
                                tools.ability(),
                                toolProperties(tools)
                                        .attributes(
                                                SwordItem.createAttributes(
                                                        tools.tier(),
                                                        SWORD_ATTACK_DAMAGE,
                                                        SWORD_ATTACK_SPEED))));
    }

    private static DeferredItem<AbilityAxeItem> axe(String name, ToolSet tools) {
        return EQUIPMENT.register(
                name,
                () ->
                        new AbilityAxeItem(
                                tools.tier(),
                                tools.ability(),
                                toolProperties(tools)
                                        .attributes(
                                                AxeItem.createAttributes(
                                                        tools.tier(),
                                                        AXE_ATTACK_DAMAGE,
                                                        AXE_ATTACK_SPEED))));
    }

    private static DeferredItem<AbilityPickaxeItem> pickaxe(String name, ToolSet tools) {
        return EQUIPMENT.register(
                name,
                () ->
                        new AbilityPickaxeItem(
                                tools.tier(),
                                tools.ability(),
                                toolProperties(tools)
                                        .attributes(
                                                PickaxeItem.createAttributes(
                                                        tools.tier(),
                                                        PICKAXE_ATTACK_DAMAGE,
                                                        PICKAXE_ATTACK_SPEED))));
    }

    private static DeferredItem<AbilityShovelItem> shovel(String name, ToolSet tools) {
        return EQUIPMENT.register(
                name,
                () ->
                        new AbilityShovelItem(
                                tools.tier(),
                                tools.ability(),
                                toolProperties(tools)
                                        .attributes(
                                                ShovelItem.createAttributes(
                                                        tools.tier(),
                                                        SHOVEL_ATTACK_DAMAGE,
                                                        SHOVEL_ATTACK_SPEED))));
    }

    private static DeferredItem<AbilityHoeItem> hoe(String name, ToolSet tools) {
        return EQUIPMENT.register(
                name,
                () ->
                        new AbilityHoeItem(
                                tools.tier(),
                                tools.ability(),
                                toolProperties(tools)
                                        .attributes(
                                                HoeItem.createAttributes(
                                                        tools.tier(),
                                                        HOE_ATTACK_DAMAGE,
                                                        HOE_ATTACK_SPEED))));
    }

    private static DeferredItem<ArmorItem> armor(String name, ArmorSet armor, ArmorItem.Type type) {
        return EQUIPMENT.register(
                name,
                () ->
                        new ArmorItem(
                                armor.material(),
                                type,
                                properties(armor.fireResistant())
                                        .durability(
                                                type.getDurability(armor.durabilityMultiplier()))));
    }

    private static Item.Properties toolProperties(ToolSet tools) {
        return properties(tools.fireResistant());
    }

    private static Item.Properties properties(boolean fireResistant) {
        Item.Properties properties = new Item.Properties();
        return fireResistant ? properties.fireResistant() : properties;
    }

    private record ToolSet(Tier tier, ToolAbility ability, boolean fireResistant) {}

    private record ArmorSet(
            Holder<ArmorMaterial> material, int durabilityMultiplier, boolean fireResistant) {}
}
