package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, DarkCaverns.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_AMBIENT =
            register("entity.shroomie.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_HURT =
            register("entity.shroomie.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_DEATH =
            register("entity.shroomie.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_TRADE_YES =
            register("entity.shroomie.trade_yes");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_TRADE_NO =
            register("entity.shroomie.trade_no");
    public static final DeferredHolder<SoundEvent, SoundEvent> DARK_CAVERNS_MUSIC =
            register("ambient.dark_caverns_ambience");

    private CustomSoundEvents() {}

    public static final DeferredHolder<SoundEvent, SoundEvent> TANGLED_HALLOW_MUSIC =
            register("music.tangled_hallow");

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, SoundEvent::createVariableRangeEvent);
    }
}
