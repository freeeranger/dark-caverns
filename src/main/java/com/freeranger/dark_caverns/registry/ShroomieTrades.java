package com.freeranger.dark_caverns.registry;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

import java.util.Random;

public class ShroomieTrades {
    public static final Int2ObjectMap<VillagerTrades.ITrade[]> SHROOMIE_TRADES = toIntMap(ImmutableMap.of(
            1,
            new VillagerTrades.ITrade[]{
                new ItemsForItemsTrade(CustomItems.LUMINITE_TORCH.get(), 2, CustomItems.LUMINITE_DUST.get(), 2, 13, 1),
                new ItemsForItemsTrade(CustomBlocks.GLIMMERSHROOM.get(), 14, CustomItems.LUMINITE_DUST.get(), 5, 6, 1),
                new ItemsForItemsTrade(CustomBlocks.GLIMMERGRASS.get(), 13, CustomItems.LUMINITE_DUST.get(), 4, 7, 1),
                new ItemsForItemsTrade(CustomBlocks.GLIMMERSHROOM_BLOCK.get(), 4, CustomItems.LUMINITE_DUST.get(), 6, 3, 1),
                new ItemsForItemsTrade(CustomBlocks.LUMINITE_LANTERN.get(), 1, CustomItems.LUMINITE_DUST.get(), 7, 5, 1),
                new ItemsForItemsTrade(CustomItems.THROWABLE_LUMINITE_TORCH.get(), 3, Items.IRON_INGOT, 1, 4, 1),
                new ItemsForItemsTrade(CustomBlocks.SMOOTH_CARFSTONE.get(), 12, Items.IRON_INGOT, 2, 8, 1),
                new ItemsForItemsTrade(CustomBlocks.CARFSTONE_BRICKS.get(), 12, Items.IRON_INGOT, 2, 7, 1),
                new ItemsForItemsTrade(CustomItems.LUMINITE_HELMET.get(), 1, Items.IRON_INGOT, 13, 1, 1),
                new ItemsForItemsTrade(CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get(), 5, Items.IRON_INGOT, 1, 10, 1),
                new ItemsForItemsTrade(CustomItems.SCORCHED_BERRIES.get(), 2, Items.GOLD_INGOT, 3, 4, 1),
                new ItemsForItemsTrade(Items.IRON_INGOT, 3, Items.GOLD_INGOT, 2, 6, 1),
                new ItemsForItemsTrade(Items.LAPIS_LAZULI, 1, Items.GOLD_INGOT, 4, 8, 1),
                new ItemsForItemsTrade(CustomItems.THROWABLE_LUMINITE_TORCH.get(), 2, Items.GOLD_INGOT, 2, 7, 1),
                new ItemsForItemsTrade(CustomBlocks.GLIMMERGRASS_BLOCK.get(), 1, Items.GOLD_INGOT, 3, 4, 1),
            },
            2,
            new VillagerTrades.ITrade[]{
                new ItemsForItemsTrade(CustomItems.CORRUPTED_PEARL.get(), 1, Items.ENDER_PEARL, 9, 5, 10),
                new ItemsForItemsTrade(CustomItems.SHROOMSTONE_PIECE.get(), 1, Items.DIAMOND, 3, 4, 10),
                new ItemsForItemsTrade(Items.LAPIS_LAZULI, 1, Items.GOLD_INGOT, 4, 2, 10),
                new ItemsForItemsTrade(CustomItems.THROWABLE_LUMINITE_TORCH.get(), 2, Items.GOLD_INGOT, 2, 3, 10),
                new ItemsForItemsTrade(CustomItems.SHROOMSTONE_PIECE.get(), 5, Items.DIAMOND, 2, 4, 10),
                new ItemsForItemsTrade(Items.SADDLE, 1, Items.EMERALD, 29, 1, 10),
                new ItemsForItemsTrade(Items.LEAD, 1, Items.EMERALD, 14, 5, 10),
                new ItemsForItemsTrade(Blocks.LIME_STAINED_GLASS, 7, CustomItems.LUMINITE_DUST.get(), 9, 2, 10),
                new ItemsForItemsTrade(Items.REDSTONE, 2, Items.LAPIS_LAZULI, 1, 3, 10),
                new ItemsForItemsTrade(CustomItems.CORRUPTED_PEARL.get(), 1, Items.EMERALD, 20, 2, 10)
            }
    ));

    private static Int2ObjectMap<VillagerTrades.ITrade[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ITrade[]> p_221238_0_) {
        return new Int2ObjectOpenHashMap<>(p_221238_0_);
    }

    static class ItemsForItemsTrade implements VillagerTrades.ITrade {
        private final ItemStack itemStack;
        private final int itemCount;
        private final ItemStack costItemStack;
        private final int costItemStackCount;
        private final int maxUses;
        private final int xp;
        private final float priceMultiplier;

        public ItemsForItemsTrade(Item item, int itemCount, Item costItem, int costItemCount, int maxUses, int xp) {
            this(new ItemStack(item), itemCount, new ItemStack(costItem), costItemCount, maxUses, xp, 0.05F);
        }

        public ItemsForItemsTrade(Block item, int itemCount, Item costItem, int costItemCount, int maxUses, int xp) {
            this(new ItemStack(item), itemCount, new ItemStack(costItem), costItemCount, maxUses, xp, 0.05F);
        }

        public ItemsForItemsTrade(ItemStack itemStack, int itemCount, ItemStack itemStackCost, int itemCostCount, int maxUses, int xp, float priceMultiplier) {
            this.itemStack = itemStack;
            this.itemCount = itemCount;
            this.costItemStack = itemStackCost;
            this.costItemStackCount = itemCostCount;
            this.maxUses = maxUses;
            this.xp = xp;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity entity, Random rand) {
            return new MerchantOffer(new ItemStack(this.costItemStack.getItem(), this.costItemStackCount), new ItemStack(this.itemStack.getItem(), this.itemCount), this.maxUses, this.xp, this.priceMultiplier);
        }
    }
}