package com.freeranger.dark_caverns.armor;

import com.freeranger.dark_caverns.registry.CustomItems;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ScorchsteelArmorItem extends ArmorItem {
    private double lastPosX, lastPosY, lastPosZ;
    private int timer = 0;

    public ScorchsteelArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        int armorCount = 0;

        if(player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.SCORCHSTEEL_HELMET.get()){
            armorCount++;
        }
        if(player.getItemBySlot(EquipmentSlotType.CHEST).getItem() == CustomItems.SCORCHSTEEL_CHESTPLATE.get()){
            armorCount++;
        }
        if(player.getItemBySlot(EquipmentSlotType.LEGS).getItem() == CustomItems.SCORCHSTEEL_LEGGINGS.get()){
            armorCount++;
        }
        if(player.getItemBySlot(EquipmentSlotType.FEET).getItem() == CustomItems.SCORCHSTEEL_BOOTS.get()){
            armorCount++;
        }

        if(armorCount > 0){
            if(lastPosX == player.getX() && lastPosY == player.getY() && lastPosZ == player.getZ()){
                timer++;
                int effectTime = 20;
                if(armorCount == 2) effectTime = 20 * 2;
                else if(armorCount == 3) effectTime = 20 * 3;
                else if(armorCount == 4) effectTime = 20 * 6;
                if(timer >= 20){
                    player.addEffect(new EffectInstance(Effects.INVISIBILITY, effectTime, 0, false, false, true));
                }
            }else{
                timer = 0;
            }
        }
        lastPosX = player.getX();
        lastPosY = player.getY();
        lastPosZ = player.getZ();
    }
}
