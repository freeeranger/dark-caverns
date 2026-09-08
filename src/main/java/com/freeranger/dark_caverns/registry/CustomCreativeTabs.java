package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DarkCaverns.MOD_ID);

    private static final List<Supplier<? extends ItemLike>> CONTENTS =
            List.of(
                    CustomBlocks.CARFSTONE,
                    CustomBlocks.SMOOTH_CARFSTONE,
                    CustomBlocks.CARFSTONE_BRICKS,
                    CustomBlocks.CARFSTONE_STAIRS,
                    CustomBlocks.CARFSTONE_SLAB,
                    CustomBlocks.CARFSTONE_WALL,
                    CustomBlocks.SMOOTH_CARFSTONE_STAIRS,
                    CustomBlocks.SMOOTH_CARFSTONE_SLAB,
                    CustomBlocks.SMOOTH_CARFSTONE_WALL,
                    CustomBlocks.CARFSTONE_BRICK_STAIRS,
                    CustomBlocks.CARFSTONE_BRICK_SLAB,
                    CustomBlocks.CARFSTONE_BRICK_WALL,
                    CustomBlocks.MOLTEN_CARFSTONE,
                    CustomBlocks.SMOOTH_MOLTEN_CARFSTONE,
                    CustomBlocks.MOLTEN_CARFSTONE_BRICKS,
                    CustomBlocks.MOLTEN_CARFSTONE_STAIRS,
                    CustomBlocks.MOLTEN_CARFSTONE_SLAB,
                    CustomBlocks.MOLTEN_CARFSTONE_WALL,
                    CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS,
                    CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB,
                    CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL,
                    CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS,
                    CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB,
                    CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL,
                    CustomBlocks.GLIMMERGRASS_BLOCK,
                    CustomBlocks.CRACKED_BEDROCK,
                    CustomBlocks.GATEWAY_TO_THE_CAVERNS,
                    CustomBlocks.GATEWAY_TO_THE_OVERWORLD,
                    CustomBlocks.CARFSTONE_COAL_ORE,
                    CustomBlocks.CARFSTONE_IRON_ORE,
                    CustomBlocks.CARFSTONE_GOLD_ORE,
                    CustomBlocks.CARFSTONE_REDSTONE_ORE,
                    CustomBlocks.CARFSTONE_LAPIS_ORE,
                    CustomBlocks.CARFSTONE_DIAMOND_ORE,
                    CustomBlocks.PLATINUM_ORE,
                    CustomBlocks.LUMINITE_ORE,
                    CustomBlocks.HELLSTONE_ORE,
                    CustomBlocks.PLATINUM_BLOCK,
                    CustomBlocks.LUMINITE_BLOCK,
                    CustomBlocks.HELLSTONE_BLOCK,
                    CustomBlocks.SHROOMSTONE_BLOCK,
                    CustomBlocks.GLIMMERSHROOM,
                    CustomBlocks.GLIMMERSHROOM_BLOCK,
                    CustomBlocks.GLIMMERGRASS,
                    CustomBlocks.CHARRED_GRASS,
                    CustomItems.SCORCHED_BERRIES,
                    CustomItems.SCORCHED_MEAT,
                    CustomItems.PLATINUM_PIECE,
                    CustomItems.PLATINUM_INGOT,
                    CustomItems.LUMINITE_DUST,
                    CustomItems.HELLSTONE_ROCK,
                    CustomItems.HELLSTONE,
                    CustomItems.SHROOMSTONE_PIECE,
                    CustomItems.SHROOMSTONE,
                    CustomItems.SCORCHLING_TAIL,
                    CustomItems.SCORCHSTEEL_INGOT,
                    CustomEquipment.PLATINUM_SWORD,
                    CustomEquipment.PLATINUM_PICKAXE,
                    CustomEquipment.PLATINUM_AXE,
                    CustomEquipment.PLATINUM_SHOVEL,
                    CustomEquipment.PLATINUM_HOE,
                    CustomEquipment.HELLSTONE_SWORD,
                    CustomEquipment.HELLSTONE_PICKAXE,
                    CustomEquipment.HELLSTONE_AXE,
                    CustomEquipment.HELLSTONE_SHOVEL,
                    CustomEquipment.HELLSTONE_HOE,
                    CustomEquipment.SHROOMSTONE_SWORD,
                    CustomEquipment.SHROOMSTONE_PICKAXE,
                    CustomEquipment.SHROOMSTONE_AXE,
                    CustomEquipment.SHROOMSTONE_SHOVEL,
                    CustomEquipment.SHROOMSTONE_HOE,
                    CustomEquipment.LUMINITE_HELMET,
                    CustomEquipment.PLATINUM_HELMET,
                    CustomEquipment.PLATINUM_CHESTPLATE,
                    CustomEquipment.PLATINUM_LEGGINGS,
                    CustomEquipment.PLATINUM_BOOTS,
                    CustomEquipment.HELLSTONE_HELMET,
                    CustomEquipment.HELLSTONE_CHESTPLATE,
                    CustomEquipment.HELLSTONE_LEGGINGS,
                    CustomEquipment.HELLSTONE_BOOTS,
                    CustomEquipment.SHROOMSTONE_HELMET,
                    CustomEquipment.SHROOMSTONE_CHESTPLATE,
                    CustomEquipment.SHROOMSTONE_LEGGINGS,
                    CustomEquipment.SHROOMSTONE_BOOTS,
                    CustomEquipment.SCORCHSTEEL_HELMET,
                    CustomEquipment.SCORCHSTEEL_CHESTPLATE,
                    CustomEquipment.SCORCHSTEEL_LEGGINGS,
                    CustomEquipment.SCORCHSTEEL_BOOTS,
                    CustomItems.KEY_TO_THE_CAVERNS,
                    CustomItems.LUMINITE_TORCH,
                    CustomBlocks.LUMINITE_LANTERN,
                    CustomItems.THROWABLE_LUMINITE_TORCH,
                    CustomItems.SHROOMBOMB,
                    CustomItems.CORRUPTED_PEARL,
                    CustomSpawnEggs.SCORCHLING_SPAWN_EGG,
                    CustomSpawnEggs.SCORCHHOUND_SPAWN_EGG,
                    CustomSpawnEggs.MOLTENER_SPAWN_EGG,
                    CustomSpawnEggs.CAMOROCK_SPAWN_EGG,
                    CustomSpawnEggs.LUMINITE_GOLEM_SPAWN_EGG,
                    CustomSpawnEggs.LUMINITE_FOX_SPAWN_EGG,
                    CustomSpawnEggs.SHROOMIE_SPAWN_EGG,
                    CustomSpawnEggs.SHROOMLING_SPAWN_EGG);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DARK_CAVERNS =
            CREATIVE_TABS.register(
                    "dark_caverns",
                    () ->
                            CreativeModeTab.builder()
                                    .title(Component.translatable("itemGroup.dark_caverns"))
                                    .icon(() -> new ItemStack(CustomBlocks.LUMINITE_ORE.get()))
                                    .displayItems(
                                            (parameters, output) ->
                                                    CONTENTS.forEach(
                                                            item -> output.accept(item.get())))
                                    .build());

    private CustomCreativeTabs() {}

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
