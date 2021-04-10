package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkCaverns.MOD_ID);

    public static final RegistryObject<Item> LUMINITE_DUST = ITEMS.register(
            "luminite_dust",
            () -> new Item(new Item.Properties().tab(CustomItemGroups.GROUP))
    );
}