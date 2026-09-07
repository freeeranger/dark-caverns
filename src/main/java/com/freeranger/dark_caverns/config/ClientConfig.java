package com.freeranger.dark_caverns.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/** Client-only rendering settings. */
public final class ClientConfig {
    public static final ModConfigSpec SPEC;

    private static final ClientConfig VALUES;

    static {
        Pair<ClientConfig, ModConfigSpec> configured =
                new ModConfigSpec.Builder().configure(ClientConfig::new);
        VALUES = configured.getLeft();
        SPEC = configured.getRight();
    }

    private final ModConfigSpec.BooleanValue enableDynamicLighting;
    private final ModConfigSpec.IntValue maxDynamicLightDistance;

    private ClientConfig(ModConfigSpec.Builder builder) {
        builder.push("rendering");
        enableDynamicLighting =
                builder.comment(
                                "Enable dynamic headlamp lighting for the Luminite Helmet on the"
                                        + " client.")
                        .define("enableDynamicLighting", true);
        maxDynamicLightDistance =
                builder.comment(
                                "Maximum distance for rendering dynamic light from glowing"
                                        + " entities.")
                        .defineInRange("maxDynamicLightDistance", 64, 16, 256);
        builder.pop();
    }

    public static boolean enableDynamicLighting() {
        return VALUES.enableDynamicLighting.get();
    }

    public static int maxDynamicLightDistance() {
        return VALUES.maxDynamicLightDistance.get();
    }
}
