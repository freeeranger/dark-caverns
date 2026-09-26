package com.freeranger.dark_caverns.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/** Server-authoritative gameplay and world-generation settings. */
public final class ServerConfig {
    public static final ModConfigSpec SPEC;

    private static final ServerConfig VALUES;

    static {
        Pair<ServerConfig, ModConfigSpec> configured =
                new ModConfigSpec.Builder().configure(ServerConfig::new);
        VALUES = configured.getLeft();
        SPEC = configured.getRight();
    }

    private final ModConfigSpec.BooleanValue generateForgottenTower;
    private final ModConfigSpec.IntValue crackedBedrockVeinCount;
    private final ModConfigSpec.IntValue crackedBedrockVeinSize;

    private final ModConfigSpec.IntValue gatewayCooldownTicks;
    private final ModConfigSpec.DoubleValue corruptedPearlScanRadius;
    private final ModConfigSpec.DoubleValue shroombombExplosionPower;

    private final ModConfigSpec.IntValue shroomieSpawnChance;

    private ServerConfig(ModConfigSpec.Builder builder) {
        builder.push("worldgen");
        generateForgottenTower =
                builder.comment(
                                "Whether the Forgotten Tower structure generates in Overworld"
                                        + " forests.")
                        .define("generateForgottenTower", true);
        crackedBedrockVeinCount =
                builder.comment(
                                "Number of Cracked Bedrock patches attempted per chunk on the"
                                        + " exposed Overworld bedrock floor.")
                        .defineInRange("crackedBedrockVeinCount", 8, 1, 32);
        crackedBedrockVeinSize =
                builder.comment("Maximum size of Cracked Bedrock patches.")
                        .defineInRange("crackedBedrockVeinSize", 5, 1, 16);
        builder.pop();

        builder.push("gameplay");
        gatewayCooldownTicks =
                builder.comment(
                                "Cooldown in ticks before a player can use a gateway again"
                                        + " after teleporting (20 ticks = 1s).")
                        .defineInRange("gatewayCooldownTicks", 175, 0, 1200);
        corruptedPearlScanRadius =
                builder.comment(
                                "Radius around the player used to find a Corrupted Pearl"
                                        + " target.")
                        .defineInRange("corruptedPearlScanRadius", 5.0, 1.0, 32.0);
        shroombombExplosionPower =
                builder.comment("Explosion power of thrown Shroombombs (TNT is 4.0).")
                        .defineInRange("shroombombExplosionPower", 2.5, 0.5, 8.0);
        builder.pop();

        builder.push("entities");
        builder.push("spawn_chances");
        shroomieSpawnChance = spawnChance(builder, "shroomieSpawnChance", 6);
        builder.pop(2);
    }

    public static boolean generateForgottenTower() {
        return VALUES.generateForgottenTower.get();
    }

    public static int crackedBedrockVeinCount() {
        return VALUES.crackedBedrockVeinCount.get();
    }

    public static int crackedBedrockVeinSize() {
        return VALUES.crackedBedrockVeinSize.get();
    }

    public static int gatewayCooldownTicks() {
        return VALUES.gatewayCooldownTicks.get();
    }

    public static double corruptedPearlScanRadius() {
        return VALUES.corruptedPearlScanRadius.get();
    }

    public static float shroombombExplosionPower() {
        return VALUES.shroombombExplosionPower.get().floatValue();
    }

    public static int shroomieSpawnChance() {
        return VALUES.shroomieSpawnChance.get();
    }

    private static ModConfigSpec.IntValue spawnChance(
            ModConfigSpec.Builder builder, String name, int defaultValue) {
        return builder.comment("Spawn rarity expressed as a 1-in-N chance. Lower is more common.")
                .defineInRange(name, defaultValue, 1, 100);
    }
}
