package com.freeranger.dark_caverns.core;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DarkCavernsConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Client CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();

        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    public static class Common {
        // World Generation
        public final ForgeConfigSpec.BooleanValue generateForgottenTower;
        public final ForgeConfigSpec.IntValue crackedBedrockVeinCount;
        public final ForgeConfigSpec.IntValue crackedBedrockVeinSize;

        // Gameplay Mechanics
        public final ForgeConfigSpec.IntValue gatewayCooldownTicks;
        public final ForgeConfigSpec.IntValue scorchsteelStealthStandstillTicks;
        public final ForgeConfigSpec.DoubleValue corruptedPearlScanRadius;
        public final ForgeConfigSpec.DoubleValue shroombombExplosionPower;
        public final ForgeConfigSpec.BooleanValue scorchhoundBypassShields;

        // Mob Spawn Chances (1 in N chance)
        public final ForgeConfigSpec.IntValue scorchhoundSpawnChance;
        public final ForgeConfigSpec.IntValue scorchlingSpawnChance;
        public final ForgeConfigSpec.IntValue luminiteGolemSpawnChance;
        public final ForgeConfigSpec.IntValue luminiteFoxSpawnChance;
        public final ForgeConfigSpec.IntValue camorockSpawnChance;
        public final ForgeConfigSpec.IntValue moltenerSpawnChance;
        public final ForgeConfigSpec.IntValue shroomlingSpawnChance;
        public final ForgeConfigSpec.IntValue shroomieSpawnChance;

        Common(ForgeConfigSpec.Builder builder) {
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
                    .comment("Ticks of standing completely still required to trigger Scorchsteel invisibility (20 ticks = 1s).")
                    .defineInRange("scorchsteelStealthStandstillTicks", 20, 0, 200);
            corruptedPearlScanRadius = builder
                    .comment("Radius in blocks around the player to find and banish a nearby mob when throwing a Corrupted Pearl.")
                    .defineInRange("corruptedPearlScanRadius", 5.0, 1.0, 32.0);
            shroombombExplosionPower = builder
                    .comment("Explosion power of thrown Shroombombs (TNT is 4.0).")
                    .defineInRange("shroombombExplosionPower", 4.0, 0.5, 20.0);
            scorchhoundBypassShields = builder
                    .comment("Whether Scorchhounds throw players into the air even when blocked by a shield.")
                    .define("scorchhoundBypassShields", true);
            builder.pop();

            builder.push("entities");
            builder.push("spawn_chances");
            scorchhoundSpawnChance = builder
                    .comment("Spawn rarity for Scorchhounds in Molten Depths (1 in N chance). Lower is more common.")
                    .defineInRange("scorchhoundSpawnChance", 6, 1, 100);
            scorchlingSpawnChance = builder
                    .comment("Spawn rarity for Scorchlings in Molten Depths (1 in N chance).")
                    .defineInRange("scorchlingSpawnChance", 6, 1, 100);
            luminiteGolemSpawnChance = builder
                    .comment("Spawn rarity for Luminite Golems (1 in N chance).")
                    .defineInRange("luminiteGolemSpawnChance", 10, 1, 100);
            luminiteFoxSpawnChance = builder
                    .comment("Spawn rarity for Luminite Foxes (1 in N chance).")
                    .defineInRange("luminiteFoxSpawnChance", 10, 1, 100);
            camorockSpawnChance = builder
                    .comment("Spawn rarity for Camorocks (1 in N chance).")
                    .defineInRange("camorockSpawnChance", 7, 1, 100);
            moltenerSpawnChance = builder
                    .comment("Spawn rarity for Molteners (1 in N chance).")
                    .defineInRange("moltenerSpawnChance", 4, 1, 100);
            shroomlingSpawnChance = builder
                    .comment("Spawn rarity for Shroomlings in Glimmershroom Forests (1 in N chance).")
                    .defineInRange("shroomlingSpawnChance", 6, 1, 100);
            shroomieSpawnChance = builder
                    .comment("Spawn rarity for Shroomies in Glimmershroom Forests (1 in N chance).")
                    .defineInRange("shroomieSpawnChance", 6, 1, 100);
            builder.pop();
            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue enableDynamicLighting;
        public final ForgeConfigSpec.IntValue maxDynamicLightDistance;

        Client(ForgeConfigSpec.Builder builder) {
            builder.push("rendering");
            enableDynamicLighting = builder
                    .comment("Enable dynamic headlamp lighting for the Luminite Helmet on the client.")
                    .define("enableDynamicLighting", true);
            maxDynamicLightDistance = builder
                    .comment("Maximum distance in blocks for rendering dynamic lights from glowing entities.")
                    .defineInRange("maxDynamicLightDistance", 64, 16, 256);
            builder.pop();
        }
    }
}
