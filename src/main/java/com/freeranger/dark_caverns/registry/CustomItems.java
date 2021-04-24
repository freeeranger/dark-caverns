package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.armor.LuminiteArmorMaterial;
import com.freeranger.dark_caverns.armor.PlatinumArmorMaterial;
import com.freeranger.dark_caverns.items.ThrowableLuminiteTorchItem;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkCaverns.MOD_ID);

    public static final RegistryObject<Item> LUMINITE_DUST = ITEMS.register(
            "luminite_dust",
            () -> new Item(new Item.Properties().tab(CustomItemGroups.GROUP))
    );

    public static final RegistryObject<Item> PLATINUM_INGOT = ITEMS.register(
            "platinum_ingot",
            () -> new Item(new Item.Properties().tab(CustomItemGroups.GROUP))
    );

    public static final RegistryObject<Item> THROWABLE_LUMINITE_TORCH = ITEMS.register(
            "throwable_luminite_torch",
            () -> new ThrowableLuminiteTorchItem(new Item.Properties().tab(CustomItemGroups.GROUP))
    );

    public static final RegistryObject<Item> LUMINITE_HELMET = ITEMS.register(
            "luminite_helmet",
            () -> new ArmorItem(
                    LuminiteArmorMaterial.LUMINITE,
                    EquipmentSlotType.HEAD,
                    new Item.Properties().tab(CustomItemGroups.GROUP)
            )
    );

    public static final RegistryObject<Item> PLATINUM_HELMET = ITEMS.register(
            "platinum_helmet",
            () -> new ArmorItem(
                    PlatinumArmorMaterial.PLATINUM,
                    EquipmentSlotType.HEAD,
                    new Item.Properties().tab(CustomItemGroups.GROUP)
            )
    );

    public static final RegistryObject<Item> PLATINUM_CHESTPLATE = ITEMS.register(
            "platinum_chestplate",
            () -> new ArmorItem(
                    PlatinumArmorMaterial.PLATINUM,
                    EquipmentSlotType.CHEST,
                    new Item.Properties().tab(CustomItemGroups.GROUP)
            )
    );

    public static final RegistryObject<Item> PLATINUM_LEGGINGS = ITEMS.register(
            "platinum_leggings",
            () -> new ArmorItem(
                    PlatinumArmorMaterial.PLATINUM,
                    EquipmentSlotType.LEGS,
                    new Item.Properties().tab(CustomItemGroups.GROUP)
            )
    );

    public static final RegistryObject<Item> PLATINUM_BOOTS = ITEMS.register(
            "platinum_boots",
            () -> new ArmorItem(
                    PlatinumArmorMaterial.PLATINUM,
                    EquipmentSlotType.FEET,
                    new Item.Properties().tab(CustomItemGroups.GROUP)
            )
    );

    public static final RegistryObject<Item> LUMINITE_TORCH = ITEMS.register(
            "luminite_torch",
            () -> new WallOrFloorItem(
                    CustomBlocks.LUMINITE_TORCH.get(),
                    CustomBlocks.LUMINITE_WALL_TORCH.get(),
                    new Item.Properties().tab(CustomItemGroups.GROUP)
            )
    );

}