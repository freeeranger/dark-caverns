package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, DarkCaverns.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LUMINITE_FLAME =
            PARTICLES.register("luminite_flame", () -> new SimpleParticleType(false));

    private CustomParticles() {}

    public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }
}
