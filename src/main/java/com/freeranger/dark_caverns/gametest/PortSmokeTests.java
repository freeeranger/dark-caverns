package com.freeranger.dark_caverns.gametest;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.blocks.GatewayToTheCavernsBlock;
import com.freeranger.dark_caverns.blocks.GatewayToTheOverworldBlock;
import com.freeranger.dark_caverns.core.ExplorationTrades;
import com.freeranger.dark_caverns.core.GatewayCooldowns;
import com.freeranger.dark_caverns.entities.CamorockEntity;
import com.freeranger.dark_caverns.entities.LuminiteFoxEntity;
import com.freeranger.dark_caverns.entities.LuminiteGolemEntity;
import com.freeranger.dark_caverns.entities.MoltenerEntity;
import com.freeranger.dark_caverns.entities.ScorchhoundEntity;
import com.freeranger.dark_caverns.entities.ScorchlingEntity;
import com.freeranger.dark_caverns.entities.ShroomieEntity;
import com.freeranger.dark_caverns.entities.ShroomlingEntity;
import com.freeranger.dark_caverns.events.CorruptedPearlTeleportEvent;
import com.freeranger.dark_caverns.registry.CustomArmorMaterials;
import com.freeranger.dark_caverns.registry.CustomAttachments;
import com.freeranger.dark_caverns.registry.CustomBlockTags;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEntityTypes;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import com.freeranger.dark_caverns.registry.CustomItems;
import com.freeranger.dark_caverns.registry.CustomSpawnEggs;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

