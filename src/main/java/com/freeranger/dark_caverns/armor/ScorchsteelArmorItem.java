package com.freeranger.dark_caverns.armor;

import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

public class ScorchsteelArmorItem extends ArmorItem {
    private double lastPosX, lastPosY, lastPosZ;
    private int timer = 0;

    public ScorchsteelArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if(player.hasEffect(Effects.INVISIBILITY)) {
            player.setInvisible(true);
            return;
        }
        if(player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.SCORCHSTEEL_HELMET.get() &&
                player.getItemBySlot(EquipmentSlotType.CHEST).getItem() == CustomItems.SCORCHSTEEL_CHESTPLATE.get() &&
                player.getItemBySlot(EquipmentSlotType.LEGS).getItem() == CustomItems.SCORCHSTEEL_LEGGINGS.get() &&
                player.getItemBySlot(EquipmentSlotType.FEET).getItem() == CustomItems.SCORCHSTEEL_BOOTS.get()){
            if(lastPosX == player.getX() && lastPosY == player.getY() && lastPosZ == player.getZ()){
                timer++;
                if(timer >= 20){
                    player.setInvisible(true);
                }
            }else{
                timer = 0;
                player.setInvisible(false);
            }
            lastPosX = player.getX();
            lastPosY = player.getY();
            lastPosZ = player.getZ();
        }
    }
}
