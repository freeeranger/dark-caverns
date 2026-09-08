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
    private final ModConfigSpec.IntValue scorchsteelStealthStandstillTicks;
    private final ModConfigSpec.DoubleValue corruptedPearlScanRadius;
    private final ModConfigSpec.DoubleValue shroombombExplosionPower;
    private final ModConfigSpec.BooleanValue scorchhoundBypassShields;

    private final ModConfigSpec.IntValue scorchhoundSpawnChance;
    private final ModConfigSpec.IntValue scorchlingSpawnChance;
    private final ModConfigSpec.IntValue luminiteGolemSpawnChance;
    private final ModConfigSpec.IntValue luminiteFoxSpawnChance;
    private final ModConfigSpec.IntValue camorockSpawnChance;
    private final ModConfigSpec.IntValue moltenerSpawnChance;
    private final ModConfigSpec.IntValue shroomlingSpawnChance;
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
                                "Number of Cracked Bedrock veins attempted per chunk in the"
                                        + " Overworld bedrock layer.")
                        .defineInRange("crackedBedrockVeinCount", 4, 1, 32);
        crackedBedrockVeinSize =
                builder.comment("Maximum size of Cracked Bedrock veins.")
                        .defineInRange("crackedBedrockVeinSize", 4, 1, 16);
        builder.pop();

        builder.push("gameplay");
        gatewayCooldownTicks =
                builder.comment(
                                "Cooldown in ticks before a player can use a gateway again"
                                        + " after teleporting (20 ticks = 1s).")
                        .defineInRange("gatewayCooldownTicks", 175, 0, 1200);
        scorchsteelStealthStandstillTicks =
                builder.comment(
                                "Ticks of standing still required to trigger Scorchsteel"
                                        + " invisibility (20 ticks = 1s).")
                        .defineInRange("scorchsteelStealthStandstillTicks", 20, 0, 200);
        corruptedPearlScanRadius =
                builder.comment(
                                "Radius around the player used to find a Corrupted Pearl"
                                        + " target.")
                        .defineInRange("corruptedPearlScanRadius", 5.0, 1.0, 32.0);
        shroombombExplosionPower =
                builder.comment("Explosion power of thrown Shroombombs (TNT is 4.0).")
                        .defineInRange("shroombombExplosionPower", 2.5, 0.5, 8.0);
        scorchhoundBypassShields =
                builder.comment(
                                "Whether Scorchhounds fling players even when an attack is"
                                        + " blocked by a shield.")
                        .define("scorchhoundBypassShields", true);
        builder.pop();

        builder.push("entities");
        builder.push("spawn_chances");
        scorchhoundSpawnChance = spawnChance(builder, "scorchhoundSpawnChance", 6);
        scorchlingSpawnChance = spawnChance(builder, "scorchlingSpawnChance", 6);
        luminiteGolemSpawnChance = spawnChance(builder, "luminiteGolemSpawnChance", 10);
        luminiteFoxSpawnChance = spawnChance(builder, "luminiteFoxSpawnChance", 10);
        camorockSpawnChance = spawnChance(builder, "camorockSpawnChance", 7);
        moltenerSpawnChance = spawnChance(builder, "moltenerSpawnChance", 4);
        shroomlingSpawnChance = spawnChance(builder, "shroomlingSpawnChance", 6);
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

    public static int scorchsteelStealthStandstillTicks() {
        return VALUES.scorchsteelStealthStandstillTicks.get();
    }

    public static double corruptedPearlScanRadius() {
        return VALUES.corruptedPearlScanRadius.get();
    }

    public static float shroombombExplosionPower() {
        return VALUES.shroombombExplosionPower.get().floatValue();
    }

    public static boolean scorchhoundBypassShields() {
        return VALUES.scorchhoundBypassShields.get();
    }

    public static int scorchhoundSpawnChance() {
        return VALUES.scorchhoundSpawnChance.get();
    }

    public static int scorchlingSpawnChance() {
        return VALUES.scorchlingSpawnChance.get();
    }

    public static int luminiteGolemSpawnChance() {
        return VALUES.luminiteGolemSpawnChance.get();
    }

    public static int luminiteFoxSpawnChance() {
        return VALUES.luminiteFoxSpawnChance.get();
    }

    public static int camorockSpawnChance() {
        return VALUES.camorockSpawnChance.get();
    }

    public static int moltenerSpawnChance() {
        return VALUES.moltenerSpawnChance.get();
    }

    public static int shroomlingSpawnChance() {
        return VALUES.shroomlingSpawnChance.get();
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
