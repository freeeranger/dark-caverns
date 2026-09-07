package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DarkCaverns.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DARK_CAVERNS =
            CREATIVE_TABS.register(
                    "dark_caverns",
                    () ->
                            CreativeModeTab.builder()
                                    .title(Component.translatable("itemGroup.dark_caverns"))
                                    .icon(() -> new ItemStack(CustomBlocks.LUMINITE_ORE.get()))
                                    .displayItems(
                                            (parameters, output) -> {
                                                output.accept(CustomBlocks.CARFSTONE.get());
                                                output.accept(CustomBlocks.SMOOTH_CARFSTONE.get());
                                                output.accept(CustomBlocks.CARFSTONE_BRICKS.get());
                                                output.accept(CustomBlocks.CARFSTONE_STAIRS.get());
                                                output.accept(CustomBlocks.CARFSTONE_SLAB.get());
                                                output.accept(CustomBlocks.CARFSTONE_WALL.get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_CARFSTONE_STAIRS.get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_CARFSTONE_SLAB.get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_CARFSTONE_WALL.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_BRICK_STAIRS.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_BRICK_SLAB.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_BRICK_WALL.get());

                                                output.accept(CustomBlocks.MOLTEN_CARFSTONE.get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_STAIRS.get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_SLAB.get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_WALL.get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS
                                                                .get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB
                                                                .get());
                                                output.accept(
                                                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL
                                                                .get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS
                                                                .get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB
                                                                .get());
                                                output.accept(
                                                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL
                                                                .get());

                                                output.accept(
                                                        CustomBlocks.GLIMMERGRASS_BLOCK.get());
                                                output.accept(CustomBlocks.CRACKED_BEDROCK.get());
                                                output.accept(
                                                        CustomBlocks.GATEWAY_TO_THE_CAVERNS.get());
                                                output.accept(
                                                        CustomBlocks.GATEWAY_TO_THE_OVERWORLD
                                                                .get());

                                                output.accept(
                                                        CustomBlocks.CARFSTONE_COAL_ORE.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_IRON_ORE.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_GOLD_ORE.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_REDSTONE_ORE.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_LAPIS_ORE.get());
                                                output.accept(
                                                        CustomBlocks.CARFSTONE_DIAMOND_ORE.get());
                                                output.accept(CustomBlocks.PLATINUM_ORE.get());
                                                output.accept(CustomBlocks.LUMINITE_ORE.get());
                                                output.accept(CustomBlocks.HELLSTONE_ORE.get());

                                                output.accept(CustomBlocks.PLATINUM_BLOCK.get());
                                                output.accept(CustomBlocks.LUMINITE_BLOCK.get());
                                                output.accept(CustomBlocks.HELLSTONE_BLOCK.get());
                                                output.accept(CustomBlocks.SHROOMSTONE_BLOCK.get());

                                                output.accept(CustomBlocks.GLIMMERSHROOM.get());
                                                output.accept(
                                                        CustomBlocks.GLIMMERSHROOM_BLOCK.get());
                                                output.accept(CustomBlocks.GLIMMERGRASS.get());
                                                output.accept(CustomBlocks.CHARRED_GRASS.get());
                                                output.accept(CustomItems.SCORCHED_BERRIES.get());
                                                output.accept(CustomItems.SCORCHED_MEAT.get());

                                                output.accept(CustomItems.PLATINUM_PIECE.get());
                                                output.accept(CustomItems.PLATINUM_INGOT.get());
                                                output.accept(CustomItems.LUMINITE_DUST.get());
                                                output.accept(CustomItems.HELLSTONE_ROCK.get());
                                                output.accept(CustomItems.HELLSTONE.get());
                                                output.accept(CustomItems.SHROOMSTONE_PIECE.get());
                                                output.accept(CustomItems.SHROOMSTONE.get());
                                                output.accept(CustomItems.SCORCHLING_TAIL.get());
                                                output.accept(CustomItems.SCORCHSTEEL_INGOT.get());

                                                output.accept(CustomEquipment.PLATINUM_SWORD.get());
                                                output.accept(
                                                        CustomEquipment.PLATINUM_PICKAXE.get());
                                                output.accept(CustomEquipment.PLATINUM_AXE.get());
                                                output.accept(
                                                        CustomEquipment.PLATINUM_SHOVEL.get());
                                                output.accept(CustomEquipment.PLATINUM_HOE.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_SWORD.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_PICKAXE.get());
                                                output.accept(CustomEquipment.HELLSTONE_AXE.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_SHOVEL.get());
                                                output.accept(CustomEquipment.HELLSTONE_HOE.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_SWORD.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_PICKAXE.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_AXE.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_SHOVEL.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_HOE.get());

                                                output.accept(
                                                        CustomEquipment.LUMINITE_HELMET.get());
                                                output.accept(
                                                        CustomEquipment.PLATINUM_HELMET.get());
                                                output.accept(
                                                        CustomEquipment.PLATINUM_CHESTPLATE.get());
                                                output.accept(
                                                        CustomEquipment.PLATINUM_LEGGINGS.get());
                                                output.accept(CustomEquipment.PLATINUM_BOOTS.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_HELMET.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_CHESTPLATE.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_LEGGINGS.get());
                                                output.accept(
                                                        CustomEquipment.HELLSTONE_BOOTS.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_HELMET.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_CHESTPLATE
                                                                .get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_LEGGINGS.get());
                                                output.accept(
                                                        CustomEquipment.SHROOMSTONE_BOOTS.get());
                                                output.accept(
                                                        CustomEquipment.SCORCHSTEEL_HELMET.get());
                                                output.accept(
                                                        CustomEquipment.SCORCHSTEEL_CHESTPLATE
                                                                .get());
                                                output.accept(
                                                        CustomEquipment.SCORCHSTEEL_LEGGINGS.get());
                                                output.accept(
                                                        CustomEquipment.SCORCHSTEEL_BOOTS.get());

                                                output.accept(CustomItems.KEY_TO_THE_CAVERNS.get());
                                                output.accept(CustomItems.LUMINITE_TORCH.get());
                                                output.accept(CustomBlocks.LUMINITE_LANTERN.get());
                                                output.accept(
                                                        CustomItems.THROWABLE_LUMINITE_TORCH.get());
                                                output.accept(CustomItems.SHROOMBOMB.get());
                                                output.accept(CustomItems.CORRUPTED_PEARL.get());

                                                output.accept(
                                                        CustomSpawnEggs.SCORCHLING_SPAWN_EGG.get());
                                                output.accept(
                                                        CustomSpawnEggs.SCORCHHOUND_SPAWN_EGG
                                                                .get());
                                                output.accept(
                                                        CustomSpawnEggs.MOLTENER_SPAWN_EGG.get());
                                                output.accept(
                                                        CustomSpawnEggs.CAMOROCK_SPAWN_EGG.get());
                                                output.accept(
                                                        CustomSpawnEggs.LUMINITE_GOLEM_SPAWN_EGG
                                                                .get());
                                                output.accept(
                                                        CustomSpawnEggs.LUMINITE_FOX_SPAWN_EGG
                                                                .get());
                                                output.accept(
                                                        CustomSpawnEggs.SHROOMIE_SPAWN_EGG.get());
                                                output.accept(
                                                        CustomSpawnEggs.SHROOMLING_SPAWN_EGG.get());
                                            })
                                    .build());

    private CustomCreativeTabs() {}

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
