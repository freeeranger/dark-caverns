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

public final class ShroomieTrades {
    private ShroomieTrades() {}

    public static VillagerTrades.ItemListing[] commonTrades() {
        return new VillagerTrades.ItemListing[] {
            trade(CustomItems.LUMINITE_TORCH, 8, CustomItems.LUMINITE_DUST, 1, 16, 1),
            trade(CustomBlocks.GLIMMERSHROOM, 4, CustomItems.LUMINITE_DUST, 1, 12, 1),
            trade(CustomBlocks.GLIMMERGRASS, 8, CustomItems.LUMINITE_DUST, 1, 12, 1),
            trade(CustomBlocks.GLIMMERSHROOM_BLOCK, 4, CustomItems.LUMINITE_DUST, 3, 8, 1),
            trade(CustomBlocks.LUMINITE_LANTERN, 1, CustomItems.LUMINITE_DUST, 3, 8, 1),
            trade(CustomItems.THROWABLE_LUMINITE_TORCH, 4, () -> Items.IRON_INGOT, 1, 12, 1),
            trade(CustomBlocks.SMOOTH_CARFSTONE, 16, () -> Items.IRON_INGOT, 1, 16, 1),
            trade(CustomBlocks.CARFSTONE_BRICKS, 16, () -> Items.IRON_INGOT, 1, 16, 1),
            trade(CustomEquipment.LUMINITE_HELMET, 1, () -> Items.IRON_INGOT, 10, 2, 5),
            trade(CustomBlocks.MOLTEN_CARFSTONE_BRICKS, 8, () -> Items.IRON_INGOT, 1, 12, 1),
            trade(CustomItems.SCORCHED_BERRIES, 4, () -> Items.GOLD_INGOT, 1, 8, 1),
            trade(CustomBlocks.GLIMMERGRASS_BLOCK, 4, () -> Items.GOLD_INGOT, 1, 8, 1)
        };
    }

    public static VillagerTrades.ItemListing progressionTrade() {
        return trade(CustomItems.SHROOMSTONE_PIECE, 2, () -> Items.DIAMOND, 1, 8, 10);
    }

    public static VillagerTrades.ItemListing[] rareTrades() {
        return new VillagerTrades.ItemListing[] {
            trade(CustomItems.CORRUPTED_PEARL, 1, () -> Items.ENDER_PEARL, 4, 4, 10),
            trade(() -> Items.SADDLE, 1, () -> Items.EMERALD, 8, 2, 10),
            trade(() -> Items.LEAD, 1, () -> Items.EMERALD, 4, 6, 10),
            trade(() -> Items.NAME_TAG, 1, () -> Items.EMERALD, 12, 2, 10)
        };
    }

    private static VillagerTrades.ItemListing trade(
            Supplier<? extends ItemLike> result,
            int resultCount,
            Supplier<? extends ItemLike> cost,
            int costCount,
            int maxUses,
            int xp) {
        return new ItemsForItemsTrade(result, resultCount, cost, costCount, maxUses, xp, 0.05F);
    }

    private record ItemsForItemsTrade(
            Supplier<? extends ItemLike> result,
            int resultCount,
            Supplier<? extends ItemLike> cost,
            int costCount,
            int maxUses,
            int xp,
            float priceMultiplier)
            implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(
                    new ItemCost(cost.get(), costCount),
                    new ItemStack(result.get(), resultCount),
                    maxUses,
                    xp,
                    priceMultiplier);
        }
    }
}