@GameTestHolder(DarkCaverns.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PortSmokeTests {
    private PortSmokeTests() {}

    @GameTest(template = "sacred_torch")
    public static void registriesLoad(GameTestHelper helper) {
        helper.assertTrue(
                countModEntries(BuiltInRegistries.BLOCK) == 49, "Expected all 49 block IDs");
        helper.assertTrue(
                countModEntries(BuiltInRegistries.ITEM) == 102,
                "Expected 46 block items plus 56 standalone item IDs");
        helper.assertTrue(
                countModEntries(BuiltInRegistries.SOUND_EVENT) == 27, "Expected all 27 sound IDs");
        helper.assertTrue(
                countModEntries(BuiltInRegistries.ENTITY_TYPE) == 11,
                "Expected eight mobs plus three projectile entity IDs");
        helper.assertTrue(
                countModEntries(NeoForgeRegistries.ATTACHMENT_TYPES) == 2,
                "Expected gateway cooldown and Scorchsteel state attachment IDs");
        verifyRegistryCount(helper, Registries.BIOME, 3, "biome definitions");
        verifyRegistryCount(helper, Registries.CONFIGURED_CARVER, 1, "configured carver");
        verifyRegistryCount(helper, Registries.CONFIGURED_FEATURE, 22, "configured features");
        verifyRegistryCount(helper, Registries.PLACED_FEATURE, 21, "placed features");
        verifyRegistryCount(helper, Registries.NOISE, 4, "noise definitions");
        verifyRegistryCount(helper, Registries.NOISE_SETTINGS, 1, "noise settings");
        verifyRegistryCount(helper, Registries.STRUCTURE, 4, "structure definitions");
        verifyRegistryCount(helper, Registries.STRUCTURE_SET, 4, "structure sets");
        verifyRegistryCount(helper, Registries.TEMPLATE_POOL, 4, "structure template pools");

        var configuredFeatures =
                helper.getLevel().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        var spike = configuredFeatures.get(DarkCaverns.id("spike_feature"));
        var moltenSpike = configuredFeatures.get(DarkCaverns.id("molten_spike_feature"));
        helper.assertTrue(
                spike != null
                        && spike.config() instanceof BlockStateConfiguration configuration
                        && configuration.state.is(CustomBlocks.CARFSTONE.get()),
                "Carfstone spike material did not load from configured-feature data");
        helper.assertTrue(
                moltenSpike != null
                        && moltenSpike.config() instanceof BlockStateConfiguration configuration
                        && configuration.state.is(CustomBlocks.MOLTEN_CARFSTONE.get()),
                "Molten spike material did not load from configured-feature data");
        long darkCavernsAdvancements =
                helper.getLevel().getServer().getAdvancements().getAllAdvancements().stream()
                        .filter(
                                advancement ->
                                        advancement.id().getNamespace().equals(DarkCaverns.MOD_ID))
                        .count();
        helper.assertTrue(
                darkCavernsAdvancements == 162,
                "Expected 20 progression advancements and 142 recipe unlock advancements");
        helper.assertTrue(
                helper.getLevel()
                                .getServer()
                                .getAdvancements()
                                .get(DarkCaverns.id("recipes/platinum_sword"))
                        != null,
                "Platinum sword recipe unlock advancement did not load");

        helper.assertTrue(
                CustomEntityTypes.SCORCHLING_ENTITY.get().create(helper.getLevel()) != null,
                "Scorchling factory failed");
        helper.assertTrue(
                CustomEntityTypes.SCORCHHOUND_ENTITY.get().create(helper.getLevel()) != null,
                "Scorchhound factory failed");
        helper.assertTrue(
                CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get().create(helper.getLevel()) != null,
                "Luminite golem factory failed");
        helper.assertTrue(
                CustomEntityTypes.MOLTENER_ENTITY.get().create(helper.getLevel()) != null,
                "Moltener factory failed");
        helper.assertTrue(
                CustomEntityTypes.CAMOROCK_ENTITY.get().create(helper.getLevel()) != null,
                "Camorock factory failed");
        helper.assertTrue(
                CustomEntityTypes.LUMINITE_FOX_ENTITY.get().create(helper.getLevel()) != null,
                "Luminite fox factory failed");
        ShroomieEntity shroomie = CustomEntityTypes.SHROOMIE_ENTITY.get().create(helper.getLevel());
        helper.assertTrue(shroomie != null, "Shroomie factory failed");
        var shroomieOffers = shroomie.getOffers();
        helper.assertTrue(
                shroomieOffers.size() == 7,
                "Shroomie should generate five common, one progression, and one rare offer");
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
        helper.assertTrue(
                shroomstoneOffer.getResult().getCount() == 2
                        && shroomstoneOffer.getBaseCostA().is(Items.DIAMOND)
                        && shroomstoneOffer.getBaseCostA().getCount() == 1,
                "Shroomstone trade should exchange one diamond for two pieces");
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
        ShroomlingEntity shroomling =
                CustomEntityTypes.SHROOMLING_ENTITY.get().create(helper.getLevel());
        helper.assertTrue(shroomling != null, "Shroomling factory failed");
        UUID angerTarget = UUID.randomUUID();
        shroomling.setRemainingPersistentAngerTime(200);
        shroomling.setPersistentAngerTarget(angerTarget);
        CompoundTag shroomlingData = new CompoundTag();
        shroomling.addAdditionalSaveData(shroomlingData);
        ShroomlingEntity restoredShroomling =
                CustomEntityTypes.SHROOMLING_ENTITY.get().create(helper.getLevel());
        helper.assertTrue(restoredShroomling != null, "Second Shroomling factory failed");
        restoredShroomling.readAdditionalSaveData(shroomlingData);
        helper.assertTrue(
                restoredShroomling.getRemainingPersistentAngerTime() == 200
                        && angerTarget.equals(restoredShroomling.getPersistentAngerTarget()),
                "Shroomling persistent anger state did not survive serialization");
        helper.assertTrue(
                CustomEntityTypes.THROWABLE_LUMINITE_TORCH.get().create(helper.getLevel()) != null,
                "Torch projectile factory failed");
        helper.assertTrue(
                CustomEntityTypes.SHROOMBOMB.get().create(helper.getLevel()) != null,
                "Shroombomb factory failed");
        helper.assertTrue(
                CustomEntityTypes.CORRUPTED_PEARL.get().create(helper.getLevel()) != null,
                "Corrupted pearl factory failed");

        helper.assertTrue(
                CustomSpawnEggs.SCORCHHOUND_SPAWN_EGG
                                .get()
                                .getType(new ItemStack(CustomSpawnEggs.SCORCHHOUND_SPAWN_EGG.get()))
                        == CustomEntityTypes.SCORCHHOUND_ENTITY.get(),
                "Scorchhound egg resolves to the wrong entity type");
        helper.assertTrue(
                CustomSpawnEggs.SHROOMIE_SPAWN_EGG
                                .get()
                                .getType(new ItemStack(CustomSpawnEggs.SHROOMIE_SPAWN_EGG.get()))
                        == CustomEntityTypes.SHROOMIE_ENTITY.get(),
                "Shroomie egg resolves to the wrong entity type");
        helper.assertTrue(
                CustomItems.SCORCHLING_TAIL
                                .get()
                                .getBurnTime(new ItemStack(CustomItems.SCORCHLING_TAIL.get()), null)
                        == 1600,
                "Scorchling tail furnace fuel value did not load");
        helper.assertTrue(
                BuiltInRegistries.ITEM
                                .wrapAsHolder(CustomBlocks.GLIMMERSHROOM.get().asItem())
                                .getData(NeoForgeDataMaps.COMPOSTABLES)
                                .chance()
                        == 0.65F,
                "Glimmershroom compost chance did not load");

        helper.assertTrue(
                CustomBlocks.GATEWAY_TO_THE_CAVERNS.get() instanceof GatewayToTheCavernsBlock,
                "Overworld gateway block did not load its teleport behavior");
        helper.assertTrue(
                CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get() instanceof GatewayToTheOverworldBlock,
                "Dark Caverns gateway block did not load its teleport behavior");

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

        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void blockAndEquipmentBehavior(GameTestHelper helper) {
        verifyCompostChance(helper, CustomBlocks.GLIMMERSHROOM.get().asItem(), 0.65F);
        verifyCompostChance(helper, CustomBlocks.GLIMMERSHROOM_BLOCK.get().asItem(), 0.85F);
        verifyCompostChance(helper, CustomBlocks.GLIMMERGRASS.get().asItem(), 0.30F);
        verifyCompostChance(helper, CustomBlocks.CHARRED_GRASS.get().asItem(), 0.30F);
        verifyCompostChance(helper, CustomItems.SCORCHED_BERRIES.get(), 0.30F);

        FoodProperties berryFood =
                new ItemStack(CustomItems.SCORCHED_BERRIES.get()).get(DataComponents.FOOD);
        helper.assertTrue(
                berryFood != null
                        && berryFood.nutrition() == 2
                        && berryFood.canAlwaysEat()
                        && berryFood.eatDurationTicks() == 16
                        && berryFood.effects().size() == 1
                        && berryFood
                                .effects()
                                .getFirst()
                                .effect()
                                .getEffect()
                                .equals(MobEffects.FIRE_RESISTANCE)
                        && berryFood.effects().getFirst().effect().getDuration() == 100,
                "Scorched berries should be a short emergency fire-resistance food");
        FoodProperties scorchedMeatFood =
                new ItemStack(CustomItems.SCORCHED_MEAT.get()).get(DataComponents.FOOD);
        helper.assertTrue(
                scorchedMeatFood != null
                        && scorchedMeatFood.nutrition() == 8
                        && !scorchedMeatFood.canAlwaysEat()
                        && scorchedMeatFood.effects().isEmpty(),
                "Scorched meat should be strong food without a hidden saturation effect");
        helper.assertTrue(
                CustomBlocks.CARFSTONE_COAL_ORE.get() instanceof DropExperienceBlock
                        && CustomBlocks.CARFSTONE_DIAMOND_ORE.get() instanceof DropExperienceBlock
                        && CustomBlocks.CARFSTONE_REDSTONE_ORE.get() instanceof DropExperienceBlock
                        && CustomBlocks.CARFSTONE_LAPIS_ORE.get() instanceof DropExperienceBlock
                        && CustomBlocks.LUMINITE_ORE.get() instanceof DropExperienceBlock
                        && CustomBlocks.HELLSTONE_ORE.get() instanceof DropExperienceBlock,
                "Gem, dust, redstone, lapis, and coal ores should award mining experience");

        BlockPos samplePos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.assertTrue(
                CustomBlocks.LUMINITE_TORCH
                                .get()
                                .defaultBlockState()
                                .getLightEmission(helper.getLevel(), samplePos)
                        == 15,
                "Luminite torch should emit light level 15");
        helper.assertTrue(
                CustomBlocks.LUMINITE_WALL_TORCH
                                .get()
                                .defaultBlockState()
                                .getLightEmission(helper.getLevel(), samplePos)
                        == 15,
                "Luminite wall torch should emit light level 15");
        helper.assertTrue(
                CustomBlocks.LUMINITE_LANTERN
                                .get()
                                .defaultBlockState()
                                .getLightEmission(helper.getLevel(), samplePos)
                        == 15,
                "Luminite lantern should emit light level 15");
        helper.assertTrue(
                CustomBlocks.CARFSTONE
                        .get()
                        .defaultBlockState()
                        .is(BlockTags.MINEABLE_WITH_PICKAXE),
                "Carfstone should be mineable with a pickaxe");
        helper.assertTrue(
                CustomBlocks.CARFSTONE_IRON_ORE
                        .get()
                        .defaultBlockState()
                        .is(BlockTags.NEEDS_STONE_TOOL),
                "Carfstone iron ore should require a stone-tier tool");
        helper.assertTrue(
                CustomBlocks.LUMINITE_ORE.get().defaultBlockState().is(BlockTags.NEEDS_IRON_TOOL),
                "Luminite ore should require an iron-tier tool");
        helper.assertTrue(
                CustomBlocks.PLATINUM_ORE
                        .get()
                        .defaultBlockState()
                        .is(BlockTags.NEEDS_DIAMOND_TOOL),
                "Platinum ore should require a diamond-tier tool");
        helper.assertTrue(
                CustomBlocks.GLIMMERSHROOM_BLOCK
                        .get()
                        .defaultBlockState()
                        .is(BlockTags.MINEABLE_WITH_AXE),
                "Glimmershroom blocks should retain their axe mining behavior");
        helper.assertTrue(
                CustomBlocks.CARFSTONE
                                .get()
                                .defaultBlockState()
                                .is(CustomBlockTags.ROCKY_CREATURE_SPAWNABLE_ON)
                        && CustomBlocks.MOLTEN_CARFSTONE
                                .get()
                                .defaultBlockState()
                                .is(CustomBlockTags.MOLTEN_CREATURE_SPAWNABLE_ON)
                        && CustomBlocks.GLIMMERGRASS_BLOCK
                                .get()
                                .defaultBlockState()
                                .is(CustomBlockTags.GLIMMERSHROOM_CREATURE_SPAWNABLE_ON),
                "Creature spawn-ground tags did not load");

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

        verifyArmorMaterial(
                helper,
                CustomArmorMaterials.LUMINITE.get(),
                "luminite",
                2,
                6,
                5,
                2,
                15,
                0.0F,
                CustomItems.LUMINITE_DUST.get());
        verifyArmorMaterial(
                helper,
                CustomArmorMaterials.PLATINUM.get(),
                "platinum",
                3,
                8,
                6,
                3,
                20,
                2.5F,
                CustomItems.PLATINUM_INGOT.get());
        verifyArmorMaterial(
                helper,
                CustomArmorMaterials.HELLSTONE.get(),
                "hellstone",
                3,
                8,
                6,
                3,
                20,
                2.5F,
                CustomItems.HELLSTONE.get());
        verifyArmorMaterial(
                helper,
                CustomArmorMaterials.SHROOMSTONE.get(),
                "shroomstone",
                3,
                8,
                6,
                3,
                20,
                2.5F,
                CustomItems.SHROOMSTONE.get());
        verifyArmorMaterial(
                helper,
                CustomArmorMaterials.SCORCHSTEEL.get(),
                "scorchsteel",
                3,
                8,
                6,
                3,
                20,
                2.5F,
                CustomItems.SCORCHSTEEL_INGOT.get());

        helper.assertTrue(
                new ItemStack(CustomEquipment.LUMINITE_HELMET.get()).getMaxDamage() == 165,
                "Luminite helmet durability multiplier changed");
        helper.assertTrue(
                new ItemStack(CustomEquipment.PLATINUM_HELMET.get()).getMaxDamage() == 396,
                "Platinum helmet durability multiplier changed");
        helper.assertTrue(
                new ItemStack(CustomEquipment.PLATINUM_CHESTPLATE.get()).getMaxDamage() == 576,
                "Platinum chestplate durability multiplier changed");
        helper.assertTrue(
                new ItemStack(CustomEquipment.PLATINUM_LEGGINGS.get()).getMaxDamage() == 540,
                "Platinum leggings durability multiplier changed");
        helper.assertTrue(
                new ItemStack(CustomEquipment.PLATINUM_BOOTS.get()).getMaxDamage() == 468,
                "Platinum boots durability multiplier changed");

        var toolUser = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int tick = 0; tick < 20; tick++) {
            toolUser.tick();
        }
        var toolTarget = helper.spawn(EntityType.ZOMBIE, new BlockPos(1, 3, 1));
        ItemStack hellstoneSword = new ItemStack(CustomEquipment.HELLSTONE_SWORD.get());
        toolUser.setItemInHand(InteractionHand.MAIN_HAND, hellstoneSword);
        CustomEquipment.HELLSTONE_SWORD.get().hurtEnemy(hellstoneSword, toolTarget, toolUser);
        helper.assertTrue(
                toolTarget.getRemainingFireTicks() >= 160,
                "Hellstone tools should ignite targets for at least eight seconds");

        toolTarget.clearFire();
        toolTarget.setDeltaMovement(Vec3.ZERO);
        ItemStack shroomstoneSword = new ItemStack(CustomEquipment.SHROOMSTONE_SWORD.get());
        toolUser.setItemInHand(InteractionHand.MAIN_HAND, shroomstoneSword);
        CustomEquipment.SHROOMSTONE_SWORD.get().hurtEnemy(shroomstoneSword, toolTarget, toolUser);
        helper.assertTrue(
                Math.abs(toolTarget.getDeltaMovement().y - 0.6) < 0.0001,
                "Shroomstone tools should launch targets upward");

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
        shroomPlayer.setItemSlot(
                EquipmentSlot.CHEST, new ItemStack(CustomEquipment.SHROOMSTONE_CHESTPLATE.get()));
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(shroomPlayer));
        helper.assertFalse(
                shroomPlayer.hasEffect(MobEffects.JUMP),
                "A partial Shroomstone set should not grant the full-set jump bonus");
        var fallDamage =
                new LivingDamageEvent.Pre(
                        shroomPlayer,
                        new DamageContainer(helper.getLevel().damageSources().fall(), 8.0F));
        NeoForge.EVENT_BUS.post(fallDamage);
        helper.assertTrue(
                Math.abs(fallDamage.getNewDamage() - 4.0F) < 0.0001F,
                "Two Shroomstone pieces should halve fall damage");
        shroomPlayer.setItemSlot(
                EquipmentSlot.LEGS, new ItemStack(CustomEquipment.SHROOMSTONE_LEGGINGS.get()));
        shroomPlayer.setItemSlot(
                EquipmentSlot.FEET, new ItemStack(CustomEquipment.SHROOMSTONE_BOOTS.get()));
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(shroomPlayer));
        helper.assertTrue(
                shroomPlayer.hasEffect(MobEffects.JUMP)
                        && shroomPlayer.getEffect(MobEffects.JUMP).getAmplifier() == 1,
                "A full Shroomstone set should grant Jump Boost II");

        var hellstonePlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        hellstonePlayer.setItemSlot(
                EquipmentSlot.HEAD, new ItemStack(CustomEquipment.HELLSTONE_HELMET.get()));
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
                fireDamage.getNewDamage() == 0.0F,
                "A full Hellstone set should negate fire damage");

        var scorchsteelPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        scorchsteelPlayer.setItemSlot(
                EquipmentSlot.HEAD, new ItemStack(CustomEquipment.SCORCHSTEEL_HELMET.get()));
        for (int tick = 0; tick <= 20; tick++) {
            NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(scorchsteelPlayer));
        }
        helper.assertFalse(
                scorchsteelPlayer.hasEffect(MobEffects.INVISIBILITY),
                "A partial Scorchsteel set should not grant concealment");
        scorchsteelPlayer.setItemSlot(
                EquipmentSlot.CHEST, new ItemStack(CustomEquipment.SCORCHSTEEL_CHESTPLATE.get()));
        scorchsteelPlayer.setItemSlot(
                EquipmentSlot.LEGS, new ItemStack(CustomEquipment.SCORCHSTEEL_LEGGINGS.get()));
        scorchsteelPlayer.setItemSlot(
                EquipmentSlot.FEET, new ItemStack(CustomEquipment.SCORCHSTEEL_BOOTS.get()));
        for (int tick = 0; tick <= 20; tick++) {
            NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(scorchsteelPlayer));
        }
        helper.assertTrue(
                scorchsteelPlayer.hasEffect(MobEffects.INVISIBILITY),
                "A stationary full Scorchsteel set should grant concealment after the configured"
                        + " delay");
        helper.assertTrue(
                scorchsteelPlayer.hasData(CustomAttachments.SCORCHSTEEL_STEALTH),
                "Scorchsteel standstill tracking should use its registered data attachment");
        var zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(6, 2, 2));
        zombie.setTarget(scorchsteelPlayer);
        NeoForge.EVENT_BUS.post(new EntityTickEvent.Post(zombie));
        helper.assertTrue(
                zombie.getTarget() == null,
                "Monsters should clear invisible Scorchsteel-wearing targets");
        NeoForge.EVENT_BUS.post(new AttackEntityEvent(scorchsteelPlayer, zombie));
        helper.assertFalse(
                scorchsteelPlayer.hasEffect(MobEffects.INVISIBILITY),
                "Attacking should immediately break Scorchsteel concealment");
        scorchsteelPlayer.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(scorchsteelPlayer));
        helper.assertFalse(
                scorchsteelPlayer.hasData(CustomAttachments.SCORCHSTEEL_STEALTH),
                "Scorchsteel standstill state should be removed when no pieces are equipped");

        helper.succeed();
    }

    @GameTest(template = "sacred_torch")
    public static void entityCombatBehavior(GameTestHelper helper) {
        CamorockEntity camorock = CustomEntityTypes.CAMOROCK_ENTITY.get().create(helper.getLevel());
        MoltenerEntity moltener = CustomEntityTypes.MOLTENER_ENTITY.get().create(helper.getLevel());
        LuminiteFoxEntity fox =
                CustomEntityTypes.LUMINITE_FOX_ENTITY.get().create(helper.getLevel());
        ShroomlingEntity shroomling =
                CustomEntityTypes.SHROOMLING_ENTITY.get().create(helper.getLevel());
        ShroomieEntity shroomie = CustomEntityTypes.SHROOMIE_ENTITY.get().create(helper.getLevel());
        ScorchlingEntity scorchling =
                CustomEntityTypes.SCORCHLING_ENTITY.get().create(helper.getLevel());
        ScorchhoundEntity scorchhound =
                CustomEntityTypes.SCORCHHOUND_ENTITY.get().create(helper.getLevel());
        LuminiteGolemEntity golem =
                CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get().create(helper.getLevel());

        verifyMob(
                helper,
                camorock,
                CustomEntityTypes.CAMOROCK_ENTITY.get(),
                0.9F,
                0.6F,
                10.0,
                0.15,
                24.0,
                false);
        verifyMob(
                helper,
                moltener,
                CustomEntityTypes.MOLTENER_ENTITY.get(),
                0.7F,
                0.9F,
                10.0,
                0.15,
                24.0,
                true);
        verifyMob(
                helper,
                fox,
                CustomEntityTypes.LUMINITE_FOX_ENTITY.get(),
                0.7F,
                0.4F,
                8.0,
                0.25,
                24.0,
                false);
        verifyMob(
                helper,
                shroomling,
                CustomEntityTypes.SHROOMLING_ENTITY.get(),
                1.5F,
                0.6F,
                18.0,
                0.4,
                24.0,
                false);
        verifyMob(
                helper,
                shroomie,
                CustomEntityTypes.SHROOMIE_ENTITY.get(),
                0.5F,
                1.2F,
                15.0,
                0.25,
                24.0,
                false);
        verifyMob(
                helper,
                scorchling,
                CustomEntityTypes.SCORCHLING_ENTITY.get(),
                0.6F,
                0.4F,
                15.0,
                0.24,
                24.0,
                true);
        verifyMob(
                helper,
                scorchhound,
                CustomEntityTypes.SCORCHHOUND_ENTITY.get(),
                1.5F,
                1.0F,
                40.0,
                0.23,
                32.0,
                true);
        verifyMob(
                helper,
                golem,
                CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get(),
                1.3F,
                2.0F,
                40.0,
                0.15,
                16.0,
                false);

        helper.assertTrue(
                shroomling.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) == 6.0,
                "Shroomling attack damage changed");
        helper.assertTrue(
                shroomling.getAttributeBaseValue(Attributes.ATTACK_SPEED) == 1.4,
                "Shroomling attack speed changed");
        helper.assertTrue(
                shroomling.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) == 1.0,
                "Shroomling attack knockback changed");
        helper.assertTrue(
                scorchling.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) == 4.0,
                "Scorchling attack damage changed");
        helper.assertTrue(
                scorchling.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) == 1.7,
                "Scorchling attack knockback changed");
        helper.assertTrue(
                scorchling.getAttributeBaseValue(Attributes.ARMOR) == 4.0,
                "Scorchling armor changed");
        helper.assertTrue(
                scorchhound.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) == 6.0,
                "Scorchhound attack damage changed");
        helper.assertTrue(
                scorchhound.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) == 0.6,
                "Scorchhound attack knockback changed");
        helper.assertTrue(
                scorchhound.getAttributeBaseValue(Attributes.ARMOR) == 6.0,
                "Scorchhound armor changed");
        helper.assertTrue(
                scorchhound.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) == 0.75,
                "Scorchhound knockback resistance should remain strong but valid");
        helper.assertTrue(
                golem.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) == 14.0,
                "Luminite golem should use its rebalanced attack damage");
        helper.assertTrue(
                golem.getAttributeBaseValue(Attributes.ATTACK_KNOCKBACK) == 1.0,
                "Luminite golem attack knockback changed");
        helper.assertTrue(
                golem.getAttributeBaseValue(Attributes.ARMOR) == 10.0,
                "Luminite golem armor changed");
        helper.assertTrue(
                golem.getAttributeBaseValue(Attributes.KNOCKBACK_RESISTANCE) == 0.9,
                "Luminite golem knockback resistance should remain strong but valid");

        ScorchhoundEntity attackingHound =
                helper.spawn(CustomEntityTypes.SCORCHHOUND_ENTITY.get(), new BlockPos(2, 2, 2));
        var flingTarget = helper.makeMockPlayer(GameType.SURVIVAL);
        flingTarget.setPos(Vec3.atCenterOf(helper.absolutePos(new BlockPos(4, 2, 2))));
        float healthBeforeFling = flingTarget.getHealth();
        helper.assertTrue(
                attackingHound.doHurtTarget(flingTarget),
                "Scorchhound attack should damage its target");
        helper.assertTrue(
                flingTarget.getHealth() < healthBeforeFling, "Scorchhound attack dealt no damage");
        helper.assertTrue(
                flingTarget.getDeltaMovement().lengthSqr() > 0.0,
                "Scorchhound attack should use Hoglin-style fling motion");

        LuminiteGolemEntity attackingGolem =
                helper.spawn(CustomEntityTypes.LUMINITE_GOLEM_ENTITY.get(), new BlockPos(2, 2, 4));
        var golemTarget = helper.makeMockPlayer(GameType.SURVIVAL);
        golemTarget.setPos(Vec3.atCenterOf(helper.absolutePos(new BlockPos(4, 2, 4))));
        golemTarget.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0);
        golemTarget.setHealth(100.0F);
        helper.assertTrue(
                attackingGolem.doHurtTarget(golemTarget),
                "Luminite golem should begin a telegraphed attack");
        helper.assertTrue(
                golemTarget.getHealth() == 100.0F,
                "Luminite golem damage should wait for the animation's impact frame");
        helper.assertTrue(
                attackingGolem.attackAnimationTick() == 14,
                "Luminite golem attack animation was not synchronized");
        for (int tick = 0; tick < 7; tick++) {
            attackingGolem.aiStep();
        }
        float golemDamage = 100.0F - golemTarget.getHealth();
        helper.assertTrue(
                golemDamage >= 10.5F && golemDamage <= 17.5F,
                "Luminite golem attack should use its rebalanced damage range at impact");
        helper.assertTrue(
                golemTarget.getDeltaMovement().y >= 0.5,
                "Luminite golem attack should launch its target upward");
        helper.succeed();
    }

    private static void verifyCompostChance(
            GameTestHelper helper, net.minecraft.world.item.Item item, float expected) {
        var compostable =
                BuiltInRegistries.ITEM.wrapAsHolder(item).getData(NeoForgeDataMaps.COMPOSTABLES);
        helper.assertTrue(
                compostable != null && compostable.chance() == expected,
                "Incorrect compost chance for " + item);
    }

    private static long countModEntries(Registry<?> registry) {
        return registry.keySet().stream()
                .filter(id -> id.getNamespace().equals(DarkCaverns.MOD_ID))
                .count();
    }

    private static <T> void verifyRegistryCount(
            GameTestHelper helper,
            ResourceKey<? extends Registry<? extends T>> registryKey,
            long expected,
            String description) {
        long actual =
                countModEntries(helper.getLevel().registryAccess().registryOrThrow(registryKey));
        helper.assertTrue(
                actual == expected,
                "Expected " + expected + " Dark Caverns " + description + ", found " + actual);
    }

    private static void verifyMob(
            GameTestHelper helper,
            Mob mob,
            EntityType<?> type,
            float width,
            float height,
            double health,
            double speed,
            double followRange,
            boolean fireImmune) {
        helper.assertTrue(mob != null, "Entity factory returned null for " + type);
        helper.assertTrue(
                Math.abs(type.getDimensions().width() - width) < 0.0001F,
                "Entity width changed for " + type);
        helper.assertTrue(
                Math.abs(type.getDimensions().height() - height) < 0.0001F,
                "Entity height changed for " + type);
        helper.assertTrue(
                mob.getAttributeBaseValue(Attributes.MAX_HEALTH) == health,
                "Maximum health changed for " + type);
        helper.assertTrue(
                mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) == speed,
                "Movement speed changed for " + type);
        helper.assertTrue(
                mob.getAttributeBaseValue(Attributes.FOLLOW_RANGE) == followRange,
                "Follow range changed for " + type);
        helper.assertTrue(type.fireImmune() == fireImmune, "Fire immunity changed for " + type);
    }

    private static void verifyArmorMaterial(
            GameTestHelper helper,
            ArmorMaterial material,
            String textureName,
            int helmet,
            int chestplate,
            int leggings,
            int boots,
            int enchantmentValue,
            float toughness,
            net.minecraft.world.item.Item repairItem) {
        helper.assertTrue(
                material.getDefense(ArmorItem.Type.HELMET) == helmet,
                textureName + " helmet defense changed");
        helper.assertTrue(
                material.getDefense(ArmorItem.Type.CHESTPLATE) == chestplate,
                textureName + " chest defense changed");
        helper.assertTrue(
                material.getDefense(ArmorItem.Type.LEGGINGS) == leggings,
                textureName + " leg defense changed");
        helper.assertTrue(
                material.getDefense(ArmorItem.Type.BOOTS) == boots,
                textureName + " boot defense changed");
        helper.assertTrue(
                material.enchantmentValue() == enchantmentValue,
                textureName + " enchantability changed");
        helper.assertTrue(material.toughness() == toughness, textureName + " toughness changed");
        helper.assertTrue(
                material.repairIngredient().get().test(new ItemStack(repairItem)),
                textureName + " repair ingredient changed");
        helper.assertTrue(
                material.layers()
                        .getFirst()
                        .texture(false)
                        .equals(
                                DarkCaverns.id(
                                        "textures/models/armor/" + textureName + "_layer_1.png")),
                textureName + " outer armor texture does not resolve to the existing asset");
        helper.assertTrue(
                material.layers()
                        .getFirst()
                        .texture(true)
                        .equals(
                                DarkCaverns.id(
                                        "textures/models/armor/" + textureName + "_layer_2.png")),
                textureName + " inner armor texture does not resolve to the existing asset");
    }
}
