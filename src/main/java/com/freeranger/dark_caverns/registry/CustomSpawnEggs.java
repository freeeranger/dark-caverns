package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomSpawnEggs {
    private static final int SCORCHED_BACKGROUND = 0x4F4645;
    private static final int SHROOM_BACKGROUND = 0x65ACDC;
    private static final int LUMINITE_BACKGROUND = 0x4B4E58;
    private static final int WHITE_HIGHLIGHT = 0xFFFFFF;

    private static final DeferredRegister.Items SPAWN_EGGS =
            DeferredRegister.createItems(DarkCaverns.MOD_ID);

    public static final DeferredItem<DeferredSpawnEggItem> SCORCHHOUND_SPAWN_EGG =
            spawnEgg(
                    "scorchhound_spawn_egg",
                    CustomEntityTypes.SCORCHHOUND_ENTITY,
                    SCORCHED_BACKGROUND,
                    0xFF6400);
    public static final DeferredItem<DeferredSpawnEggItem> MOLTENER_SPAWN_EGG =
            spawnEgg(
                    "moltener_spawn_egg",
                    CustomEntityTypes.MOLTENER_ENTITY,
                    SCORCHED_BACKGROUND,
                    0x3D5236);
    public static final DeferredItem<DeferredSpawnEggItem> CAMOROCK_SPAWN_EGG =
            spawnEgg("camorock_spawn_egg", CustomEntityTypes.CAMOROCK_ENTITY, 0x424242, 0xE8E8E8);
    public static final DeferredItem<DeferredSpawnEggItem> SHROOMIE_SPAWN_EGG =
            spawnEgg(
                    "shroomie_spawn_egg",
                    CustomEntityTypes.SHROOMIE_ENTITY,
                    SHROOM_BACKGROUND,
                    WHITE_HIGHLIGHT);
    public static final DeferredItem<DeferredSpawnEggItem> SHROOMLING_SPAWN_EGG =
            spawnEgg(
                    "shroomling_spawn_egg",
                    CustomEntityTypes.SHROOMLING_ENTITY,
                    SHROOM_BACKGROUND,
                    WHITE_HIGHLIGHT);
    public static final DeferredItem<DeferredSpawnEggItem> LUMINITE_GOLEM_SPAWN_EGG =
            spawnEgg(
                    "luminite_golem_spawn_egg",
                    CustomEntityTypes.LUMINITE_GOLEM_ENTITY,
                    LUMINITE_BACKGROUND,
                    0x31FF9E);
    public static final DeferredItem<DeferredSpawnEggItem> LUMINITE_FOX_SPAWN_EGG =
            spawnEgg(
                    "luminite_fox_spawn_egg",
                    CustomEntityTypes.LUMINITE_FOX_ENTITY,
                    LUMINITE_BACKGROUND,
                    0x31FF9E);
    public static final DeferredItem<DeferredSpawnEggItem> SCORCHLING_SPAWN_EGG =
            spawnEgg(
                    "scorchling_spawn_egg",
                    CustomEntityTypes.SCORCHLING_ENTITY,
                    SCORCHED_BACKGROUND,
                    0xFF9D08);

    private CustomSpawnEggs() {}

    public static void register(IEventBus modBus) {
        SPAWN_EGGS.register(modBus);
    }

    private static DeferredItem<DeferredSpawnEggItem> spawnEgg(
            String name,
            Supplier<? extends EntityType<? extends Mob>> type,
            int backgroundColor,
            int highlightColor) {
        return SPAWN_EGGS.register(
                name,
                () ->
                        new DeferredSpawnEggItem(
                                type, backgroundColor, highlightColor, new Item.Properties()));
    }
}
