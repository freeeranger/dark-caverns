package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.blocks.*;
import net.minecraft.block.*;
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

public class CustomBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DarkCaverns.MOD_ID);

    public static final RegistryObject<Block> CARFSTONE = register("carfstone", () -> new Block(
            AbstractBlock.Properties.of(Material.STONE)
                    .strength(5f, 7f)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(0)
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> GLIMMERSHROOM = register("glimmershroom", () -> new CustomMushroomBlock(
                    AbstractBlock.Properties.copy(Blocks.RED_MUSHROOM)
                            .lightLevel((p_235464_0_) -> 13)
            )
    );


    public static final RegistryObject<Block> GLIMMERSHROOM_BLOCK = register("glimmershroom_block", () -> new HugeMushroomBlock(
            AbstractBlock.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK)
                    .lightLevel((p_235464_0_) -> 11)
            )
    );

    public static final RegistryObject<Block> GLIMMERGRASS_BLOCK = register("glimmergrass_block", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(5f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(0)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> LUMINITE_BLOCK = register("luminite_block", () -> new Block(
            AbstractBlock.Properties.of(Material.STONE)
                    .strength(6f, 7f)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)
                    .requiresCorrectToolForDrops()
                    .lightLevel((p_235464_0_) -> 15)
            )
    );

    public static final RegistryObject<Block> LUMINITE_ORE = register("carfstone_luminite_ore", () -> new Block(
            AbstractBlock.Properties.of(Material.STONE)
                    .strength(6f, 7f)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)
                    .requiresCorrectToolForDrops()
                    .lightLevel((p_235464_0_) -> 9)
            )
    );

    public static final RegistryObject<Block> PLATINUM_ORE = register("carfstone_platinum_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(8f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(3)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> PLATINUM_BLOCK = register("platinum_block", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(8f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(3)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> CARFSTONE_COAL_ORE = register("carfstone_coal_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(6.5f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(0)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> CARFSTONE_IRON_ORE = register("carfstone_iron_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(7f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(1)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> CARFSTONE_GOLD_ORE = register("carfstone_gold_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(7f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(2)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> CARFSTONE_DIAMOND_ORE = register("carfstone_diamond_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(8f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(2)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> CARFSTONE_REDSTONE_ORE = register("carfstone_redstone_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(7f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(2)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> CARFSTONE_LAPIS_ORE = register("carfstone_lapis_ore", () -> new Block(
                    AbstractBlock.Properties.of(Material.STONE)
                            .strength(7f, 7f)
                            .harvestTool(ToolType.PICKAXE)
                            .harvestLevel(2)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> LUMINITE_TORCH = BLOCKS.register("luminite_torch", () -> new LuminiteTorchBlock(
            AbstractBlock.Properties.copy(Blocks.TORCH)
                    .lightLevel((state) -> 15))
    );
    public static final RegistryObject<Block> LUMINITE_WALL_TORCH = BLOCKS.register("luminite_wall_torch", () -> new LuminiteWallTorchBlock(
            AbstractBlock.Properties.copy(Blocks.WALL_TORCH)
                    .lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> LUMINITE_LANTERN = register("luminite_lantern", () -> new LuminiteLanternBlock(
            AbstractBlock.Properties.copy(Blocks.LANTERN)
    ));

    public static final RegistryObject<Block> GLIMMERGRASS = register("glimmergrass", () -> new CustomPlantBlock(
            AbstractBlock.Properties.copy(Blocks.GRASS)
                    .lightLevel((state) -> 9)
    ));

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
