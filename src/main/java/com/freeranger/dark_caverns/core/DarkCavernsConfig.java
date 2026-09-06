package com.freeranger.dark_caverns.core;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class DarkCavernsConfig {
    public static final Common COMMON;
    public static final ModConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();
        COMMON = new Common(commonBuilder);
        COMMON_SPEC = commonBuilder.build();

        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
        CLIENT = new Client(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    private DarkCavernsConfig() {}

    public static final class Common {
        public final ModConfigSpec.BooleanValue generateForgottenTower;
        public final ModConfigSpec.IntValue crackedBedrockVeinCount;
        public final ModConfigSpec.IntValue crackedBedrockVeinSize;

        public final ModConfigSpec.IntValue gatewayCooldownTicks;
        public final ModConfigSpec.IntValue scorchsteelStealthStandstillTicks;
        public final ModConfigSpec.DoubleValue corruptedPearlScanRadius;
        public final ModConfigSpec.DoubleValue shroombombExplosionPower;
        public final ModConfigSpec.BooleanValue scorchhoundBypassShields;

        public final ModConfigSpec.IntValue scorchhoundSpawnChance;
        public final ModConfigSpec.IntValue scorchlingSpawnChance;
        public final ModConfigSpec.IntValue luminiteGolemSpawnChance;
        public final ModConfigSpec.IntValue luminiteFoxSpawnChance;
        public final ModConfigSpec.IntValue camorockSpawnChance;
        public final ModConfigSpec.IntValue moltenerSpawnChance;
        public final ModConfigSpec.IntValue shroomlingSpawnChance;
        public final ModConfigSpec.IntValue shroomieSpawnChance;

        private Common(ModConfigSpec.Builder builder) {
            builder.push("worldgen");
            generateForgottenTower = builder
                    .comment("Whether the Forgotten Tower structure generates in Overworld forests.")
                    .define("generateForgottenTower", true);
            crackedBedrockVeinCount = builder
                    .comment("Number of Cracked Bedrock veins attempted per chunk in the Overworld bedrock layer.")
                    .defineInRange("crackedBedrockVeinCount", 4, 1, 32);
            crackedBedrockVeinSize = builder
                    .comment("Maximum size of Cracked Bedrock veins.")
                    .defineInRange("crackedBedrockVeinSize", 4, 1, 16);
            builder.pop();

            builder.push("gameplay");
            gatewayCooldownTicks = builder
                    .comment("Cooldown in ticks before a player can use a gateway again after teleporting (20 ticks = 1s).")
                    .defineInRange("gatewayCooldownTicks", 175, 0, 1200);
            scorchsteelStealthStandstillTicks = builder
                    .comment("Ticks of standing still required to trigger Scorchsteel invisibility (20 ticks = 1s).")
                    .defineInRange("scorchsteelStealthStandstillTicks", 20, 0, 200);
            corruptedPearlScanRadius = builder
                    .comment("Radius around the player used to find a Corrupted Pearl target.")
                    .defineInRange("corruptedPearlScanRadius", 5.0, 1.0, 32.0);
            shroombombExplosionPower = builder
                    .comment("Explosion power of thrown Shroombombs (TNT is 4.0).")
                    .defineInRange("shroombombExplosionPower", 4.0, 0.5, 20.0);
            scorchhoundBypassShields = builder
                    .comment("Whether Scorchhounds fling players even when an attack is blocked by a shield.")
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

        private static ModConfigSpec.IntValue spawnChance(
                ModConfigSpec.Builder builder,
                String name,
                int defaultValue
        ) {
            return builder
                    .comment("Spawn rarity expressed as a 1-in-N chance. Lower is more common.")
                    .defineInRange(name, defaultValue, 1, 100);
        }
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue enableDynamicLighting;
        public final ModConfigSpec.IntValue maxDynamicLightDistance;

        private Client(ModConfigSpec.Builder builder) {
            builder.push("rendering");
            enableDynamicLighting = builder
                    .comment("Enable dynamic headlamp lighting for the Luminite Helmet on the client.")
                    .define("enableDynamicLighting", true);
            maxDynamicLightDistance = builder
                    .comment("Maximum distance for rendering dynamic light from glowing entities.")
                    .defineInRange("maxDynamicLightDistance", 64, 16, 256);
            builder.pop();
        }
    }
}
