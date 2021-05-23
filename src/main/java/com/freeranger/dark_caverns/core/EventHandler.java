package com.freeranger.dark_caverns.core;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.capabilities.GatewayCooldownCapability;
import com.freeranger.dark_caverns.capabilities.GatewayCooldownProvider;
import com.freeranger.dark_caverns.registry.CustomStructures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
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
                    new Random().nextInt(13) + 49,
                    CustomStructures.FORGOTTEN_TOWER.get(),
                    MapDecoration.Type.RED_X,
                    5,
                    15
            ));
        }
    }

}
