package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.core.ScorchsteelStealthState;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class CustomAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DarkCaverns.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>>
            GATEWAY_COOLDOWN_UNTIL =
                    ATTACHMENTS.register(
                            "gateway_cooldown_until",
                            () -> AttachmentType.builder(() -> 0L).serialize(Codec.LONG).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ScorchsteelStealthState>>
            SCORCHSTEEL_STEALTH =
                    ATTACHMENTS.register(
                            "scorchsteel_stealth",
                            () -> AttachmentType.builder(ScorchsteelStealthState::new).build());

    private CustomAttachments() {}

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }
}
