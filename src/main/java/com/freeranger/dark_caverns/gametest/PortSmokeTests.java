package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.core.ExplorationTrades;
import com.freeranger.dark_caverns.core.GatewayCooldowns;
import com.freeranger.dark_caverns.entities.ShroomieEntity;
import com.freeranger.dark_caverns.events.CorruptedPearlTeleportEvent;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import com.freeranger.dark_caverns.registry.CustomFeatures;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PortSmokeTests {
    private PortSmokeTests() {}

    @GameTest(template = "sacred_torch")
    public static void persistenceGatewayAndFeatureHooksWork(GameTestHelper helper) {
        ShroomieEntity shroomie = CustomEntityTypes.SHROOMIE_ENTITY.get().create(helper.getLevel());
        helper.assertTrue(shroomie != null, "Shroomie factory failed");
        var shroomieOffers = shroomie.getOffers();
        helper.assertTrue(
                shroomieOffers.stream().map(offer -> offer.getResult().getItem()).distinct().count()
                        == shroomieOffers.size(),
                "Shroomie offers should not contain duplicate results");
        var shroomstoneOffers =
                shroomieOffers.stream()
                        .filter(offer -> offer.getResult().is(CustomItems.SHROOMSTONE_PIECE.get()))
                        .toList();
        helper.assertTrue(
                shroomstoneOffers.size() == 1,
                "Every Shroomie should have exactly one Shroomstone progression offer");
        var shroomstoneOffer = shroomstoneOffers.getFirst();
        shroomstoneOffer.setToOutOfStock();
        shroomie.restock();
        helper.assertFalse(
                shroomstoneOffer.isOutOfStock(), "Shroomie restocking should reset offer uses");
        shroomieOffers.remove(shroomstoneOffer);
        var preservedOffer = shroomieOffers.getFirst();
        preservedOffer.increaseUses();
        CompoundTag legacyShroomieData = new CompoundTag();
        shroomie.addAdditionalSaveData(legacyShroomieData);
        legacyShroomieData.remove("NextRestockGameTime");
        ShroomieEntity restoredShroomie =
                CustomEntityTypes.SHROOMIE_ENTITY.get().create(helper.getLevel());
        helper.assertTrue(restoredShroomie != null, "Second Shroomie factory failed");
        restoredShroomie.readAdditionalSaveData(legacyShroomieData);
        helper.assertTrue(
                restoredShroomie.getOffers().stream()
                                .filter(
                                        offer ->
                                                offer.getResult()
                                                        .is(CustomItems.SHROOMSTONE_PIECE.get()))
                                .count()
                        == 1,
                "Existing Shroomies without a Shroomstone trade should gain one when loaded");
        helper.assertTrue(
                restoredShroomie.getOffers().stream()
                        .anyMatch(
                                offer ->
                                        offer.getResult().is(preservedOffer.getResult().getItem())
                                                && offer.getUses() == 1),
                "Adding the progression trade should preserve existing offers and uses");
        BlockPos gatewayPos = new BlockPos(1, 2, 1);
        helper.setBlock(gatewayPos, CustomBlocks.CRACKED_BEDROCK.get());
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack key = new ItemStack(CustomItems.KEY_TO_THE_CAVERNS.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, key);
        helper.placeAt(player, key, gatewayPos.below(), Direction.UP);
        helper.assertBlockPresent(CustomBlocks.GATEWAY_TO_THE_CAVERNS.get(), gatewayPos);
        helper.assertTrue(key.isEmpty(), "Using the key in survival should consume it");
        helper.assertTrue(
                GatewayCooldowns.isReady(player, helper.getLevel()),
                "A new player should have no gateway cooldown");
        helper.assertFalse(
                player.hasData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL),
                "Checking a new gateway cooldown should not allocate state");
        GatewayCooldowns.start(player, helper.getLevel());
        helper.assertTrue(
                !GatewayCooldowns.isReady(player, helper.getLevel()),
                "Starting a gateway cooldown should block reuse");
        helper.assertTrue(
                player.hasData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL),
                "Gateway cooldown should use its registered data attachment");
        player.setData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL, helper.getLevel().getGameTime());
        helper.assertTrue(
                GatewayCooldowns.isReady(player, helper.getLevel()),
                "An expired gateway cooldown should allow reuse");
        helper.assertFalse(
                player.hasData(CustomAttachments.GATEWAY_COOLDOWN_UNTIL),
                "Expired gateway cooldown state should be removed");
        helper.assertTrue(
                helper.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.STRUCTURE)
                        .getTag(ExplorationTrades.FORGOTTEN_TOWER_MAP_DESTINATIONS)
                        .map(
                                tag ->
                                        tag.stream()
                                                .anyMatch(
                                                        holder ->
                                                                holder.is(
                                                                        net.minecraft.resources
                                                                                .ResourceKey.create(
                                                                                Registries
                                                                                        .STRUCTURE,
                                                                                net.minecraft
                                                                                        .resources
                                                                                        .ResourceLocation
                                                                                        .fromNamespaceAndPath(
                                                                                                DarkCaverns
                                                                                                        .MOD_ID,
                                                                                                "forgotten_tower")))))
                        .orElse(false),
                "Forgotten Tower map destination tag did not load");
        var cartographerTrades = new Int2ObjectOpenHashMap<List<VillagerTrades.ItemListing>>();
        for (int level = 1; level <= 5; level++) {
            cartographerTrades.put(level, new ArrayList<>());
        }
        NeoForge.EVENT_BUS.post(
                new VillagerTradesEvent(
                        cartographerTrades,
                        VillagerProfession.CARTOGRAPHER,
                        helper.getLevel().registryAccess()));
        helper.assertTrue(
                cartographerTrades.get(4).stream()
                        .anyMatch(VillagerTrades.TreasureMapForEmeralds.class::isInstance),
                "Expert cartographers did not receive the Forgotten Tower map trade");
        var serverPlayer =
                new ServerPlayer(
                        helper.getLevel().getServer(),
                        helper.getLevel(),
                        new GameProfile(UUID.randomUUID(), "test-mock-player"),
                        ClientInformation.createDefault());
        var pearl =
                new com.freeranger.dark_caverns.entities.CorruptedPearlEntity(
                        helper.getLevel(), serverPlayer);
        var teleportTarget = helper.spawn(EntityType.ZOMBIE, new BlockPos(6, 2, 2));
        var teleportEvent =
                new CorruptedPearlTeleportEvent(teleportTarget, serverPlayer, 1.0, 2.0, 3.0, pearl);
        teleportEvent.setCanceled(true);
        helper.assertTrue(
                teleportEvent.isCanceled(),
                "Corrupted Pearl teleport event must remain cancellable");
        helper.assertTrue(
                teleportEvent.getPearlEntity() == pearl,
                "Corrupted Pearl event lost its projectile context");
        helper.assertTrue(
                teleportEvent.getEntity() == teleportTarget
                        && teleportEvent.getPearlOwner() == serverPlayer,
                "Corrupted Pearl event should expose its target and owner separately");

        // Verify CrackedBedrockFeature generates on the surface of the bedrock stratum
        // (even under deepslate) and never replaces bedrock buried under other bedrock.
        BlockPos testOrigin = helper.absolutePos(new BlockPos(4, 0, 4));
        int testMinY = helper.getLevel().getMinBuildHeight();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.set(testOrigin.getX() + x, testMinY + 5, testOrigin.getZ() + z);
                helper.getLevel().setBlock(pos, Blocks.DEEPSLATE.defaultBlockState(), 3);
                pos.set(testOrigin.getX() + x, testMinY + 4, testOrigin.getZ() + z);
                helper.getLevel().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                pos.set(testOrigin.getX() + x, testMinY + 3, testOrigin.getZ() + z);
                helper.getLevel().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
            }
        }

        boolean generated =
                CustomFeatures.CRACKED_BEDROCK
                        .get()
                        .place(
                                new FeaturePlaceContext<>(
                                        Optional.empty(),
                                        helper.getLevel(),
                                        helper.getLevel().getChunkSource().getGenerator(),
                                        RandomSource.create(42L),
                                        testOrigin,
                                        NoneFeatureConfiguration.INSTANCE));

        helper.assertTrue(
                generated,
                "CrackedBedrockFeature should generate on surface bedrock covered by deepslate");

        int crackedCount = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                pos.set(testOrigin.getX() + x, testMinY + 4, testOrigin.getZ() + z);
                if (helper.getLevel().getBlockState(pos).is(CustomBlocks.CRACKED_BEDROCK.get())) {
                    crackedCount++;
                }
                pos.set(testOrigin.getX() + x, testMinY + 3, testOrigin.getZ() + z);
                helper.assertTrue(
                        helper.getLevel().getBlockState(pos).is(Blocks.BEDROCK),
                        "Bedrock buried underneath other bedrock must never be replaced with"
                                + " cracked bedrock");
            }
        }
        helper.assertTrue(
                crackedCount > 0,
                "Expected cracked bedrock to generate on the top bedrock surface under deepslate");

        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void blockAndEquipmentBehavior(GameTestHelper helper) {
        BlockPos plantPos = new BlockPos(2, 2, 2);
        helper.setBlock(plantPos.below(), CustomBlocks.GLIMMERGRASS_BLOCK.get());
        helper.assertTrue(
                CustomBlocks.GLIMMERGRASS
                        .get()
                        .defaultBlockState()
                        .canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                "Glimmergrass should survive on glimmergrass block");
        helper.setBlock(plantPos.below(), CustomBlocks.MOLTEN_CARFSTONE.get());
        helper.assertFalse(
                CustomBlocks.GLIMMERGRASS
                        .get()
                        .defaultBlockState()
                        .canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                "Glimmergrass should not survive on molten carfstone");
        helper.assertTrue(
                CustomBlocks.CHARRED_GRASS
                        .get()
                        .defaultBlockState()
                        .canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                "Charred grass should survive on molten carfstone");
        helper.setBlock(plantPos.below(), CustomBlocks.ASHY_MOLTEN_CARFSTONE.get());
        helper.assertTrue(
                CustomBlocks.ASHY_CHARRED_GRASS
                        .get()
                        .defaultBlockState()
                        .canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                "Ashy charred grass should survive on ashy molten carfstone");
        helper.assertFalse(
                CustomBlocks.CHARRED_GRASS
                        .get()
                        .defaultBlockState()
                        .canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                "Normal charred grass should not survive on an ashy patch");
        helper.setBlock(plantPos.below(), CustomBlocks.MOLTEN_CARFSTONE.get());
        helper.assertTrue(
                CustomBlocks.SCORCHED_BERRY_BUSH
                        .get()
                        .defaultBlockState()
                        .canSurvive(helper.getLevel(), helper.absolutePos(plantPos)),
                "Scorched berry bushes should survive on molten carfstone");

        BlockPos bushPos = new BlockPos(3, 2, 2);
        helper.setBlock(bushPos.below(), CustomBlocks.MOLTEN_CARFSTONE.get());
        var matureBush =
                CustomBlocks.SCORCHED_BERRY_BUSH
                        .get()
                        .defaultBlockState()
                        .setValue(SweetBerryBushBlock.AGE, SweetBerryBushBlock.MAX_AGE);
        helper.setBlock(bushPos, matureBush);
        helper.assertTrue(
                matureBush.isRandomlyTicking() == false,
                "A mature scorched berry bush should stop growing");
        BonemealableBlock bonemealableBush = CustomBlocks.SCORCHED_BERRY_BUSH.get();
        helper.assertTrue(
                bonemealableBush.isValidBonemealTarget(
                        helper.getLevel(),
                        helper.absolutePos(bushPos),
                        matureBush.setValue(SweetBerryBushBlock.AGE, 1)),
                "An immature scorched berry bush should accept bone meal");

        var harvestingPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(bushPos, harvestingPlayer);
        helper.assertBlockProperty(bushPos, SweetBerryBushBlock.AGE, 1);
        int harvestedBerries =
                helper.getEntities(EntityType.ITEM, bushPos, 2.0).stream()
                        .filter(entity -> entity.getItem().is(CustomItems.SCORCHED_BERRIES.get()))
                        .mapToInt(entity -> entity.getItem().getCount())
                        .sum();
        helper.assertTrue(
                harvestedBerries >= 2 && harvestedBerries <= 3,
                "A mature scorched berry bush should drop 2-3 berries");

        helper.setBlock(bushPos, matureBush);
        Vec3 bushCenter = Vec3.atCenterOf(helper.absolutePos(bushPos));
        harvestingPlayer.setPos(bushCenter);
        harvestingPlayer.xOld = harvestingPlayer.getX() - 0.1;
        matureBush.entityInside(helper.getLevel(), helper.absolutePos(bushPos), harvestingPlayer);
        helper.assertTrue(
                harvestingPlayer.getRemainingFireTicks() >= 200,
                "A moving living entity should burn for ten seconds in a grown scorched berry"
                        + " bush");

        var bouncedItem = helper.spawnItem(Items.STICK, new BlockPos(4, 3, 2));
        bouncedItem.setDeltaMovement(1.0, -1.0, 1.0);
        CustomBlocks.GLIMMERSHROOM_BLOCK
                .get()
                .updateEntityAfterFallOn(helper.getLevel(), bouncedItem);
        helper.assertTrue(
                Math.abs(bouncedItem.getDeltaMovement().y - 0.8) < 0.0001,
                "Glimmershroom block should bounce non-living entities at 80 percent velocity");
        bouncedItem.setDeltaMovement(1.0, 0.0, 1.0);
        CustomBlocks.GLIMMERSHROOM_BLOCK
                .get()
                .stepOn(
                        helper.getLevel(),
                        helper.absolutePos(new BlockPos(4, 2, 2)),
                        CustomBlocks.GLIMMERSHROOM_BLOCK.get().defaultBlockState(),
                        bouncedItem);
        helper.assertTrue(
                Math.abs(bouncedItem.getDeltaMovement().x - 0.4) < 0.0001,
                "Glimmershroom block should slow horizontal movement");
        bouncedItem.setShiftKeyDown(true);
        bouncedItem.setDeltaMovement(1.0, 0.0, 1.0);
        CustomBlocks.GLIMMERSHROOM_BLOCK
                .get()
                .stepOn(
                        helper.getLevel(),
                        helper.absolutePos(new BlockPos(4, 2, 2)),
                        CustomBlocks.GLIMMERSHROOM_BLOCK.get().defaultBlockState(),
                        bouncedItem);
        helper.assertTrue(
                Math.abs(bouncedItem.getDeltaMovement().x - 1.0) < 0.0001,
                "Careful movement should bypass Glimmershroom horizontal slowdown");

        var fallingPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        float healthBeforeFall = fallingPlayer.getHealth();
        CustomBlocks.GLIMMERSHROOM_BLOCK
                .get()
                .fallOn(
                        helper.getLevel(),
                        CustomBlocks.GLIMMERSHROOM_BLOCK.get().defaultBlockState(),
                        helper.absolutePos(new BlockPos(4, 2, 2)),
                        fallingPlayer,
                        20.0F);
        helper.assertTrue(
                fallingPlayer.getHealth() == healthBeforeFall,
                "Glimmershroom block should cancel fall damage while bouncing");

        BlockPos mushroomPos = new BlockPos(5, 2, 2);
        helper.setBlock(mushroomPos, CustomBlocks.GLIMMERSHROOM.get());
        helper.setBlock(mushroomPos.above(), CustomBlocks.CARFSTONE.get());
        boolean grew =
                CustomBlocks.GLIMMERSHROOM
                        .get()
                        .growMushroom(
                                helper.getLevel(),
                                helper.absolutePos(mushroomPos),
                                CustomBlocks.GLIMMERSHROOM.get().defaultBlockState(),
                                helper.getLevel().random);
        helper.assertFalse(grew, "A blocked Glimmershroom should not grow into a huge mushroom");
        helper.assertBlockPresent(CustomBlocks.GLIMMERSHROOM.get(), mushroomPos);

        var toolUser = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int tick = 0; tick < 20; tick++) {
            toolUser.tick();
        }
        var toolTarget = helper.spawn(EntityType.ZOMBIE, new BlockPos(1, 3, 1));
        ItemStack hellstoneSword = new ItemStack(CustomEquipment.HELLSTONE_SWORD.get());
        toolUser.setItemInHand(InteractionHand.MAIN_HAND, hellstoneSword);
        CustomEquipment.HELLSTONE_SWORD.get().hurtEnemy(hellstoneSword, toolTarget, toolUser);
        helper.assertTrue(
                toolTarget.getRemainingFireTicks() > 0, "Hellstone tools should ignite targets");

        toolTarget.clearFire();
        toolTarget.setDeltaMovement(Vec3.ZERO);
        ItemStack shroomstoneSword = new ItemStack(CustomEquipment.SHROOMSTONE_SWORD.get());
        toolUser.setItemInHand(InteractionHand.MAIN_HAND, shroomstoneSword);
        CustomEquipment.SHROOMSTONE_SWORD.get().hurtEnemy(shroomstoneSword, toolTarget, toolUser);
        helper.assertTrue(
                toolTarget.getDeltaMovement().y > 0, "Shroomstone tools should launch targets");

        toolTarget.setDeltaMovement(Vec3.ZERO);
        ItemStack platinumSword = new ItemStack(CustomEquipment.PLATINUM_SWORD.get());
        toolUser.setItemInHand(InteractionHand.MAIN_HAND, platinumSword);
        CustomEquipment.PLATINUM_SWORD.get().hurtEnemy(platinumSword, toolTarget, toolUser);
        helper.assertTrue(
                toolTarget.getRemainingFireTicks() == 0
                        && toolTarget.getDeltaMovement().equals(Vec3.ZERO),
                "Platinum tools should not apply an additional on-hit ability");

        var shroomPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        shroomPlayer.setItemSlot(
                EquipmentSlot.HEAD, new ItemStack(CustomEquipment.SHROOMSTONE_HELMET.get()));
        shroomPlayer.setSprinting(true);
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(shroomPlayer));
        helper.assertTrue(
                shroomPlayer
                        .getAttribute(Attributes.MOVEMENT_SPEED)
                        .hasModifier(DarkCaverns.id("shroomstone_sprint_speed")),
                "The Shroomstone helmet should grant 10% sprint speed");
        shroomPlayer.setItemSlot(
                EquipmentSlot.CHEST, new ItemStack(CustomEquipment.SHROOMSTONE_CHESTPLATE.get()));
        var knockback = new LivingKnockBackEvent(shroomPlayer, 1.0F, 1.0, 0.0);
        NeoForge.EVENT_BUS.post(knockback);
        helper.assertTrue(
                knockback.getStrength() < 1.0F,
                "The Shroomstone chestplate should reduce knockback");
        var fallDamage =
                new LivingDamageEvent.Pre(
                        shroomPlayer,
                        new DamageContainer(helper.getLevel().damageSources().fall(), 8.0F));
        NeoForge.EVENT_BUS.post(fallDamage);
        helper.assertTrue(
                fallDamage.getNewDamage() == 8.0F,
                "Shroomstone armor without boots should not alter fall damage");
        shroomPlayer.setItemSlot(
                EquipmentSlot.LEGS, new ItemStack(CustomEquipment.SHROOMSTONE_LEGGINGS.get()));
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(shroomPlayer));
        helper.assertTrue(
                shroomPlayer.getAttributeValue(Attributes.JUMP_STRENGTH) > 0.42,
                "The Shroomstone leggings should increase jump strength");
        shroomPlayer.setItemSlot(
                EquipmentSlot.FEET, new ItemStack(CustomEquipment.SHROOMSTONE_BOOTS.get()));
        var negatedFallDamage =
                new LivingDamageEvent.Pre(
                        shroomPlayer,
                        new DamageContainer(helper.getLevel().damageSources().fall(), 8.0F));
        NeoForge.EVENT_BUS.post(negatedFallDamage);
        helper.assertTrue(
                negatedFallDamage.getNewDamage() == 0.0F,
                "The Shroomstone boots should negate fall damage");

        var hellstonePlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        hellstonePlayer.setItemSlot(
                EquipmentSlot.HEAD, new ItemStack(CustomEquipment.HELLSTONE_HELMET.get()));
        hellstonePlayer.setRemainingFireTicks(100);
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(hellstonePlayer));
        helper.assertTrue(
                hellstonePlayer.getRemainingFireTicks() == 99,
                "The Hellstone helmet should remove an extra fire tick after leaving fire");
        hellstonePlayer.setItemSlot(
                EquipmentSlot.CHEST, new ItemStack(CustomEquipment.HELLSTONE_CHESTPLATE.get()));
        hellstonePlayer.setItemSlot(
                EquipmentSlot.LEGS, new ItemStack(CustomEquipment.HELLSTONE_LEGGINGS.get()));
        hellstonePlayer.setItemSlot(
                EquipmentSlot.FEET, new ItemStack(CustomEquipment.HELLSTONE_BOOTS.get()));
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(hellstonePlayer));
        helper.assertFalse(
                hellstonePlayer.hasEffect(MobEffects.FIRE_RESISTANCE),
                "Hellstone protection should not add a redundant potion effect");
        var fireDamage =
                new LivingDamageEvent.Pre(
                        hellstonePlayer,
                        new DamageContainer(helper.getLevel().damageSources().lava(), 8.0F));
        NeoForge.EVENT_BUS.post(fireDamage);
        helper.assertTrue(
                fireDamage.getNewDamage() > 0.0F && fireDamage.getNewDamage() < 8.0F,
                "The Hellstone chestplate should reduce fire damage");
        var hotFloorDamage =
                new LivingIncomingDamageEvent(
                        hellstonePlayer,
                        new DamageContainer(helper.getLevel().damageSources().hotFloor(), 1.0F));
        NeoForge.EVENT_BUS.post(hotFloorDamage);
        helper.assertTrue(
                hotFloorDamage.isCanceled(),
                "The Hellstone boots should prevent damage from hot blocks");

        BlockPos lavaTestPos = new BlockPos(1, 2, 1);
        helper.setBlock(lavaTestPos, Blocks.LAVA.defaultBlockState());
        hellstonePlayer.setPos(Vec3.atCenterOf(helper.absolutePos(lavaTestPos)));
        hellstonePlayer.setDeltaMovement(0.01, 0.01, 0.0);
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(hellstonePlayer));
        helper.assertTrue(
                hellstonePlayer.getDeltaMovement().x > 0.01,
                "Hellstone leggings should actively boost horizontal movement in lava");
        helper.assertTrue(
                hellstonePlayer.getDeltaMovement().y >= 0.15,
                "Hellstone leggings should boost upward swimming in lava");
        hellstonePlayer.setDeltaMovement(0.0, 0.0, 0.0);
        hellstonePlayer.setShiftKeyDown(true);
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(hellstonePlayer));
        helper.assertTrue(
                hellstonePlayer.getDeltaMovement().y <= -0.14,
                "Hellstone leggings should allow fast diving when sneaking in lava");
        hellstonePlayer.setShiftKeyDown(false);

        helper.succeed();
    }
}
