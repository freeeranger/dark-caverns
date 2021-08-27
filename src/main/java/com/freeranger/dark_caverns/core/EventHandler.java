package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.capabilities.GatewayCooldownCapability;
import com.freeranger.dark_caverns.capabilities.GatewayCooldownProvider;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomStructures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class EventHandler {
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity){
            GatewayCooldownProvider provider = new GatewayCooldownProvider();
            event.addCapability(new ResourceLocation(DarkCaverns.MOD_ID, "gateway_cooldown"), provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        event.player.getCapability(GatewayCooldownCapability.GATEWAY_COOLDOWN_CAPABILITY).ifPresent(h -> {
            int cooldown = h.getCooldown();
            if(cooldown > 0) {
                h.setCooldown(cooldown - 1);
                return;
            }
            if(cooldown < 0) h.setCooldown(0);
        });
    }
    @SubscribeEvent
    public void addTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            event.getTrades().get(5).add(new VillagerTrades.EmeraldForMapTrade(
                    36,
                    CustomStructures.FORGOTTEN_TOWER.get(),
                    MapDecoration.Type.RED_X,
                    1,
                    15
            ));
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event){
        if (event.getEntityLiving() instanceof PlayerEntity){
            if(event.getSource() == DamageSource.FALL){
                int shroomstoneArmorCount = 0;
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.SHROOMSTONE_HELMET.get()){
                    shroomstoneArmorCount++;
                }
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.CHEST).getItem() == CustomItems.SHROOMSTONE_CHESTPLATE.get()){
                    shroomstoneArmorCount++;
                }
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.LEGS).getItem() == CustomItems.SHROOMSTONE_LEGGINGS.get()){
                    shroomstoneArmorCount++;
                }
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.FEET).getItem() == CustomItems.SHROOMSTONE_BOOTS.get()){
                    shroomstoneArmorCount++;
                }
                if(shroomstoneArmorCount > 0){
                    if(shroomstoneArmorCount == 1){
                        event.setAmount(event.getAmount() * 0.75f);
                    }else if(shroomstoneArmorCount == 2){
                        event.setAmount(event.getAmount() * 0.5f);
                    }else if(shroomstoneArmorCount == 3){
                        event.setAmount(event.getAmount() * 0.25f);
                    }else {
                        event.setAmount(0);
                    }
                }
            }else if(event.getSource() == DamageSource.IN_FIRE ||
                     event.getSource() == DamageSource.ON_FIRE ||
                     event.getSource() == DamageSource.LAVA
            ){
                int hellstoneArmorCount = 0;
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.HELLSTONE_HELMET.get()){
                    hellstoneArmorCount++;
                }
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.CHEST).getItem() == CustomItems.HELLSTONE_CHESTPLATE.get()){
                    hellstoneArmorCount++;
                }
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.LEGS).getItem() == CustomItems.HELLSTONE_LEGGINGS.get()){
                    hellstoneArmorCount++;
                }
                if(event.getEntityLiving().getItemBySlot(EquipmentSlotType.FEET).getItem() == CustomItems.HELLSTONE_BOOTS.get()){
                    hellstoneArmorCount++;
                }
                if(hellstoneArmorCount > 0){
                    if(hellstoneArmorCount == 1){
                        event.setAmount(event.getAmount() * 0.75f);
                    }else if(hellstoneArmorCount == 2){
                        event.setAmount(event.getAmount() * 0.5f);
                    }else if(hellstoneArmorCount == 3){
                        event.setAmount(event.getAmount() * 0.25f);
                    }else {
                        event.setAmount(0);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
        if(event.getEntityLiving() instanceof MonsterEntity){
            if(((MonsterEntity) event.getEntityLiving()).getTarget() instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity) ((MonsterEntity) event.getEntityLiving()).getTarget();
                if(player.hasEffect(Effects.INVISIBILITY) &&
                        (player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == CustomItems.SCORCHSTEEL_HELMET.get() ||
                        player.getItemBySlot(EquipmentSlotType.CHEST).getItem() == CustomItems.SCORCHSTEEL_CHESTPLATE.get() ||
                        player.getItemBySlot(EquipmentSlotType.LEGS).getItem() == CustomItems.SCORCHSTEEL_LEGGINGS.get() ||
                        player.getItemBySlot(EquipmentSlotType.FEET).getItem() == CustomItems.SCORCHSTEEL_BOOTS.get())){
                    ((MonsterEntity)event.getEntityLiving()).setTarget(null);
                }
            }
        }
    }

}
