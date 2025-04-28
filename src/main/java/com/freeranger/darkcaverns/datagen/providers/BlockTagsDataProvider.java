package com.freeranger.darkcaverns.datagen.providers;

import com.freeranger.darkcaverns.DarkCaverns;
import com.freeranger.darkcaverns.registries.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class BlockTagsDataProvider extends BlockTagsProvider {
    public BlockTagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, DarkCaverns.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(BlockRegistry.LUMINITE_ORE.get())
            .add(BlockRegistry.CARFSTONE.get())
            .add(BlockRegistry.CARFSTONE_SLAB.get())
            .add(BlockRegistry.CARFSTONE_STAIRS.get())
            .add(BlockRegistry.CARFSTONE_WALL.get())
            .add(BlockRegistry.CARFSTONE_BRICKS.get())
            .add(BlockRegistry.CARFSTONE_BRICK_SLAB.get())
            .add(BlockRegistry.CARFSTONE_BRICK_STAIRS.get())
            .add(BlockRegistry.CARFSTONE_BRICK_WALL.get())
            .add(BlockRegistry.CHISELED_CARFSTONE_BRICKS.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_SLAB.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_STAIRS.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_WALL.get())
            .add(BlockRegistry.ASHY_MOLTEN_CARFSTONE.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_BRICKS.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_SLAB.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_STAIRS.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_WALL.get())
            .add(BlockRegistry.HELLSTONE_ORE.get())
            .add(BlockRegistry.CARFSTONE_IRON_ORE.get())
            .add(BlockRegistry.CARFSTONE_GOLD_ORE.get())
            .add(BlockRegistry.CARFSTONE_DIAMOND_ORE.get())
            .add(BlockRegistry.CHISELED_MOLTEN_CARFSTONE_BRICKS.get())
            .add(BlockRegistry.OVERGROWN_CARFSTONE.get())
            .add(BlockRegistry.LUMINITE_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
            .add(BlockRegistry.TWISTWOOD_WOOD.get())
            .add(BlockRegistry.TWISTWOOD_DOOR.get())
            .add(BlockRegistry.TWISTWOOD_TRAPDOOR.get())
            .add(BlockRegistry.STRIPPED_TWISTWOOD_WOOD.get())
            .add(BlockRegistry.TWISTWOOD_LOG.get())
            .add(BlockRegistry.STRIPPED_TWISTWOOD_LOG.get())
            .add(BlockRegistry.TWISTWOOD_LOG.get());

        this.tag(BlockTags.LOGS_THAT_BURN)
            .add(BlockRegistry.TWISTWOOD_LOG.get())
            .add(BlockRegistry.STRIPPED_TWISTWOOD_LOG.get());

        this.tag(BlockTags.LOGS)
            .add(BlockRegistry.TWISTWOOD_LOG.get())
            .add(BlockRegistry.STRIPPED_TWISTWOOD_LOG.get());

        this.tag(BlockTags.LEAVES)
            .add(BlockRegistry.TWISTWOOD_LEAVES.get());

        this.tag(BlockTags.PLANKS)
            .add(BlockRegistry.TWISTWOOD_PLANKS.get());

        this.tag(BlockTags.DOORS)
            .add(BlockRegistry.TWISTWOOD_DOOR.get());

        this.tag(BlockTags.WOODEN_DOORS)
            .add(BlockRegistry.TWISTWOOD_DOOR.get());

        this.tag(BlockTags.TRAPDOORS)
            .add(BlockRegistry.TWISTWOOD_TRAPDOOR.get());

        this.tag(BlockTags.WOODEN_TRAPDOORS)
            .add(BlockRegistry.TWISTWOOD_TRAPDOOR.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
            .add(BlockRegistry.CARFSTONE_IRON_ORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
            .add(BlockRegistry.LUMINITE_ORE.get())
            .add(BlockRegistry.CARFSTONE_GOLD_ORE.get())
            .add(BlockRegistry.CARFSTONE_DIAMOND_ORE.get())
            .add(BlockRegistry.LUMINITE_BLOCK.get());

        this.tag(BlockTags.WALLS)
            .add(BlockRegistry.CARFSTONE_WALL.get())
            .add(BlockRegistry.CARFSTONE_BRICK_WALL.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_WALL.get())
            .add(BlockRegistry.MOLTEN_CARFSTONE_BRICK_WALL.get());
    }
}
