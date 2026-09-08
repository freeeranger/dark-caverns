package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomItems;
import java.util.Set;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

final class DarkCavernsBlockLoot extends BlockLootSubProvider {
    DarkCavernsBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        addSpecialDrops();

        for (Block block : getKnownBlocks()) {
            if (block.getLootTable() == BuiltInLootTables.EMPTY || hasSpecialDrop(block)) {
                continue;
            }
            if (block instanceof SlabBlock) {
                add(block, createSlabItemTable(block));
            } else if (block == CustomBlocks.LUMINITE_WALL_TORCH.get()) {
                dropOther(block, CustomItems.LUMINITE_TORCH.get());
            } else {
                dropSelf(block);
            }
        }
    }

    private void addSpecialDrops() {
        add(CustomBlocks.CARFSTONE_COAL_ORE.get(), block -> createOreDrop(block, Items.COAL));
        add(CustomBlocks.CARFSTONE_DIAMOND_ORE.get(), block -> createOreDrop(block, Items.DIAMOND));
        add(CustomBlocks.CARFSTONE_LAPIS_ORE.get(), this::createLapisOreDrops);
        add(CustomBlocks.CARFSTONE_REDSTONE_ORE.get(), this::createRedstoneOreDrops);
        add(
                CustomBlocks.HELLSTONE_ORE.get(),
                block -> createOreDrop(block, CustomItems.HELLSTONE_ROCK.get()));
        add(CustomBlocks.LUMINITE_ORE.get(), this::createLuminiteOreDrops);
        add(
                CustomBlocks.GLIMMERGRASS_BLOCK.get(),
                block -> createSingleItemTableWithSilkTouch(block, CustomBlocks.CARFSTONE.get()));
        add(
                CustomBlocks.GLIMMERSHROOM_BLOCK.get(),
                block -> createMushroomBlockDrop(block, CustomBlocks.GLIMMERSHROOM.get()));
        add(CustomBlocks.GLIMMERGRASS.get(), block -> createShearsOnlyDrop(block));
        add(CustomBlocks.CHARRED_GRASS.get(), block -> createShearsOnlyDrop(block));
        add(CustomBlocks.SCORCHED_BERRY_BUSH.get(), this::createScorchedBerryDrops);
    }

    private LootTable.Builder createLuminiteOreDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> enchantments =
                registries.lookupOrThrow(Registries.ENCHANTMENT);
        return createSilkTouchDispatchTable(
                block,
                applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(CustomItems.LUMINITE_DUST.get())
                                .apply(
                                        SetItemCountFunction.setCount(
                                                UniformGenerator.between(1.0F, 4.0F)))
                                .apply(
                                        ApplyBonusCount.addUniformBonusCount(
                                                enchantments.getOrThrow(Enchantments.FORTUNE)))));
    }

    private LootTable.Builder createScorchedBerryDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> enchantments =
                registries.lookupOrThrow(Registries.ENCHANTMENT);
        return applyExplosionDecay(
                block,
                LootTable.lootTable()
                        .withPool(
                                berryPool(
                                        block,
                                        SweetBerryBushBlock.MAX_AGE,
                                        2.0F,
                                        3.0F,
                                        enchantments))
                        .withPool(berryPool(block, 2, 1.0F, 2.0F, enchantments)));
    }

    private LootPool.Builder berryPool(
            Block block,
            int age,
            float minimum,
            float maximum,
            HolderLookup.RegistryLookup<Enchantment> enchantments) {
        return LootPool.lootPool()
                .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(
                                        StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(SweetBerryBushBlock.AGE, age)))
                .add(LootItem.lootTableItem(CustomItems.SCORCHED_BERRIES.get()))
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minimum, maximum)))
                .apply(
                        ApplyBonusCount.addUniformBonusCount(
                                enchantments.getOrThrow(Enchantments.FORTUNE)));
    }

    private boolean hasSpecialDrop(Block block) {
        return block == CustomBlocks.CARFSTONE_COAL_ORE.get()
                || block == CustomBlocks.CARFSTONE_DIAMOND_ORE.get()
                || block == CustomBlocks.CARFSTONE_LAPIS_ORE.get()
                || block == CustomBlocks.CARFSTONE_REDSTONE_ORE.get()
                || block == CustomBlocks.HELLSTONE_ORE.get()
                || block == CustomBlocks.LUMINITE_ORE.get()
                || block == CustomBlocks.GLIMMERGRASS_BLOCK.get()
                || block == CustomBlocks.GLIMMERSHROOM_BLOCK.get()
                || block == CustomBlocks.GLIMMERGRASS.get()
                || block == CustomBlocks.CHARRED_GRASS.get()
                || block == CustomBlocks.SCORCHED_BERRY_BUSH.get();
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return () ->
                BuiltInRegistries.BLOCK.stream()
                        .filter(
                                block ->
                                        BuiltInRegistries.BLOCK
                                                .getKey(block)
                                                .getNamespace()
                                                .equals(DarkCaverns.MOD_ID))
                        .iterator();
    }
}
