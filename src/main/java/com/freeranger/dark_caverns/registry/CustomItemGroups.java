package com.freeranger.dark_caverns.registry;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CustomItemGroups {
    public static final ItemGroup GROUP = new ItemGroup("dark_caverns"){
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CustomBlocks.LUMINITE_ORE.get());
        }
    };

}
