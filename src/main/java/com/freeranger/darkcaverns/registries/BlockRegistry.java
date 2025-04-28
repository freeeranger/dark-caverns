package com.freeranger.darkcaverns.registries;

import com.freeranger.darkcaverns.DarkCaverns;
import com.freeranger.darkcaverns.blocks.GrassBlockLikeBlock;
import com.freeranger.darkcaverns.blocks.GrassLikeBlock;
import com.freeranger.darkcaverns.blocks.StrippableBlock;
import com.freeranger.darkcaverns.blocks.ScorchedBerryBushBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DarkCaverns.MODID);
    
    //region Carfstone blocks
    private static final BlockBehaviour.Properties carfstoneProperties = BlockBehaviour.Properties.of()
        .destroyTime(2.0f)
        .explosionResistance(6.0f)
        .sound(SoundType.DEEPSLATE)
        .requiresCorrectToolForDrops();
    
    public static final DeferredBlock<Block> CARFSTONE = registerBlock(
        "carfstone", name -> new Block(carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_SLAB = registerBlock(
        "carfstone_slab", name -> new SlabBlock(carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_STAIRS = registerBlock(
        "carfstone_stairs", name -> new StairBlock(CARFSTONE.get().defaultBlockState(), carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_WALL = registerBlock(
        "carfstone_wall", name -> new WallBlock(carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CHISELED_CARFSTONE_BRICKS = registerBlock(
        "chiseled_carfstone_bricks", name -> new Block(carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_BRICKS = registerBlock(
        "carfstone_bricks", name -> new Block(carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_BRICK_SLAB = registerBlock(
        "carfstone_brick_slab", name -> new SlabBlock(carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_BRICK_STAIRS = registerBlock(
        "carfstone_brick_stairs", name -> new StairBlock(CARFSTONE_BRICKS.get().defaultBlockState(), carfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_BRICK_WALL = registerBlock(
        "carfstone_brick_wall", name -> new WallBlock(carfstoneProperties.setId(name))
    );
    //endregion
    
    //region Molten carfstone blocks
    private static final BlockBehaviour.Properties moltenCarfstoneProperties = BlockBehaviour.Properties.of()
        .destroyTime(2.0f)
        .explosionResistance(12.0f)
        .sound(SoundType.DEEPSLATE)
        .requiresCorrectToolForDrops();
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE = registerBlock(
        "molten_carfstone",
        name -> new GrassBlockLikeBlock(moltenCarfstoneProperties.setId(name), "patch_small_charred_grass_placed")
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_SLAB = registerBlock(
        "molten_carfstone_slab", name -> new SlabBlock(moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_STAIRS = registerBlock(
        "molten_carfstone_stairs",
        name -> new StairBlock(MOLTEN_CARFSTONE.get().defaultBlockState(), moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_WALL = registerBlock(
        "molten_carfstone_wall", name -> new WallBlock(moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CHISELED_MOLTEN_CARFSTONE_BRICKS = registerBlock(
        "chiseled_molten_carfstone_bricks", name -> new Block(moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_BRICKS = registerBlock(
        "molten_carfstone_bricks", name -> new Block(moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_BRICK_SLAB = registerBlock(
        "molten_carfstone_brick_slab", name -> new SlabBlock(moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_BRICK_STAIRS = registerBlock(
        "molten_carfstone_brick_stairs",
        name -> new StairBlock(MOLTEN_CARFSTONE_BRICKS.get().defaultBlockState(), moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> MOLTEN_CARFSTONE_BRICK_WALL = registerBlock(
        "molten_carfstone_brick_wall",
        name -> new WallBlock(moltenCarfstoneProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> CHARRED_GRASS = registerBlock(
        "charred_grass",
        name -> new GrassLikeBlock(BlockBehaviour.Properties.of().setId(name), MOLTEN_CARFSTONE.get(), true),
        true
    );
    
    public static final DeferredBlock<Block> ASHY_MOLTEN_CARFSTONE = registerBlock(
        "ashy_molten_carfstone",
        name -> new GrassBlockLikeBlock(moltenCarfstoneProperties
            .randomTicks()
            .setId(name)
            .sound(new SoundType(
                1f,
                1f,
                SoundEvents.DEEPSLATE_BREAK,
                SoundEvents.GRAVEL_STEP,
                SoundEvents.DEEPSLATE_PLACE,
                SoundEvents.DEEPSLATE_HIT,
                SoundEvents.GRAVEL_FALL
            )),
            "patch_small_ashy_charred_grass_placed"
        )
    );
    
    public static final DeferredBlock<Block> ASHY_CHARRED_GRASS = registerBlock(
        "ashy_charred_grass",
        name -> new GrassLikeBlock(BlockBehaviour.Properties.of().setId(name), ASHY_MOLTEN_CARFSTONE.get(), true),
        true
    );
    
    public static final DeferredBlock<Block> SCORCHED_BERRY_BUSH = registerBlock(
        "scorched_berry_bush",
        registryName -> new ScorchedBerryBushBlock(
            BlockBehaviour.Properties.of()
                .instabreak()
                .sound(SoundType.SWEET_BERRY_BUSH)
                .lightLevel(state -> state.getValue(ScorchedBerryBushBlock.AGE) >= 2 ? 7 : 0)
                .noOcclusion()
                .noCollission()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
                .setId(registryName)
        ),
        false
    );
    //endregion
    
    //region Ore and valuable blocks
    public static final DeferredBlock<Block> CARFSTONE_IRON_ORE = registerBlock(
        "carfstone_iron_ore",
        name -> new Block(carfstoneProperties.setId(name).strength(4f))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_GOLD_ORE = registerBlock(
        "carfstone_gold_ore",
        name -> new Block(carfstoneProperties.setId(name).strength(4f))
    );
    
    public static final DeferredBlock<Block> CARFSTONE_DIAMOND_ORE = registerBlock(
        "carfstone_diamond_ore",
        name -> new DropExperienceBlock(UniformInt.of(3, 7), carfstoneProperties.setId(name).strength(4f))
    );
    
    public static final DeferredBlock<Block> HELLSTONE_ORE = registerBlock(
        "hellstone_ore",
        name -> new DropExperienceBlock(
            UniformInt.of(5, 9),
            moltenCarfstoneProperties.setId(name).strength(5f).lightLevel(state -> 7)
        )
    );
    
    public static final DeferredBlock<Block> LUMINITE_ORE = registerBlock(
        "luminite_ore",
        name -> new DropExperienceBlock(
            UniformInt.of(2, 4),
            BlockBehaviour.Properties.of()
                .destroyTime(2.5f)
                .explosionResistance(6.0f)
                .sound(SoundType.DEEPSLATE)
                .lightLevel(value -> 15)
                .requiresCorrectToolForDrops()
                .setId(name)
        )
    );
    
    public static final DeferredBlock<Block> LUMINITE_BLOCK = registerBlock(
        "luminite_block",
        name -> new Block(
            BlockBehaviour.Properties.of()
                .destroyTime(2.5f)
                .explosionResistance(6.0f)
                .sound(SoundType.METAL)
                .lightLevel(value -> 15)
                .requiresCorrectToolForDrops()
                .setId(name)
        )
    );
    //endregion
    
    //region Tangled hallow blocks
    public static final DeferredBlock<Block> OVERGROWN_CARFSTONE = registerBlock(
        "overgrown_carfstone",
        name -> new GrassBlockLikeBlock(carfstoneProperties
            .randomTicks()
            .setId(name)
            .sound(new SoundType(
                1f,
                1f,
                SoundEvents.DEEPSLATE_BREAK,
                SoundEvents.GRASS_STEP,
                SoundEvents.DEEPSLATE_PLACE,
                SoundEvents.DEEPSLATE_HIT,
                SoundEvents.GRASS_FALL
            )),
            "patch_small_undersprouts_placed"
        )
    );
    
    public static final DeferredBlock<Block> UNDERSPROUTS = registerBlock(
        "undersprouts",
        name -> new GrassLikeBlock(BlockBehaviour.Properties.of().setId(name), OVERGROWN_CARFSTONE.get(), true),
        true
    );
    //endregion
    
    //region Twistwood blocks
    private static final BlockBehaviour.Properties twistwoodProperties = BlockBehaviour.Properties.of()
        .strength(2f, 3f)
        .sound(SoundType.WOOD)
        .ignitedByLava();
    
    public static final DeferredBlock<Block> STRIPPED_TWISTWOOD_WOOD = registerBlock(
        "stripped_twistwood_wood", name -> new RotatedPillarBlock(twistwoodProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> TWISTWOOD_WOOD = registerBlock(
        "twistwood_wood", name -> new StrippableBlock(twistwoodProperties.setId(name), STRIPPED_TWISTWOOD_WOOD.get())
    );
    
    public static final DeferredBlock<Block> STRIPPED_TWISTWOOD_LOG = registerBlock(
        "stripped_twistwood_log", name -> new RotatedPillarBlock(twistwoodProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> TWISTWOOD_LOG = registerBlock(
        "twistwood_log", name -> new StrippableBlock(twistwoodProperties.setId(name), STRIPPED_TWISTWOOD_LOG.get())
    );
    
    public static final DeferredBlock<Block> TWISTWOOD_PLANKS = registerBlock(
        "twistwood_planks", name -> new Block(twistwoodProperties.setId(name))
    );
    
    public static final DeferredBlock<Block> TWISTWOOD_DOOR = registerBlock(
        "twistwood_door",
        name -> new DoorBlock(BlockSetType.OAK, twistwoodProperties
            .noOcclusion()
            .isSuffocating((state, getter, pos) -> false)
            .isViewBlocking((state, getter, pos) -> false)
            .isRedstoneConductor((state, getter, pos) -> false)
            .setId(name))
    );
    
    public static final DeferredBlock<Block> TWISTWOOD_TRAPDOOR = registerBlock(
        "twistwood_trapdoor",
        name -> new TrapDoorBlock(BlockSetType.OAK, twistwoodProperties
            .noOcclusion()
            .isSuffocating((state, getter, pos) -> false)
            .isViewBlocking((state, getter, pos) -> false)
            .isRedstoneConductor((state, getter, pos) -> false)
            .setId(name))
    );
    
    public static final DeferredBlock<Block> TWISTWOOD_LEAVES = registerBlock(
        "twistwood_leaves",
        name -> new Block(BlockBehaviour.Properties.of()
            .strength(0.2f)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isSuffocating((state, getter, pos) -> false)
            .isViewBlocking((state, getter, pos) -> false)
            .isRedstoneConductor((state, getter, pos) -> false)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
            .setId(name)
        )
    );
    //endregion
    
    private static <T extends Block> DeferredBlock<Block> registerBlock(String name, Function<ResourceKey<Block>, T> blockFactory) {
        return registerBlock(name, blockFactory, true);
    }
    
    private static <T extends Block> DeferredBlock<Block> registerBlock(String name, Function<ResourceKey<Block>, T> blockFactory, boolean needsItem) {
        DeferredBlock<Block> newBlock = BLOCKS.register(name, registryName ->
            blockFactory.apply(ResourceKey.create(Registries.BLOCK, registryName))
        );
        
        if(needsItem) ItemRegistry.ITEMS.registerSimpleBlockItem(newBlock);
        
        return newBlock;
    }
    
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
