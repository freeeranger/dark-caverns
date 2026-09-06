package com.freeranger.dark_caverns.registry;

import java.util.function.Supplier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public final class ShroomieTrades {
    private ShroomieTrades() {
    }

    public static VillagerTrades.ItemListing[] commonTrades() {
        return new VillagerTrades.ItemListing[] {
                trade(CustomItems.LUMINITE_TORCH, 2, CustomItems.LUMINITE_DUST, 2, 13, 1),
                trade(CustomBlocks.GLIMMERSHROOM, 14, CustomItems.LUMINITE_DUST, 5, 6, 1),
                trade(CustomBlocks.GLIMMERGRASS, 13, CustomItems.LUMINITE_DUST, 4, 7, 1),
                trade(CustomBlocks.GLIMMERSHROOM_BLOCK, 4, CustomItems.LUMINITE_DUST, 6, 3, 1),
                trade(CustomBlocks.LUMINITE_LANTERN, 1, CustomItems.LUMINITE_DUST, 7, 5, 1),
                trade(CustomItems.THROWABLE_LUMINITE_TORCH, 3, () -> Items.IRON_INGOT, 1, 4, 1),
                trade(CustomBlocks.SMOOTH_CARFSTONE, 12, () -> Items.IRON_INGOT, 2, 8, 1),
                trade(CustomBlocks.CARFSTONE_BRICKS, 12, () -> Items.IRON_INGOT, 2, 7, 1),
                trade(CustomItems.LUMINITE_HELMET, 1, () -> Items.IRON_INGOT, 13, 1, 1),
                trade(CustomBlocks.MOLTEN_CARFSTONE_BRICKS, 5, () -> Items.IRON_INGOT, 1, 10, 1),
                trade(CustomItems.SCORCHED_BERRIES, 2, () -> Items.GOLD_INGOT, 3, 4, 1),
                trade(() -> Items.IRON_INGOT, 3, () -> Items.GOLD_INGOT, 2, 6, 1),
                trade(() -> Items.LAPIS_LAZULI, 1, () -> Items.GOLD_INGOT, 4, 8, 1),
                trade(CustomItems.THROWABLE_LUMINITE_TORCH, 2, () -> Items.GOLD_INGOT, 2, 7, 1),
                trade(CustomBlocks.GLIMMERGRASS_BLOCK, 1, () -> Items.GOLD_INGOT, 3, 4, 1)
        };
    }

    public static VillagerTrades.ItemListing[] rareTrades() {
        return new VillagerTrades.ItemListing[] {
                trade(CustomItems.CORRUPTED_PEARL, 1, () -> Items.ENDER_PEARL, 9, 5, 10),
                trade(CustomItems.SHROOMSTONE_PIECE, 1, () -> Items.DIAMOND, 3, 4, 10),
                trade(() -> Items.LAPIS_LAZULI, 1, () -> Items.GOLD_INGOT, 4, 2, 10),
                trade(CustomItems.THROWABLE_LUMINITE_TORCH, 2, () -> Items.GOLD_INGOT, 2, 3, 10),
                trade(CustomItems.SHROOMSTONE_PIECE, 5, () -> Items.DIAMOND, 14, 4, 10),
                trade(() -> Items.SADDLE, 1, () -> Items.EMERALD, 29, 1, 10),
                trade(() -> Items.LEAD, 1, () -> Items.EMERALD, 14, 5, 10),
                trade(() -> Blocks.LIME_STAINED_GLASS, 7, CustomItems.LUMINITE_DUST, 9, 2, 10),
                trade(() -> Items.REDSTONE, 2, () -> Items.LAPIS_LAZULI, 1, 3, 10),
                trade(CustomItems.CORRUPTED_PEARL, 1, () -> Items.EMERALD, 20, 2, 10)
        };
    }

    private static VillagerTrades.ItemListing trade(
            Supplier<? extends ItemLike> result,
            int resultCount,
            Supplier<? extends ItemLike> cost,
            int costCount,
            int maxUses,
            int xp
    ) {
        return new ItemsForItemsTrade(result, resultCount, cost, costCount, maxUses, xp, 0.05F);
    }

    private record ItemsForItemsTrade(
            Supplier<? extends ItemLike> result,
            int resultCount,
            Supplier<? extends ItemLike> cost,
            int costCount,
            int maxUses,
            int xp,
            float priceMultiplier
    ) implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(
                    new ItemCost(cost.get(), costCount),
                    new ItemStack(result.get(), resultCount),
                    maxUses,
                    xp,
                    priceMultiplier
            );
        }
    }
}
