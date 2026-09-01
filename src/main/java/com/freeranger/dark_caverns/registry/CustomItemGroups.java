package com.freeranger.dark_caverns.registry;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

public class CustomItemGroups {
    public static final ItemGroup GROUP = new ItemGroup("dark_caverns") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CustomBlocks.LUMINITE_ORE.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            // Blocks & Stone
            add(items, CustomBlocks.CARFSTONE.get());
            add(items, CustomBlocks.SMOOTH_CARFSTONE.get());
            add(items, CustomBlocks.CARFSTONE_BRICKS.get());
            add(items, CustomBlocks.CARFSTONE_STAIRS.get());
            add(items, CustomBlocks.CARFSTONE_SLAB.get());
            add(items, CustomBlocks.CARFSTONE_WALL.get());
            add(items, CustomBlocks.SMOOTH_CARFSTONE_STAIRS.get());
            add(items, CustomBlocks.SMOOTH_CARFSTONE_SLAB.get());
            add(items, CustomBlocks.SMOOTH_CARFSTONE_WALL.get());
            add(items, CustomBlocks.CARFSTONE_BRICK_STAIRS.get());
            add(items, CustomBlocks.CARFSTONE_BRICK_SLAB.get());
            add(items, CustomBlocks.CARFSTONE_BRICK_WALL.get());

            add(items, CustomBlocks.MOLTEN_CARFSTONE.get());
            add(items, CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_STAIRS.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_SLAB.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_WALL.get());
            add(items, CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS.get());
            add(items, CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB.get());
            add(items, CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB.get());
            add(items, CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL.get());

            add(items, CustomBlocks.GLIMMERGRASS_BLOCK.get());
            add(items, CustomBlocks.CRACKED_BEDROCK.get());
            add(items, CustomBlocks.GATEWAY_TO_THE_CAVERNS.get());
            add(items, CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get());

            // Ores & Storage Blocks
            add(items, CustomBlocks.CARFSTONE_COAL_ORE.get());
            add(items, CustomBlocks.CARFSTONE_IRON_ORE.get());
            add(items, CustomBlocks.CARFSTONE_GOLD_ORE.get());
            add(items, CustomBlocks.CARFSTONE_REDSTONE_ORE.get());
            add(items, CustomBlocks.CARFSTONE_LAPIS_ORE.get());
            add(items, CustomBlocks.CARFSTONE_DIAMOND_ORE.get());
            add(items, CustomBlocks.PLATINUM_ORE.get());
            add(items, CustomBlocks.LUMINITE_ORE.get());
            add(items, CustomBlocks.HELLSTONE_ORE.get());

            add(items, CustomBlocks.PLATINUM_BLOCK.get());
            add(items, CustomBlocks.LUMINITE_BLOCK.get());
            add(items, CustomBlocks.HELLSTONE_BLOCK.get());
            add(items, CustomBlocks.SHROOMSTONE_BLOCK.get());

            // Plants & Nature
            add(items, CustomBlocks.GLIMMERSHROOM.get());
            add(items, CustomBlocks.GLIMMERSHROOM_BLOCK.get());
            add(items, CustomBlocks.GLIMMERGRASS.get());
            add(items, CustomBlocks.CHARRED_GRASS.get());
            add(items, CustomItems.SCORCHED_BERRIES.get());
            add(items, CustomItems.SCORCHED_MEAT.get());

            // Raw Resources & Materials
            add(items, CustomItems.PLATINUM_PIECE.get());
            add(items, CustomItems.PLATINUM_INGOT.get());
            add(items, CustomItems.LUMINITE_DUST.get());
            add(items, CustomItems.HELLSTONE_ROCK.get());
            add(items, CustomItems.HELLSTONE.get());
            add(items, CustomItems.SHROOMSTONE_PIECE.get());
            add(items, CustomItems.SHROOMSTONE.get());
            add(items, CustomItems.SCORCHLING_TAIL.get());
            add(items, CustomItems.SCORCHSTEEL_INGOT.get());

