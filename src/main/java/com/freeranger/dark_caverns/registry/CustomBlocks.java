package com.freeranger.dark_caverns.registry;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.blocks.CustomMushroomBlock;
import com.freeranger.dark_caverns.blocks.CustomPlantBlock;
import com.freeranger.dark_caverns.blocks.GatewayToTheCavernsBlock;
import com.freeranger.dark_caverns.blocks.GatewayToTheOverworldBlock;
import com.freeranger.dark_caverns.blocks.GlimmershroomBlock;
import com.freeranger.dark_caverns.blocks.LuminiteTorchBlock;
import com.freeranger.dark_caverns.blocks.LuminiteWallTorchBlock;
import com.freeranger.dark_caverns.blocks.ScorchedBerryBushBlock;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomBlocks {
    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DarkCaverns.MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS =
            DeferredRegister.createItems(DarkCaverns.MOD_ID);

    private static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_GLIMMERSHROOM =
            featureKey("huge_mushroom_feature");
    private static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_HIGH_GLIMMERSHROOM =
            featureKey("huge_high_mushroom_feature");

    public static final DeferredBlock<Block> CARFSTONE =
            register("carfstone", () -> new Block(stone(4.0F)));
    public static final DeferredBlock<Block> SMOOTH_CARFSTONE = copy("smooth_carfstone", CARFSTONE);
    public static final DeferredBlock<Block> CARFSTONE_BRICKS = copy("carfstone_bricks", CARFSTONE);
    public static final DeferredBlock<StairBlock> CARFSTONE_BRICK_STAIRS =
            stairs("carfstone_brick_stairs", CARFSTONE_BRICKS);
    public static final DeferredBlock<SlabBlock> CARFSTONE_BRICK_SLAB =
            slab("carfstone_brick_slab", CARFSTONE_BRICKS);
    public static final DeferredBlock<StairBlock> CARFSTONE_STAIRS =
            stairs("carfstone_stairs", CARFSTONE);
    public static final DeferredBlock<SlabBlock> CARFSTONE_SLAB = slab("carfstone_slab", CARFSTONE);
    public static final DeferredBlock<StairBlock> SMOOTH_CARFSTONE_STAIRS =
            stairs("smooth_carfstone_stairs", SMOOTH_CARFSTONE);
    public static final DeferredBlock<SlabBlock> SMOOTH_CARFSTONE_SLAB =
            slab("smooth_carfstone_slab", SMOOTH_CARFSTONE);
    public static final DeferredBlock<WallBlock> CARFSTONE_WALL = wall("carfstone_wall", CARFSTONE);
    public static final DeferredBlock<WallBlock> SMOOTH_CARFSTONE_WALL =
            wall("smooth_carfstone_wall", SMOOTH_CARFSTONE);
    public static final DeferredBlock<WallBlock> CARFSTONE_BRICK_WALL =
            wall("carfstone_brick_wall", CARFSTONE_BRICKS);

    public static final DeferredBlock<Block> CRACKED_BEDROCK =
            register(
                    "cracked_bedrock",
                    () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)));
    public static final DeferredBlock<Block> GATEWAY_TO_THE_CAVERNS =
            register(
                    "gateway_to_the_caverns",
                    () ->
                            new GatewayToTheCavernsBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)));
    public static final DeferredBlock<Block> GATEWAY_TO_THE_OVERWORLD =
            register(
                    "gateway_to_the_overworld",
                    () ->
                            new GatewayToTheOverworldBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK)
                                            .noCollission()));

    public static final DeferredBlock<Block> MOLTEN_CARFSTONE =
            register("molten_carfstone", () -> new Block(stone(4.5F)));
    public static final DeferredBlock<Block> SMOOTH_MOLTEN_CARFSTONE =
            copy("smooth_molten_carfstone", MOLTEN_CARFSTONE);
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_BRICKS =
            copy("molten_carfstone_bricks", MOLTEN_CARFSTONE);
    public static final DeferredBlock<StairBlock> MOLTEN_CARFSTONE_BRICK_STAIRS =
            stairs("molten_carfstone_brick_stairs", MOLTEN_CARFSTONE_BRICKS);
    public static final DeferredBlock<SlabBlock> MOLTEN_CARFSTONE_BRICK_SLAB =
            slab("molten_carfstone_brick_slab", MOLTEN_CARFSTONE_BRICKS);
    public static final DeferredBlock<StairBlock> MOLTEN_CARFSTONE_STAIRS =
            stairs("molten_carfstone_stairs", MOLTEN_CARFSTONE);
    public static final DeferredBlock<SlabBlock> MOLTEN_CARFSTONE_SLAB =
            slab("molten_carfstone_slab", MOLTEN_CARFSTONE);
    public static final DeferredBlock<StairBlock> SMOOTH_MOLTEN_CARFSTONE_STAIRS =
            stairs("smooth_molten_carfstone_stairs", SMOOTH_MOLTEN_CARFSTONE);
    public static final DeferredBlock<SlabBlock> SMOOTH_MOLTEN_CARFSTONE_SLAB =
            slab("smooth_molten_carfstone_slab", SMOOTH_MOLTEN_CARFSTONE);
    public static final DeferredBlock<WallBlock> MOLTEN_CARFSTONE_WALL =
            wall("molten_carfstone_wall", MOLTEN_CARFSTONE);
    public static final DeferredBlock<WallBlock> SMOOTH_MOLTEN_CARFSTONE_WALL =
            wall("smooth_molten_carfstone_wall", SMOOTH_MOLTEN_CARFSTONE);
    public static final DeferredBlock<WallBlock> MOLTEN_CARFSTONE_BRICK_WALL =
            wall("molten_carfstone_brick_wall", MOLTEN_CARFSTONE_BRICKS);

    public static final DeferredBlock<ScorchedBerryBushBlock> SCORCHED_BERRY_BUSH =
            registerWithoutItem(
                    "scorched_berry_bush",
                    () ->
                            new ScorchedBerryBushBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)
                                            .lightLevel(state -> 7),
                                    MOLTEN_CARFSTONE));
    public static final DeferredBlock<CustomMushroomBlock> GLIMMERSHROOM =
            register(
                    "glimmershroom",
                    () ->
                            new CustomMushroomBlock(
                                    HUGE_GLIMMERSHROOM,
                                    HUGE_HIGH_GLIMMERSHROOM,
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM)
                                            .lightLevel(state -> 13)));
    public static final DeferredBlock<GlimmershroomBlock> GLIMMERSHROOM_BLOCK =
            register(
                    "glimmershroom_block",
                    () ->
                            new GlimmershroomBlock(
                                    BlockBehaviour.Properties.ofFullCopy(
                                                    Blocks.BROWN_MUSHROOM_BLOCK)
                                            .lightLevel(state -> 11)));
    public static final DeferredBlock<Block> GLIMMERGRASS_BLOCK =
            register("glimmergrass_block", () -> new Block(stone(4.0F)));

    public static final DeferredBlock<Block> LUMINITE_BLOCK =
            register("luminite_block", () -> new Block(stone(5.0F).lightLevel(state -> 15)));
    public static final DeferredBlock<Block> LUMINITE_ORE =
            register(
                    "carfstone_luminite_ore",
                    () ->
                            new DropExperienceBlock(
                                    UniformInt.of(1, 4), stone(5.0F).lightLevel(state -> 9)));
    public static final DeferredBlock<Block> PLATINUM_ORE =
            register("carfstone_platinum_ore", () -> new Block(stone(7.0F)));
    public static final DeferredBlock<Block> PLATINUM_BLOCK =
            register("platinum_block", () -> new Block(stone(7.0F)));
    public static final DeferredBlock<Block> CARFSTONE_COAL_ORE =
            register(
                    "carfstone_coal_ore",
                    () -> new DropExperienceBlock(UniformInt.of(0, 2), stone(4.5F)));
    public static final DeferredBlock<Block> CARFSTONE_IRON_ORE =
            register("carfstone_iron_ore", () -> new Block(stone(5.0F)));
    public static final DeferredBlock<Block> CARFSTONE_GOLD_ORE =
            register("carfstone_gold_ore", () -> new Block(stone(6.0F)));
    public static final DeferredBlock<Block> CARFSTONE_DIAMOND_ORE =
            register(
                    "carfstone_diamond_ore",
                    () -> new DropExperienceBlock(UniformInt.of(3, 7), stone(7.0F)));
    public static final DeferredBlock<Block> CARFSTONE_REDSTONE_ORE =
            register(
                    "carfstone_redstone_ore",
                    () -> new DropExperienceBlock(UniformInt.of(1, 5), stone(6.0F)));
    public static final DeferredBlock<Block> CARFSTONE_LAPIS_ORE =
            register(
                    "carfstone_lapis_ore",
                    () -> new DropExperienceBlock(UniformInt.of(2, 5), stone(6.0F)));
    public static final DeferredBlock<Block> HELLSTONE_ORE =
            register(
                    "hellstone_ore",
                    () -> new DropExperienceBlock(UniformInt.of(2, 5), stone(9.0F)));
    public static final DeferredBlock<Block> HELLSTONE_BLOCK =
            copy("hellstone_block", HELLSTONE_ORE);
    public static final DeferredBlock<Block> SHROOMSTONE_BLOCK =
            register("shroomstone_block", () -> new Block(stone(9.0F)));

    public static final DeferredBlock<LuminiteTorchBlock> LUMINITE_TORCH =
            registerWithoutItem(
                    "luminite_torch",
                    () ->
                            new LuminiteTorchBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH)
                                            .lightLevel(state -> 15)));
    public static final DeferredBlock<LuminiteWallTorchBlock> LUMINITE_WALL_TORCH =
            registerWithoutItem(
                    "luminite_wall_torch",
                    () ->
                            new LuminiteWallTorchBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH)
                                            .lightLevel(state -> 15)));
    public static final DeferredBlock<LanternBlock> LUMINITE_LANTERN =
            register(
                    "luminite_lantern",
                    () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN)));
    public static final DeferredBlock<CustomPlantBlock> GLIMMERGRASS =
            register(
                    "glimmergrass",
                    () ->
                            new CustomPlantBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)
                                            .lightLevel(state -> 9)
                                            .offsetType(BlockBehaviour.OffsetType.XZ),
                                    GLIMMERGRASS_BLOCK));
    public static final DeferredBlock<CustomPlantBlock> CHARRED_GRASS =
            register(
                    "charred_grass",
                    () ->
                            new CustomPlantBlock(
                                    BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS)
                                            .offsetType(BlockBehaviour.OffsetType.XZ),
                                    MOLTEN_CARFSTONE));

    private CustomBlocks() {}

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }

    private static BlockBehaviour.Properties stone(float strength) {
        return BlockBehaviour.Properties.of()
                .strength(strength, 7.0F)
                .requiresCorrectToolForDrops();
    }

    private static DeferredBlock<Block> copy(String name, Supplier<? extends Block> source) {
        return register(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(source.get())));
    }

    private static DeferredBlock<StairBlock> stairs(String name, Supplier<? extends Block> source) {
        return register(
                name,
                () ->
                        new StairBlock(
                                source.get().defaultBlockState(),
                                BlockBehaviour.Properties.ofFullCopy(source.get())));
    }

    private static DeferredBlock<SlabBlock> slab(String name, Supplier<? extends Block> source) {
        return register(
                name, () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(source.get())));
    }

    private static DeferredBlock<WallBlock> wall(String name, Supplier<? extends Block> source) {
        return register(
                name, () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(source.get())));
    }

    private static <T extends Block> DeferredBlock<T> register(
            String name, Supplier<? extends T> factory) {
        DeferredBlock<T> block = BLOCKS.register(name, factory);
        BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerWithoutItem(
            String name, Supplier<? extends T> factory) {
        return BLOCKS.register(name, factory);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> featureKey(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, DarkCaverns.id(path));
    }
}
