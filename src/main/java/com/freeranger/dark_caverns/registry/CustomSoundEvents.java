package com.freeranger.dark_caverns.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class CustomSoundEvents {
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORCHLING_AMBIENT = register("entity.scorchling.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORCHLING_HURT = register("entity.scorchling.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORCHLING_DEATH = register("entity.scorchling.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORCHHOUND_AMBIENT = register("entity.scorchhound.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORCHHOUND_HURT = register("entity.scorchhound.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SCORCHHOUND_DEATH = register("entity.scorchhound.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_FOX_AMBIENT = register("entity.luminite_fox.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_FOX_HURT = register("entity.luminite_fox.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_FOX_DEATH = register("entity.luminite_fox.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOLTENER_AMBIENT = register("entity.moltener.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOLTENER_HURT = register("entity.moltener.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOLTENER_DEATH = register("entity.moltener.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_AMBIENT = register("entity.shroomie.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_HURT = register("entity.shroomie.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_DEATH = register("entity.shroomie.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_TRADE_YES = register("entity.shroomie.trade_yes");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMIE_TRADE_NO = register("entity.shroomie.trade_no");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMLING_AMBIENT = register("entity.shroomling.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMLING_HURT = register("entity.shroomling.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHROOMLING_DEATH = register("entity.shroomling.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_GOLEM_STEP = register("entity.luminite_golem.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_GOLEM_HURT = register("entity.luminite_golem.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_GOLEM_DEATH = register("entity.luminite_golem.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LUMINITE_GOLEM_ATTACK = register("entity.luminite_golem.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> CAMOROCK_HURT = register("entity.camorock.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CAMOROCK_DEATH = register("entity.camorock.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DARK_CAVERNS_MUSIC = register("ambient.dark_caverns_ambience");

    private CustomSoundEvents() {
    }

    public static void bootstrap() {
        // Forces class initialization before the deferred register attaches to the event bus.
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return ModRegistries.SOUNDS.register(
                name,
                location -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(
                        location.getNamespace(),
                        location.getPath()
                ))
        );
    }
}
