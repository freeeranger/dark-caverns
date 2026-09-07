package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.entities.ShroombombEntity;
import com.freeranger.dark_caverns.entities.ThrowableLuminiteTorchEntity;
import com.freeranger.dark_caverns.items.KeyToTheCavernsItem;
import com.freeranger.dark_caverns.items.ThrowableItem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;

public final class CustomItems {
    private static final FoodProperties SCORCHED_BERRIES_FOOD =
            new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.2F)
                    .alwaysEdible()
                    .fast()
                    .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 320), 1.0F)
                    .build();
    private static final FoodProperties SCORCHED_MEAT_FOOD =
            new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationModifier(0.7F)
                    .alwaysEdible()
                    .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7), 1.0F)
                    .build();

    public static final DeferredItem<BlockItem> SCORCHED_BERRIES =
            ModRegistries.ITEMS.register(
                    "scorched_berries",
                    () ->
                            new BlockItem(
                                    CustomBlocks.SCORCHED_BERRY_BUSH.get(),
                                    new Item.Properties().food(SCORCHED_BERRIES_FOOD)));
    public static final DeferredItem<KeyToTheCavernsItem> KEY_TO_THE_CAVERNS =
            ModRegistries.ITEMS.register(
                    "key_to_the_caverns",
                    () -> new KeyToTheCavernsItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SCORCHLING_TAIL = item("scorchling_tail");
    public static final DeferredItem<Item> SCORCHED_MEAT =
            item("scorched_meat", new Item.Properties().food(SCORCHED_MEAT_FOOD));

    public static final DeferredItem<DeferredSpawnEggItem> SCORCHHOUND_SPAWN_EGG =
            spawnEgg(
                    "scorchhound_spawn_egg",
                    CustomEntityTypes.SCORCHHOUND_ENTITY,
                    5195333,
                    16737280);
    public static final DeferredItem<DeferredSpawnEggItem> MOLTENER_SPAWN_EGG =
            spawnEgg("moltener_spawn_egg", CustomEntityTypes.MOLTENER_ENTITY, 5195333, 4018742);
    public static final DeferredItem<DeferredSpawnEggItem> CAMOROCK_SPAWN_EGG =
            spawnEgg("camorock_spawn_egg", CustomEntityTypes.CAMOROCK_ENTITY, 4342338, 15263976);
    public static final DeferredItem<DeferredSpawnEggItem> SHROOMIE_SPAWN_EGG =
            spawnEgg("shroomie_spawn_egg", CustomEntityTypes.SHROOMIE_ENTITY, 6663388, 16777215);
    public static final DeferredItem<DeferredSpawnEggItem> SHROOMLING_SPAWN_EGG =
            spawnEgg(
                    "shroomling_spawn_egg", CustomEntityTypes.SHROOMLING_ENTITY, 6663388, 16777215);
    public static final DeferredItem<DeferredSpawnEggItem> LUMINITE_GOLEM_SPAWN_EGG =
            spawnEgg(
                    "luminite_golem_spawn_egg",
                    CustomEntityTypes.LUMINITE_GOLEM_ENTITY,
                    4935256,
                    3276702);
    public static final DeferredItem<DeferredSpawnEggItem> LUMINITE_FOX_SPAWN_EGG =
            spawnEgg(
                    "luminite_fox_spawn_egg",
                    CustomEntityTypes.LUMINITE_FOX_ENTITY,
                    4935256,
                    3276702);
    public static final DeferredItem<DeferredSpawnEggItem> SCORCHLING_SPAWN_EGG =
            spawnEgg(
                    "scorchling_spawn_egg", CustomEntityTypes.SCORCHLING_ENTITY, 5195333, 16751880);

    public static final DeferredItem<Item> LUMINITE_DUST = item("luminite_dust");
    public static final DeferredItem<Item> HELLSTONE = fireResistantItem("hellstone");
    public static final DeferredItem<Item> HELLSTONE_ROCK = fireResistantItem("hellstone_rock");
    public static final DeferredItem<Item> PLATINUM_PIECE = item("platinum_piece");
    public static final DeferredItem<Item> SHROOMSTONE = item("shroomstone");
    public static final DeferredItem<Item> SHROOMSTONE_PIECE = item("shroomstone_piece");
    public static final DeferredItem<Item> SCORCHSTEEL_INGOT = item("scorchsteel_ingot");
    public static final DeferredItem<Item> PLATINUM_INGOT = item("platinum_ingot");

    public static final DeferredItem<ThrowableItem> THROWABLE_LUMINITE_TORCH =
            ModRegistries.ITEMS.register(
                    "throwable_luminite_torch",
                    () ->
                            new ThrowableItem(
                                    new Item.Properties(),
                                    SoundEvents.SNOWBALL_THROW,
                                    0,
                                    ThrowableLuminiteTorchEntity::new));
    public static final DeferredItem<ThrowableItem> SHROOMBOMB =
            ModRegistries.ITEMS.register(
                    "shroombomb",
                    () ->
                            new ThrowableItem(
                                    new Item.Properties(),
                                    SoundEvents.EGG_THROW,
                                    0,
                                    ShroombombEntity::new));
    public static final DeferredItem<ThrowableItem> CORRUPTED_PEARL =
            ModRegistries.ITEMS.register(
                    "corrupted_pearl",
                    () ->
                            new ThrowableItem(
                                    new Item.Properties().stacksTo(16),
                                    SoundEvents.ENDER_PEARL_THROW,
                                    20,
                                    CorruptedPearlEntity::new));

    // Tool and armor mechanics require the 1.21 data-component/material migration.
    public static final DeferredItem<Item> LUMINITE_HELMET =
            armor(
                    "luminite_helmet",
                    CustomArmorMaterials.LUMINITE,
                    ArmorItem.Type.HELMET,
                    15,
                    false);
    public static final DeferredItem<Item> PLATINUM_SWORD =
            ModRegistries.ITEMS.register(
                    "platinum_sword", () -> sword(CustomItemTiers.PLATINUM, false, false));
    public static final DeferredItem<Item> PLATINUM_AXE =
            ModRegistries.ITEMS.register(
                    "platinum_axe", () -> axe(CustomItemTiers.PLATINUM, false, false));
    public static final DeferredItem<Item> PLATINUM_PICKAXE =
            ModRegistries.ITEMS.register(
                    "platinum_pickaxe", () -> pickaxe(CustomItemTiers.PLATINUM, false, false));
    public static final DeferredItem<Item> PLATINUM_SHOVEL =
            ModRegistries.ITEMS.register(
                    "platinum_shovel", () -> shovel(CustomItemTiers.PLATINUM, false, false));
    public static final DeferredItem<Item> PLATINUM_HOE =
            ModRegistries.ITEMS.register(
                    "platinum_hoe", () -> hoe(CustomItemTiers.PLATINUM, false, false));
    public static final DeferredItem<Item> HELLSTONE_SWORD =
            ModRegistries.ITEMS.register(
                    "hellstone_sword", () -> sword(CustomItemTiers.HELLSTONE, true, false));
    public static final DeferredItem<Item> HELLSTONE_AXE =
            ModRegistries.ITEMS.register(
                    "hellstone_axe", () -> axe(CustomItemTiers.HELLSTONE, true, false));
    public static final DeferredItem<Item> HELLSTONE_PICKAXE =
            ModRegistries.ITEMS.register(
                    "hellstone_pickaxe", () -> pickaxe(CustomItemTiers.HELLSTONE, true, false));
    public static final DeferredItem<Item> HELLSTONE_SHOVEL =
            ModRegistries.ITEMS.register(
                    "hellstone_shovel", () -> shovel(CustomItemTiers.HELLSTONE, true, false));
    public static final DeferredItem<Item> HELLSTONE_HOE =
            ModRegistries.ITEMS.register(
                    "hellstone_hoe", () -> hoe(CustomItemTiers.HELLSTONE, true, false));
    public static final DeferredItem<Item> SHROOMSTONE_SWORD =
            ModRegistries.ITEMS.register(
                    "shroomstone_sword", () -> sword(CustomItemTiers.SHROOMSTONE, false, true));
    public static final DeferredItem<Item> SHROOMSTONE_AXE =
            ModRegistries.ITEMS.register(
                    "shroomstone_axe", () -> axe(CustomItemTiers.SHROOMSTONE, false, true));
    public static final DeferredItem<Item> SHROOMSTONE_PICKAXE =
            ModRegistries.ITEMS.register(
                    "shroomstone_pickaxe", () -> pickaxe(CustomItemTiers.SHROOMSTONE, false, true));
    public static final DeferredItem<Item> SHROOMSTONE_SHOVEL =
            ModRegistries.ITEMS.register(
                    "shroomstone_shovel", () -> shovel(CustomItemTiers.SHROOMSTONE, false, true));
    public static final DeferredItem<Item> SHROOMSTONE_HOE =
            ModRegistries.ITEMS.register(
                    "shroomstone_hoe", () -> hoe(CustomItemTiers.SHROOMSTONE, false, true));
    public static final DeferredItem<Item> PLATINUM_HELMET =
            armor(
                    "platinum_helmet",
                    CustomArmorMaterials.PLATINUM,
                    ArmorItem.Type.HELMET,
                    36,
                    false);
    public static final DeferredItem<Item> PLATINUM_CHESTPLATE =
            armor(
                    "platinum_chestplate",
                    CustomArmorMaterials.PLATINUM,
                    ArmorItem.Type.CHESTPLATE,
                    36,
                    false);
    public static final DeferredItem<Item> PLATINUM_LEGGINGS =
            armor(
                    "platinum_leggings",
                    CustomArmorMaterials.PLATINUM,
                    ArmorItem.Type.LEGGINGS,
                    36,
                    false);
    public static final DeferredItem<Item> PLATINUM_BOOTS =
            armor("platinum_boots", CustomArmorMaterials.PLATINUM, ArmorItem.Type.BOOTS, 36, false);
    public static final DeferredItem<Item> SHROOMSTONE_HELMET =
            armor(
                    "shroomstone_helmet",
                    CustomArmorMaterials.SHROOMSTONE,
                    ArmorItem.Type.HELMET,
                    36,
                    false);
    public static final DeferredItem<Item> SHROOMSTONE_CHESTPLATE =
            armor(
                    "shroomstone_chestplate",
                    CustomArmorMaterials.SHROOMSTONE,
                    ArmorItem.Type.CHESTPLATE,
                    36,
                    false);
    public static final DeferredItem<Item> SHROOMSTONE_LEGGINGS =
            armor(
                    "shroomstone_leggings",
                    CustomArmorMaterials.SHROOMSTONE,
                    ArmorItem.Type.LEGGINGS,
                    36,
                    false);
    public static final DeferredItem<Item> SHROOMSTONE_BOOTS =
            armor(
                    "shroomstone_boots",
                    CustomArmorMaterials.SHROOMSTONE,
                    ArmorItem.Type.BOOTS,
                    36,
                    false);
    public static final DeferredItem<Item> HELLSTONE_HELMET =
            armor(
                    "hellstone_helmet",
                    CustomArmorMaterials.HELLSTONE,
                    ArmorItem.Type.HELMET,
                    36,
                    true);
    public static final DeferredItem<Item> HELLSTONE_CHESTPLATE =
            armor(
                    "hellstone_chestplate",
                    CustomArmorMaterials.HELLSTONE,
                    ArmorItem.Type.CHESTPLATE,
                    36,
                    true);
    public static final DeferredItem<Item> HELLSTONE_LEGGINGS =
            armor(
                    "hellstone_leggings",
                    CustomArmorMaterials.HELLSTONE,
                    ArmorItem.Type.LEGGINGS,
                    36,
                    true);
    public static final DeferredItem<Item> HELLSTONE_BOOTS =
            armor(
                    "hellstone_boots",
                    CustomArmorMaterials.HELLSTONE,
                    ArmorItem.Type.BOOTS,
                    36,
                    true);
    public static final DeferredItem<Item> SCORCHSTEEL_HELMET =
            armor(
                    "scorchsteel_helmet",
                    CustomArmorMaterials.SCORCHSTEEL,
                    ArmorItem.Type.HELMET,
                    36,
                    true);
    public static final DeferredItem<Item> SCORCHSTEEL_CHESTPLATE =
            armor(
                    "scorchsteel_chestplate",
                    CustomArmorMaterials.SCORCHSTEEL,
                    ArmorItem.Type.CHESTPLATE,
                    36,
                    true);
    public static final DeferredItem<Item> SCORCHSTEEL_LEGGINGS =
            armor(
                    "scorchsteel_leggings",
                    CustomArmorMaterials.SCORCHSTEEL,
                    ArmorItem.Type.LEGGINGS,
                    36,
                    true);
    public static final DeferredItem<Item> SCORCHSTEEL_BOOTS =
            armor(
                    "scorchsteel_boots",
                    CustomArmorMaterials.SCORCHSTEEL,
                    ArmorItem.Type.BOOTS,
                    36,
                    true);

    public static final DeferredItem<StandingAndWallBlockItem> LUMINITE_TORCH =
            ModRegistries.ITEMS.register(
                    "luminite_torch",
                    () ->
                            new StandingAndWallBlockItem(
                                    CustomBlocks.LUMINITE_TORCH.get(),
                                    CustomBlocks.LUMINITE_WALL_TORCH.get(),
                                    new Item.Properties(),
                                    Direction.DOWN));

    private CustomItems() {}

    public static void bootstrap() {
        // Forces class initialization before the deferred registers attach to the event bus.
    }

    private static DeferredItem<Item> item(String name) {
        return item(name, new Item.Properties());
    }

    private static DeferredItem<Item> item(String name, Item.Properties properties) {
        return ModRegistries.ITEMS.registerSimpleItem(name, properties);
    }

    private static DeferredItem<Item> fireResistantItem(String name) {
        return item(name, new Item.Properties().fireResistant());
    }

    private static DeferredItem<DeferredSpawnEggItem> spawnEgg(
            String name,
            java.util.function.Supplier<
                            ? extends
                                    net.minecraft.world.entity.EntityType<
                                            ? extends net.minecraft.world.entity.Mob>>
                    type,
            int backgroundColor,
            int highlightColor) {
        return ModRegistries.ITEMS.register(
                name,
                () ->
                        new DeferredSpawnEggItem(
                                type, backgroundColor, highlightColor, new Item.Properties()));
    }

    private static DeferredItem<Item> armor(
            String name,
            net.minecraft.core.Holder<net.minecraft.world.item.ArmorMaterial> material,
            ArmorItem.Type type,
            int durabilityMultiplier,
            boolean fireResistant) {
        Item.Properties properties =
                new Item.Properties().durability(type.getDurability(durabilityMultiplier));
        if (fireResistant) {
            properties.fireResistant();
        }
        return ModRegistries.ITEMS.register(name, () -> new ArmorItem(material, type, properties));
    }

    private static Item sword(
            net.minecraft.world.item.Tier tier, boolean ignites, boolean launches) {
        Item.Properties properties =
                toolProperties(ignites).attributes(SwordItem.createAttributes(tier, 3, -2.4F));
        return new SwordItem(tier, properties) {
            @Override
            public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                applyToolEffect(stack, target, attacker, ignites, launches);
                return super.hurtEnemy(stack, target, attacker);
            }
        };
    }

    private static Item axe(net.minecraft.world.item.Tier tier, boolean ignites, boolean launches) {
        Item.Properties properties =
                toolProperties(ignites).attributes(AxeItem.createAttributes(tier, 5.0F, -3.0F));
        return new AxeItem(tier, properties) {
            @Override
            public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                applyToolEffect(stack, target, attacker, ignites, launches);
                return super.hurtEnemy(stack, target, attacker);
            }
        };
    }

    private static Item pickaxe(
            net.minecraft.world.item.Tier tier, boolean ignites, boolean launches) {
        Item.Properties properties =
                toolProperties(ignites).attributes(PickaxeItem.createAttributes(tier, 1.0F, -2.8F));
        return new PickaxeItem(tier, properties) {
            @Override
            public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                applyToolEffect(stack, target, attacker, ignites, launches);
                return super.hurtEnemy(stack, target, attacker);
            }
        };
    }

    private static Item shovel(
            net.minecraft.world.item.Tier tier, boolean ignites, boolean launches) {
        Item.Properties properties =
                toolProperties(ignites).attributes(ShovelItem.createAttributes(tier, 1.5F, -3.0F));
        return new ShovelItem(tier, properties) {
            @Override
            public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                applyToolEffect(stack, target, attacker, ignites, launches);
                return super.hurtEnemy(stack, target, attacker);
            }
        };
    }

    private static Item hoe(net.minecraft.world.item.Tier tier, boolean ignites, boolean launches) {
        Item.Properties properties =
                toolProperties(ignites).attributes(HoeItem.createAttributes(tier, -4.0F, 0.0F));
        return new HoeItem(tier, properties) {
            @Override
            public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                applyToolEffect(stack, target, attacker, ignites, launches);
                return super.hurtEnemy(stack, target, attacker);
            }
        };
    }

    private static Item.Properties toolProperties(boolean fireResistant) {
        Item.Properties properties = new Item.Properties();
        return fireResistant ? properties.fireResistant() : properties;
    }

    private static void applyToolEffect(
            ItemStack stack,
            LivingEntity target,
            LivingEntity attacker,
            boolean ignites,
            boolean launches) {
        if (ignites) {
            int fireAspect =
                    attacker.level()
                            .registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .get(Enchantments.FIRE_ASPECT)
                            .map(stack::getEnchantmentLevel)
                            .orElse(0);
            target.setRemainingFireTicks(
                    Math.max(target.getRemainingFireTicks(), (8 + fireAspect * 4) * 20));
        }
        if (launches) {
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.6, 0.0));
        }
    }
}
