package com.freeranger.dark_caverns.armor;

import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class ShroomstoneArmorItem extends ArmorItem {
    public ShroomstoneArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if(player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.SHROOMSTONE_HELMET.get() &&
                player.getItemBySlot(EquipmentSlotType.CHEST).getItem() == CustomItems.SHROOMSTONE_CHESTPLATE.get() &&
                player.getItemBySlot(EquipmentSlotType.LEGS).getItem() == CustomItems.SHROOMSTONE_LEGGINGS.get() &&
                player.getItemBySlot(EquipmentSlotType.FEET).getItem() == CustomItems.SHROOMSTONE_BOOTS.get()){
            player.addEffect(new EffectInstance(Effects.JUMP, 20, 3));
            player.fallDistance = 0;
        }
    }
}
