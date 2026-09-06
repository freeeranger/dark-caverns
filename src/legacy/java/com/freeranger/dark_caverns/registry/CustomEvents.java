package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.events.CorruptedPearlTeleportEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;

public class CustomEvents {
    public static CorruptedPearlTeleportEvent onCorruptedPearlLand(ServerPlayerEntity entity, double targetX, double targetY, double targetZ, CorruptedPearlEntity pearlEntity, float attackDamage)
    {
        CorruptedPearlTeleportEvent event = new CorruptedPearlTeleportEvent(entity, targetX, targetY, targetZ, pearlEntity, attackDamage);
        /*
        TODO:
         * see what is done with this function in vanilla ender
         * pearl code since the function that was inside
         * setAttackDamage was supposed to be deprecated in 1.17
         */
        event.setAttackDamage(1f);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
}