            // Tools & Weapons - Platinum
            add(items, CustomItems.PLATINUM_SWORD.get());
            add(items, CustomItems.PLATINUM_PICKAXE.get());
            add(items, CustomItems.PLATINUM_AXE.get());
            add(items, CustomItems.PLATINUM_SHOVEL.get());
            add(items, CustomItems.PLATINUM_HOE.get());

            // Tools & Weapons - Hellstone
            add(items, CustomItems.HELLSTONE_SWORD.get());
            add(items, CustomItems.HELLSTONE_PICKAXE.get());
            add(items, CustomItems.HELLSTONE_AXE.get());
            add(items, CustomItems.HELLSTONE_SHOVEL.get());
            add(items, CustomItems.HELLSTONE_HOE.get());

            // Tools & Weapons - Shroomstone
            add(items, CustomItems.SHROOMSTONE_SWORD.get());
            add(items, CustomItems.SHROOMSTONE_PICKAXE.get());
            add(items, CustomItems.SHROOMSTONE_AXE.get());
            add(items, CustomItems.SHROOMSTONE_SHOVEL.get());
            add(items, CustomItems.SHROOMSTONE_HOE.get());

            // Armor
            add(items, CustomItems.LUMINITE_HELMET.get());

            add(items, CustomItems.PLATINUM_HELMET.get());
            add(items, CustomItems.PLATINUM_CHESTPLATE.get());
            add(items, CustomItems.PLATINUM_LEGGINGS.get());
            add(items, CustomItems.PLATINUM_BOOTS.get());

            add(items, CustomItems.HELLSTONE_HELMET.get());
            add(items, CustomItems.HELLSTONE_CHESTPLATE.get());
            add(items, CustomItems.HELLSTONE_LEGGINGS.get());
            add(items, CustomItems.HELLSTONE_BOOTS.get());

            add(items, CustomItems.SHROOMSTONE_HELMET.get());
            add(items, CustomItems.SHROOMSTONE_CHESTPLATE.get());
            add(items, CustomItems.SHROOMSTONE_LEGGINGS.get());
            add(items, CustomItems.SHROOMSTONE_BOOTS.get());

            add(items, CustomItems.SCORCHSTEEL_HELMET.get());
            add(items, CustomItems.SCORCHSTEEL_CHESTPLATE.get());
            add(items, CustomItems.SCORCHSTEEL_LEGGINGS.get());
            add(items, CustomItems.SCORCHSTEEL_BOOTS.get());

            // Utilities & Lights
            add(items, CustomItems.KEY_TO_THE_CAVERNS.get());
            add(items, CustomItems.LUMINITE_TORCH.get());
            add(items, CustomBlocks.LUMINITE_LANTERN.get());
            add(items, CustomItems.THROWABLE_LUMINITE_TORCH.get());
            add(items, CustomItems.SHROOMBOMB.get());
            add(items, CustomItems.CORRUPTED_PEARL.get());

            // Spawn Eggs
            add(items, CustomItems.SCORCHLING_SPAWN_EGG.get());
            add(items, CustomItems.SCORCHHOUND_SPAWN_EGG.get());
            add(items, CustomItems.MOLTENER_SPAWN_EGG.get());
            add(items, CustomItems.CAMOROCK_SPAWN_EGG.get());
            add(items, CustomItems.LUMINITE_GOLEM_SPAWN_EGG.get());
            add(items, CustomItems.LUMINITE_FOX_SPAWN_EGG.get());
            add(items, CustomItems.SHROOMIE_SPAWN_EGG.get());
            add(items, CustomItems.SHROOMLING_SPAWN_EGG.get());
        }

        private void add(NonNullList<ItemStack> list, IItemProvider itemProvider) {
            if (itemProvider != null && itemProvider.asItem() != null) {
                list.add(new ItemStack(itemProvider));
            }
        }
    };
}
