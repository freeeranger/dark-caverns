package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.entities.CorruptedPearlEntity;
import com.freeranger.dark_caverns.entities.ShroombombEntity;
import com.freeranger.dark_caverns.entities.ThrowableLuminiteTorchEntity;
import com.freeranger.dark_caverns.items.KeyToTheCavernsItem;
import com.freeranger.dark_caverns.items.LuminiteChalkItem;
import com.freeranger.dark_caverns.items.ThrowableItem;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomItems {
    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DarkCaverns.MOD_ID);

    private static final FoodProperties SCORCHED_BERRIES_FOOD =
            new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.2F)
                    .alwaysEdible()
                    .fast()
                    .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100), 1.0F)
                    .build();
    private static final FoodProperties SCORCHED_MEAT_FOOD =
            new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build();

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
    public static final DeferredItem<LuminiteChalkItem> LUMINITE_CHALK =
            ITEMS.register(
                    "luminite_chalk",
                    () -> new LuminiteChalkItem(new Item.Properties().durability(64)));

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
                                    10,
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

    public static final DeferredItem<SignItem> TWISTWOOD_SIGN =
            ITEMS.register(
                    "twistwood_sign",
                    () ->
                            new SignItem(
                                    new Item.Properties().stacksTo(16),
                                    CustomBlocks.TWISTWOOD_SIGN.get(),
                                    CustomBlocks.TWISTWOOD_WALL_SIGN.get()));
    public static final DeferredItem<HangingSignItem> TWISTWOOD_HANGING_SIGN =
            ITEMS.register(
                    "twistwood_hanging_sign",
                    () ->
                            new HangingSignItem(
                                    CustomBlocks.TWISTWOOD_HANGING_SIGN.get(),
                                    CustomBlocks.TWISTWOOD_WALL_HANGING_SIGN.get(),
                                    new Item.Properties().stacksTo(16)));

    public static final EnumProxy<Boat.Type> TWISTWOOD_BOAT_TYPE =
            new EnumProxy<>(
                    Boat.Type.class,
                    CustomBlocks.TWISTWOOD_PLANKS,
                    "dark_caverns:twistwood",
                    (Supplier<Item>) () -> CustomItems.TWISTWOOD_BOAT.get(),
                    (Supplier<Item>) () -> CustomItems.TWISTWOOD_CHEST_BOAT.get(),
                    (Supplier<Item>) () -> Items.STICK,
                    false);

    public static final DeferredItem<BoatItem> TWISTWOOD_BOAT =
            ITEMS.register(
                    "twistwood_boat",
                    () ->
                            new BoatItem(
                                    false,
                                    TWISTWOOD_BOAT_TYPE.getValue(),
                                    new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BoatItem> TWISTWOOD_CHEST_BOAT =
            ITEMS.register(
                    "twistwood_chest_boat",
                    () ->
                            new BoatItem(
                                    true,
                                    TWISTWOOD_BOAT_TYPE.getValue(),
                                    new Item.Properties().stacksTo(1)));

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
