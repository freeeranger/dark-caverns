package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.entities.ShroombombEntity;
import com.freeranger.dark_caverns.entities.ThrowableLuminiteTorchEntity;
import com.freeranger.dark_caverns.items.KeyToTheCavernsItem;
import com.freeranger.dark_caverns.items.ThrowableItem;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomItems {
    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DarkCaverns.MOD_ID);

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
            ITEMS.register(
                    "scorched_berries",
                    () ->
                            new BlockItem(
                                    CustomBlocks.SCORCHED_BERRY_BUSH.get(),
                                    new Item.Properties().food(SCORCHED_BERRIES_FOOD)));
    public static final DeferredItem<KeyToTheCavernsItem> KEY_TO_THE_CAVERNS =
            ITEMS.register(
                    "key_to_the_caverns",
                    () -> new KeyToTheCavernsItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SCORCHLING_TAIL = item("scorchling_tail");
    public static final DeferredItem<Item> SCORCHED_MEAT =
            item("scorched_meat", new Item.Properties().food(SCORCHED_MEAT_FOOD));

    public static final DeferredItem<Item> LUMINITE_DUST = item("luminite_dust");
    public static final DeferredItem<Item> HELLSTONE = fireResistantItem("hellstone");
    public static final DeferredItem<Item> HELLSTONE_ROCK = fireResistantItem("hellstone_rock");
    public static final DeferredItem<Item> PLATINUM_PIECE = item("platinum_piece");
    public static final DeferredItem<Item> SHROOMSTONE = item("shroomstone");
    public static final DeferredItem<Item> SHROOMSTONE_PIECE = item("shroomstone_piece");
    public static final DeferredItem<Item> SCORCHSTEEL_INGOT = item("scorchsteel_ingot");
    public static final DeferredItem<Item> PLATINUM_INGOT = item("platinum_ingot");

    public static final DeferredItem<ThrowableItem> THROWABLE_LUMINITE_TORCH =
            ITEMS.register(
                    "throwable_luminite_torch",
                    () ->
                            new ThrowableItem(
                                    new Item.Properties(),
                                    SoundEvents.SNOWBALL_THROW,
                                    0,
                                    ThrowableLuminiteTorchEntity::new));
    public static final DeferredItem<ThrowableItem> SHROOMBOMB =
            ITEMS.register(
                    "shroombomb",
                    () ->
                            new ThrowableItem(
                                    new Item.Properties(),
                                    SoundEvents.EGG_THROW,
                                    0,
                                    ShroombombEntity::new));
    public static final DeferredItem<ThrowableItem> CORRUPTED_PEARL =
            ITEMS.register(
                    "corrupted_pearl",
                    () ->
                            new ThrowableItem(
                                    new Item.Properties().stacksTo(16),
                                    SoundEvents.ENDER_PEARL_THROW,
                                    20,
                                    CorruptedPearlEntity::new));

    public static final DeferredItem<StandingAndWallBlockItem> LUMINITE_TORCH =
            ITEMS.register(
                    "luminite_torch",
                    () ->
                            new StandingAndWallBlockItem(
                                    CustomBlocks.LUMINITE_TORCH.get(),
                                    CustomBlocks.LUMINITE_WALL_TORCH.get(),
                                    new Item.Properties(),
                                    Direction.DOWN));

    private CustomItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private static DeferredItem<Item> item(String name) {
        return item(name, new Item.Properties());
    }

    private static DeferredItem<Item> item(String name, Item.Properties properties) {
        return ITEMS.registerSimpleItem(name, properties);
    }

    private static DeferredItem<Item> fireResistantItem(String name) {
        return item(name, new Item.Properties().fireResistant());
    }
}
