package com.freeranger.dark_caverns.registry;

import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class CustomParticles {
    public static final DeferredHolder<
                    net.minecraft.core.particles.ParticleType<?>, SimpleParticleType>
            LUMINITE_FLAME =
                    ModRegistries.PARTICLES.register(
                            "luminite_flame", () -> new SimpleParticleType(false));

    private CustomParticles() {}

    public static void bootstrap() {
        // Forces class initialization before the deferred register attaches to the event bus.
    }
}
