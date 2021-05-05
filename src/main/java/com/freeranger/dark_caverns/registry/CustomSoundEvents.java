package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DarkCaverns.MOD_ID);

    public static final RegistryObject<SoundEvent> SCORCHLING_AMBIENT = register("entity.scorchling.ambient");
    public static final RegistryObject<SoundEvent> SCORCHLING_HURT = register("entity.scorchling.hurt");
    public static final RegistryObject<SoundEvent> SCORCHLING_DEATH = register("entity.scorchling.death");

    private static RegistryObject<SoundEvent> register(String registryName) {
        return SOUNDS.register(registryName, () -> new SoundEvent(new ResourceLocation(DarkCaverns.MOD_ID, registryName)));
    }
}
