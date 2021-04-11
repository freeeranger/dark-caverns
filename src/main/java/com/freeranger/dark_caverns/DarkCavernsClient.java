package com.freeranger.dark_caverns;

import com.freeranger.dark_caverns.core.LuminiteHelmetLighting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

@OnlyIn(Dist.CLIENT)
public class DarkCavernsClient {

    public static void init(){
        MinecraftForge.EVENT_BUS.addListener((LivingEvent.LivingUpdateEvent event) -> LuminiteHelmetLighting.tick(event.getEntityLiving()));
    }
}
