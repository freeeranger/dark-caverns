package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.registry.CustomBlockTags;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

final class DarkCavernsBlockTagsProvider extends BlockTagsProvider {
    DarkCavernsBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            String modId,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        addCommonTags();
        addMiningTags();
        addVanillaBehaviorTags();
        addModTags();
    }

    private void addCommonTags() {
        tag(common("cobblestone"))
                .add(CustomBlocks.CARFSTONE.get(), CustomBlocks.MOLTEN_CARFSTONE.get());
        tag(common("mushrooms")).add(CustomBlocks.GLIMMERSHROOM.get());
        tag(common("stone"))
                .add(
                        CustomBlocks.CARFSTONE.get(),
                        CustomBlocks.SMOOTH_CARFSTONE.get(),
                        CustomBlocks.MOLTEN_CARFSTONE.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get());

        TagKey<Block> ores = common("ores");
        String[] oreNames = {
            "coal",
            "iron",
            "gold",
            "diamond",
            "redstone",
            "lapis",
            "platinum",
            "luminite",
            "hellstone"
        };
        var oreRoot = tag(ores);
        for (String oreName : oreNames) {
            oreRoot.addTag(common("ores/" + oreName));
        }
        tag(common("ores/coal")).add(CustomBlocks.CARFSTONE_COAL_ORE.get());
        tag(common("ores/iron")).add(CustomBlocks.CARFSTONE_IRON_ORE.get());
        tag(common("ores/gold")).add(CustomBlocks.CARFSTONE_GOLD_ORE.get());
        tag(common("ores/diamond")).add(CustomBlocks.CARFSTONE_DIAMOND_ORE.get());
        tag(common("ores/redstone")).add(CustomBlocks.CARFSTONE_REDSTONE_ORE.get());
        tag(common("ores/lapis")).add(CustomBlocks.CARFSTONE_LAPIS_ORE.get());
        tag(common("ores/platinum")).add(CustomBlocks.PLATINUM_ORE.get());
        tag(common("ores/luminite")).add(CustomBlocks.LUMINITE_ORE.get());
        tag(common("ores/hellstone")).add(CustomBlocks.HELLSTONE_ORE.get());

        var storageRoot = tag(common("storage_blocks"));
        for (String material : new String[] {"platinum", "luminite", "hellstone", "shroomstone"}) {
            storageRoot.addTag(common("storage_blocks/" + material));
        }
        tag(common("storage_blocks/platinum")).add(CustomBlocks.PLATINUM_BLOCK.get());
        tag(common("storage_blocks/luminite")).add(CustomBlocks.LUMINITE_BLOCK.get());
        tag(common("storage_blocks/hellstone")).add(CustomBlocks.HELLSTONE_BLOCK.get());
        tag(common("storage_blocks/shroomstone")).add(CustomBlocks.SHROOMSTONE_BLOCK.get());
    }

    private void addMiningTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        CustomBlocks.CARFSTONE.get(),
                        CustomBlocks.SMOOTH_CARFSTONE.get(),
                        CustomBlocks.CARFSTONE_BRICKS.get(),
                        CustomBlocks.CARFSTONE_STAIRS.get(),
                        CustomBlocks.CARFSTONE_SLAB.get(),
                        CustomBlocks.CARFSTONE_WALL.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_STAIRS.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_SLAB.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_WALL.get(),
                        CustomBlocks.CARFSTONE_BRICK_STAIRS.get(),
                        CustomBlocks.CARFSTONE_BRICK_SLAB.get(),
                        CustomBlocks.CARFSTONE_BRICK_WALL.get(),
                        CustomBlocks.MOLTEN_CARFSTONE.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_STAIRS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_SLAB.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_WALL.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL.get(),
                        CustomBlocks.GLIMMERGRASS_BLOCK.get(),
                        CustomBlocks.LUMINITE_BLOCK.get(),
                        CustomBlocks.LUMINITE_ORE.get(),
                        CustomBlocks.PLATINUM_ORE.get(),
                        CustomBlocks.PLATINUM_BLOCK.get(),
                        CustomBlocks.CARFSTONE_COAL_ORE.get(),
                        CustomBlocks.CARFSTONE_IRON_ORE.get(),
                        CustomBlocks.CARFSTONE_GOLD_ORE.get(),
                        CustomBlocks.CARFSTONE_DIAMOND_ORE.get(),
                        CustomBlocks.CARFSTONE_REDSTONE_ORE.get(),
                        CustomBlocks.CARFSTONE_LAPIS_ORE.get(),
                        CustomBlocks.HELLSTONE_ORE.get(),
                        CustomBlocks.HELLSTONE_BLOCK.get(),
                        CustomBlocks.SHROOMSTONE_BLOCK.get(),
                        CustomBlocks.LUMINITE_LANTERN.get());
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        CustomBlocks.SCORCHED_BERRY_BUSH.get(),
                        CustomBlocks.GLIMMERSHROOM.get(),
                        CustomBlocks.GLIMMERSHROOM_BLOCK.get(),
                        CustomBlocks.GLIMMERGRASS.get(),
                        CustomBlocks.CHARRED_GRASS.get());

        tag(BlockTags.NEEDS_STONE_TOOL).add(CustomBlocks.CARFSTONE_IRON_ORE.get());
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(
                        CustomBlocks.LUMINITE_BLOCK.get(),
                        CustomBlocks.LUMINITE_ORE.get(),
                        CustomBlocks.CARFSTONE_GOLD_ORE.get(),
                        CustomBlocks.CARFSTONE_DIAMOND_ORE.get(),
                        CustomBlocks.CARFSTONE_REDSTONE_ORE.get(),
                        CustomBlocks.CARFSTONE_LAPIS_ORE.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(
                        CustomBlocks.PLATINUM_ORE.get(),
                        CustomBlocks.PLATINUM_BLOCK.get(),
                        CustomBlocks.HELLSTONE_ORE.get(),
                        CustomBlocks.HELLSTONE_BLOCK.get(),
                        CustomBlocks.SHROOMSTONE_BLOCK.get());
    }

    private void addVanillaBehaviorTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(
                        CustomBlocks.PLATINUM_BLOCK.get(),
                        CustomBlocks.HELLSTONE_BLOCK.get(),
                        CustomBlocks.SHROOMSTONE_BLOCK.get());
        tag(BlockTags.BEE_GROWABLES).add(CustomBlocks.SCORCHED_BERRY_BUSH.get());
        tag(BlockTags.GUARDED_BY_PIGLINS).add(CustomBlocks.CARFSTONE_GOLD_ORE.get());
        tag(BlockTags.MUSHROOM_GROW_BLOCK).add(CustomBlocks.GLIMMERGRASS_BLOCK.get());
        tag(BlockTags.WALL_POST_OVERRIDE).add(CustomBlocks.LUMINITE_TORCH.get());

        Block[] fireBlocks = {
            CustomBlocks.MOLTEN_CARFSTONE.get(),
            CustomBlocks.HELLSTONE_ORE.get(),
            CustomBlocks.HELLSTONE_BLOCK.get()
        };
        tag(BlockTags.INFINIBURN_OVERWORLD).add(fireBlocks);
        tag(BlockTags.INFINIBURN_NETHER).add(fireBlocks);
        tag(BlockTags.INFINIBURN_END).add(fireBlocks);

        Block[] immuneBlocks = {
            CustomBlocks.CRACKED_BEDROCK.get(),
            CustomBlocks.GATEWAY_TO_THE_CAVERNS.get(),
            CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get()
        };
        tag(BlockTags.DRAGON_IMMUNE).add(immuneBlocks);
        tag(BlockTags.WITHER_IMMUNE).add(immuneBlocks);

        tag(BlockTags.SLABS)
                .add(
                        CustomBlocks.CARFSTONE_SLAB.get(),
                        CustomBlocks.CARFSTONE_BRICK_SLAB.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_SLAB.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_SLAB.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB.get());
        tag(BlockTags.STAIRS)
                .add(
                        CustomBlocks.CARFSTONE_STAIRS.get(),
                        CustomBlocks.CARFSTONE_BRICK_STAIRS.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_STAIRS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_STAIRS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS.get());
        tag(BlockTags.WALLS)
                .add(
                        CustomBlocks.CARFSTONE_WALL.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_WALL.get(),
                        CustomBlocks.CARFSTONE_BRICK_WALL.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_WALL.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL.get());
    }

    private void addModTags() {
        tag(DarkCavernsTags.modBlock("dark_caverns_base_stone")).add(CustomBlocks.CARFSTONE.get());
        tag(DarkCavernsTags.modBlock("dark_caverns_base_carveable"))
                .add(
                        CustomBlocks.LUMINITE_ORE.get(),
                        CustomBlocks.CARFSTONE_COAL_ORE.get(),
                        CustomBlocks.CARFSTONE_IRON_ORE.get(),
                        CustomBlocks.CARFSTONE_GOLD_ORE.get(),
                        CustomBlocks.CARFSTONE_DIAMOND_ORE.get(),
                        CustomBlocks.CARFSTONE_REDSTONE_ORE.get(),
                        CustomBlocks.CARFSTONE_LAPIS_ORE.get(),
                        CustomBlocks.PLATINUM_ORE.get(),
                        CustomBlocks.CARFSTONE.get(),
                        CustomBlocks.GLIMMERGRASS_BLOCK.get(),
                        CustomBlocks.GLIMMERGRASS.get(),
                        CustomBlocks.GLIMMERSHROOM.get(),
                        CustomBlocks.MOLTEN_CARFSTONE.get(),
                        CustomBlocks.CHARRED_GRASS.get(),
                        CustomBlocks.SCORCHED_BERRY_BUSH.get());

        tag(CustomBlockTags.ROCKY_CREATURE_SPAWNABLE_ON)
                .add(
                        CustomBlocks.CARFSTONE.get(),
                        CustomBlocks.LUMINITE_ORE.get(),
                        CustomBlocks.PLATINUM_ORE.get(),
                        CustomBlocks.CARFSTONE_COAL_ORE.get(),
                        CustomBlocks.CARFSTONE_IRON_ORE.get(),
                        CustomBlocks.CARFSTONE_GOLD_ORE.get(),
                        CustomBlocks.CARFSTONE_DIAMOND_ORE.get(),
                        CustomBlocks.CARFSTONE_REDSTONE_ORE.get(),
                        CustomBlocks.CARFSTONE_LAPIS_ORE.get());
        tag(CustomBlockTags.MOLTEN_CREATURE_SPAWNABLE_ON)
                .add(
                        CustomBlocks.MOLTEN_CARFSTONE.get(),
                        CustomBlocks.HELLSTONE_ORE.get(),
                        Blocks.MAGMA_BLOCK);
        tag(CustomBlockTags.GLIMMERSHROOM_CREATURE_SPAWNABLE_ON)
                .add(CustomBlocks.GLIMMERGRASS_BLOCK.get());
    }

    private static TagKey<Block> common(String path) {
        return DarkCavernsTags.commonBlock(path);
    }
}
