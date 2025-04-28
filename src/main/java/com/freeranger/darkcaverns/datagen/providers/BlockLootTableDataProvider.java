package com.freeranger.darkcaverns.datagen.providers;

import com.freeranger.darkcaverns.blocks.ScorchedBerryBushBlock;
import com.freeranger.darkcaverns.registries.BlockRegistry;
import com.freeranger.darkcaverns.registries.ItemRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLootTableDataProvider extends BlockLootSubProvider {
    public BlockLootTableDataProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
    }

    @Override
    protected void generate() {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.dropSelf(BlockRegistry.CARFSTONE.get());
        this.dropSelf(BlockRegistry.CARFSTONE_SLAB.get());
        this.dropSelf(BlockRegistry.CARFSTONE_STAIRS.get());
        this.dropSelf(BlockRegistry.CARFSTONE_WALL.get());
        this.dropSelf(BlockRegistry.CARFSTONE_BRICKS.get());
        this.dropSelf(BlockRegistry.CARFSTONE_BRICK_SLAB.get());
        this.dropSelf(BlockRegistry.CARFSTONE_BRICK_STAIRS.get());
        this.dropSelf(BlockRegistry.CARFSTONE_BRICK_WALL.get());
        this.dropSelf(BlockRegistry.CHISELED_CARFSTONE_BRICKS.get());

        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_SLAB.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_STAIRS.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_WALL.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_BRICKS.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_BRICK_SLAB.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_BRICK_STAIRS.get());
        this.dropSelf(BlockRegistry.MOLTEN_CARFSTONE_BRICK_WALL.get());
        this.dropSelf(BlockRegistry.CHISELED_MOLTEN_CARFSTONE_BRICKS.get());

        this.dropSelf(BlockRegistry.TWISTWOOD_LOG.get());
        this.dropSelf(BlockRegistry.STRIPPED_TWISTWOOD_LOG.get());
        this.dropSelf(BlockRegistry.TWISTWOOD_PLANKS.get());
        this.dropSelf(BlockRegistry.TWISTWOOD_WOOD.get());
        this.dropSelf(BlockRegistry.TWISTWOOD_DOOR.get());
        this.dropSelf(BlockRegistry.TWISTWOOD_TRAPDOOR.get());
        this.dropSelf(BlockRegistry.STRIPPED_TWISTWOOD_WOOD.get());

        this.add(BlockRegistry.TWISTWOOD_LEAVES.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(this.hasShears())
                    .add(LootItem.lootTableItem(BlockRegistry.TWISTWOOD_LEAVES.get()))
            )
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(this.hasSilkTouch())
                    .add(LootItem.lootTableItem(BlockRegistry.TWISTWOOD_LEAVES.get()))
            )
        );
        this.add(BlockRegistry.UNDERSPROUTS.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(this.hasShears())
                    .add(LootItem.lootTableItem(BlockRegistry.UNDERSPROUTS.get()))
            )
        );
        this.add(BlockRegistry.CHARRED_GRASS.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(this.hasShears())
                    .add(LootItem.lootTableItem(BlockRegistry.CHARRED_GRASS.get()))
            )
        );
        this.add(BlockRegistry.ASHY_CHARRED_GRASS.get(), LootTable.lootTable()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(this.hasShears())
                    .add(LootItem.lootTableItem(BlockRegistry.ASHY_CHARRED_GRASS.get()))
            )
        );

        this.dropSelf(BlockRegistry.LUMINITE_BLOCK.get());

        this.add(BlockRegistry.ASHY_MOLTEN_CARFSTONE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.ASHY_MOLTEN_CARFSTONE.get(),
            this.applyExplosionDecay(
                BlockRegistry.ASHY_MOLTEN_CARFSTONE.get(),
                LootItem.lootTableItem(BlockRegistry.MOLTEN_CARFSTONE.get())
            )
        ));
        this.add(BlockRegistry.OVERGROWN_CARFSTONE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.OVERGROWN_CARFSTONE.get(),
            this.applyExplosionDecay(
                BlockRegistry.OVERGROWN_CARFSTONE.get(),
                LootItem.lootTableItem(BlockRegistry.CARFSTONE.get())
            )
        ));
        this.add(BlockRegistry.SCORCHED_BERRY_BUSH.get(), new LootTable.Builder()
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.SCORCHED_BERRY_BUSH.get()).setProperties(
                        StatePropertiesPredicate.Builder.properties().hasProperty(ScorchedBerryBushBlock.AGE, 3)
                    ))
                    .add(LootItem.lootTableItem(ItemRegistry.SCORCHED_BERRIES.get()))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))
                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
            )
            .withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1f))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(BlockRegistry.SCORCHED_BERRY_BUSH.get()).setProperties(
                        StatePropertiesPredicate.Builder.properties().hasProperty(ScorchedBerryBushBlock.AGE, 2)
                    ))
                    .add(LootItem.lootTableItem(ItemRegistry.SCORCHED_BERRIES.get()))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                    .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
            )
        );

        this.add(BlockRegistry.LUMINITE_ORE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.LUMINITE_ORE.get(),
            this.applyExplosionDecay(
                BlockRegistry.LUMINITE_ORE.get(),
                LootItem.lootTableItem(ItemRegistry.LUMINITE_DUST)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F)))
                    .apply(ApplyBonusCount.addUniformBonusCount(
                        registrylookup.getOrThrow(Enchantments.FORTUNE)
                    ))
            )
        ));

        this.add(BlockRegistry.HELLSTONE_ORE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.HELLSTONE_ORE.get(),
            this.applyExplosionDecay(
                BlockRegistry.HELLSTONE_ORE.get(),
                LootItem.lootTableItem(ItemRegistry.HELLSTONE)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                    .apply(ApplyBonusCount.addUniformBonusCount(
                        registrylookup.getOrThrow(Enchantments.FORTUNE)
                    ))
            )
        ));

        this.add(BlockRegistry.CARFSTONE_IRON_ORE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.CARFSTONE_IRON_ORE.get(),
            this.applyExplosionDecay(
                BlockRegistry.CARFSTONE_IRON_ORE.get(),
                LootItem.lootTableItem(Items.RAW_IRON)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                    .apply(ApplyBonusCount.addUniformBonusCount(
                        registrylookup.getOrThrow(Enchantments.FORTUNE)
                    ))
            )
        ));

        this.add(BlockRegistry.CARFSTONE_GOLD_ORE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.CARFSTONE_GOLD_ORE.get(),
            this.applyExplosionDecay(
                BlockRegistry.CARFSTONE_GOLD_ORE.get(),
                LootItem.lootTableItem(Items.RAW_GOLD)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                    .apply(ApplyBonusCount.addUniformBonusCount(
                        registrylookup.getOrThrow(Enchantments.FORTUNE)
                    ))
            )
        ));

        this.add(BlockRegistry.CARFSTONE_DIAMOND_ORE.get(), this.createSilkTouchDispatchTable(
            BlockRegistry.CARFSTONE_DIAMOND_ORE.get(),
            this.applyExplosionDecay(
                BlockRegistry.CARFSTONE_DIAMOND_ORE.get(),
                LootItem.lootTableItem(Items.DIAMOND)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1f)))
                    .apply(ApplyBonusCount.addUniformBonusCount(
                        registrylookup.getOrThrow(Enchantments.FORTUNE)
                    ))
            )
        ));
    }
}
