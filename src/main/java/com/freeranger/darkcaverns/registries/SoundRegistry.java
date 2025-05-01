package com.freeranger.darkcaverns.registries;

import com.freeranger.darkcaverns.DarkCaverns;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DarkCaverns.MODID);
    
    public static final Supplier<SoundEvent> MUSIC_WHISPERS_BELOW = createSoundEvent("music.whispers_below");
    public static final Supplier<SoundEvent> MUSIC_CRYSTAL_DREAMS = createSoundEvent("music.crystal_dreams");
    
    private static Supplier<SoundEvent> createSoundEvent(String id) {
        return  SOUND_EVENTS.register(
            id, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(DarkCaverns.MODID, id)
            )
        );
    }
    
    public static void register(IEventBus bus){
        SOUND_EVENTS.register(bus);
    }
}
