package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class CustomBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DarkCaverns.MOD_ID);


    public static final RegistryObject<Block> CARFSTONE = register("carfstone", () -> new Block(
            AbstractBlock.Properties.of(Material.STONE)
                    .strength(5f, 7f)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> LUMINITE_ORE = register("carfstone_luminite_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(7f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(3)
                            .requiresCorrectToolForDrops()
                            .lightLevel((p_235464_0_) -> 9)
            )
    );

    private static <T extends Block> RegistryObject<T> baseRegister(String name, Supplier<? extends T> block, Function<RegistryObject<T>, Supplier<? extends Item>> item) {
        RegistryObject<T> register = BLOCKS.register(name, block);
        CustomItems.ITEMS.register(name, item.apply(register));
        return register;
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends Block> block) {
        return (RegistryObject<T>)baseRegister(name, block, CustomBlocks::registerBlockItem);
    }

    private static <T extends Block> Supplier<BlockItem> registerBlockItem(final RegistryObject<T> block) {
        return () -> new BlockItem(Objects.requireNonNull(block.get()), new Item.Properties().tab(CustomItemGroups.GROUP));
    }
}
