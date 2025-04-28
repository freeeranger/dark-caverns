package com.freeranger.darkcaverns.datagen.providers;

import com.freeranger.darkcaverns.DarkCaverns;
import com.freeranger.darkcaverns.registries.BlockRegistry;
import com.freeranger.darkcaverns.registries.ItemRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModelDataProvider extends ModelProvider {
    public ModelDataProvider(PackOutput output) {
        super(output, DarkCaverns.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // items
        basicItem(itemModels, ItemRegistry.LUMINITE_DUST.get());
        basicItem(itemModels, ItemRegistry.HELLSTONE.get());
        basicItem(itemModels, ItemRegistry.SCORCHED_BERRIES.get());

        // blocks
        blockWithSlabStairWallVariants(blockModels, BlockRegistry.CARFSTONE.get(), BlockRegistry.CARFSTONE_SLAB.get(), BlockRegistry.CARFSTONE_STAIRS.get(), BlockRegistry.CARFSTONE_WALL.get());
        blockWithSlabStairWallVariants(blockModels, BlockRegistry.CARFSTONE_BRICKS.get(), BlockRegistry.CARFSTONE_BRICK_SLAB.get(), BlockRegistry.CARFSTONE_BRICK_STAIRS.get(), BlockRegistry.CARFSTONE_BRICK_WALL.get());
        basicBlock(blockModels, BlockRegistry.CHISELED_CARFSTONE_BRICKS.get());

        blockWithSlabStairWallVariants(blockModels, BlockRegistry.MOLTEN_CARFSTONE.get(), BlockRegistry.MOLTEN_CARFSTONE_SLAB.get(), BlockRegistry.MOLTEN_CARFSTONE_STAIRS.get(), BlockRegistry.MOLTEN_CARFSTONE_WALL.get());
        blockWithSlabStairWallVariants(blockModels, BlockRegistry.MOLTEN_CARFSTONE_BRICKS.get(), BlockRegistry.MOLTEN_CARFSTONE_BRICK_SLAB.get(), BlockRegistry.MOLTEN_CARFSTONE_BRICK_STAIRS.get(), BlockRegistry.MOLTEN_CARFSTONE_BRICK_WALL.get());
        basicBlock(blockModels, BlockRegistry.CHISELED_MOLTEN_CARFSTONE_BRICKS.get());

        basicBlock(blockModels, BlockRegistry.CARFSTONE_IRON_ORE.get());
        basicBlock(blockModels, BlockRegistry.CARFSTONE_GOLD_ORE.get());
        basicBlock(blockModels, BlockRegistry.CARFSTONE_DIAMOND_ORE.get());
        basicBlock(blockModels, BlockRegistry.HELLSTONE_ORE.get());
        basicBlock(blockModels, BlockRegistry.LUMINITE_ORE.get());
        basicBlock(blockModels, BlockRegistry.LUMINITE_BLOCK.get());
        basicBlock(blockModels, BlockRegistry.TWISTWOOD_PLANKS.get());
        basicBlock(blockModels, BlockRegistry.TWISTWOOD_LEAVES.get());

        blockModels.createDoor(BlockRegistry.TWISTWOOD_DOOR.get());
        blockModels.createTrapdoor(BlockRegistry.TWISTWOOD_TRAPDOOR.get());

        topSideBottomBlock(blockModels, BlockRegistry.ASHY_MOLTEN_CARFSTONE.get(), "block/ashy_molten_carfstone_top", "block/ashy_molten_carfstone_side", "block/molten_carfstone");
        topSideBottomBlock(blockModels, BlockRegistry.OVERGROWN_CARFSTONE.get(), "block/overgrown_carfstone_top", "block/overgrown_carfstone_side", "block/carfstone");
        blockModels.woodProvider(BlockRegistry.TWISTWOOD_LOG.get()).logWithHorizontal(BlockRegistry.TWISTWOOD_LOG.value()).wood(BlockRegistry.TWISTWOOD_WOOD.get());
        blockModels.woodProvider(BlockRegistry.STRIPPED_TWISTWOOD_LOG.get()).logWithHorizontal(BlockRegistry.STRIPPED_TWISTWOOD_LOG.value()).wood(BlockRegistry.STRIPPED_TWISTWOOD_WOOD.get());

        crossBlock(blockModels, BlockRegistry.UNDERSPROUTS.get());
        crossBlock(blockModels, BlockRegistry.CHARRED_GRASS.get());
        crossBlock(blockModels, BlockRegistry.ASHY_CHARRED_GRASS.get());

        berryBushBlock(blockModels, BlockRegistry.SCORCHED_BERRY_BUSH.get());
    }

    private void basicItem(ItemModelGenerators itemModels, Item item) {
        itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private void basicBlock(BlockModelGenerators blockModels, Block block) {
        blockModels.createTrivialCube(block);
    }

    private void stairsBlock(BlockModelGenerators blockModels, Block block, Block materialBlock) {
        ResourceLocation texture = blockLocation(materialBlock);
        blockModels.new BlockFamilyProvider(TextureMapping.defaultTexture(texture)
            .put(TextureSlot.BOTTOM, texture)
            .put(TextureSlot.TOP, texture)
            .put(TextureSlot.SIDE, texture)
        ).stairs(block);
    }

    private void wallBlock(BlockModelGenerators blockModels, Block block, Block materialBlock) {
        blockModels.new BlockFamilyProvider(TextureMapping.columnWithWall(materialBlock)).wall(block);
    }

    private void blockWithSlab(BlockModelGenerators blockModels, Block block, Block slab) {
        ResourceLocation texture = blockLocation(block);
        blockModels.new BlockFamilyProvider(TextureMapping.cube(texture)
            .put(TextureSlot.BOTTOM, texture)
            .put(TextureSlot.TOP, texture)
            .put(TextureSlot.SIDE, texture)
        ).fullBlock(block, ModelTemplates.CUBE_ALL).slab(slab);
    }

    private void blockWithSlabStairVariants(BlockModelGenerators blockModels, Block block, Block slab, Block stairs) {
        blockWithSlab(blockModels, block, slab);
        stairsBlock(blockModels, stairs, block);
    }

    private void blockWithSlabStairWallVariants(BlockModelGenerators blockModels, Block block, Block slab, Block stairs, Block wall) {
        blockWithSlabStairVariants(blockModels, block, slab, stairs);
        wallBlock(blockModels, wall, block);
    }

    private void crossBlock(BlockModelGenerators blockModels, Block block) {
        blockModels.createTrivialBlock(block, TexturedModel.createDefault(
            thisBlock -> new TextureMapping().put(TextureSlot.CROSS, TextureMapping.getBlockTexture(thisBlock)),
            ModelTemplates.CROSS.extend().renderType("minecraft:cutout").requiredTextureSlot(TextureSlot.CROSS).build()
        ));
    }

    private void topSideBottomBlock(BlockModelGenerators blockModels, Block block, String top, String side, String bottom) {
        blockModels.createTrivialBlock(
            block,
            TexturedModel.CUBE_TOP_BOTTOM.updateTexture(mapping ->
                mapping.put(TextureSlot.SIDE, this.modLocation(side))
                    .put(TextureSlot.TOP, this.modLocation(top))
                    .put(TextureSlot.BOTTOM, this.modLocation(bottom))
            )
        );
    }

    private void berryBushBlock(BlockModelGenerators blockModels, Block block) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch
            .initial(BlockStateProperties.AGE_3)
            .generate((stage) -> plainVariant(blockModels.createSuffixedVariant(
                BlockRegistry.SCORCHED_BERRY_BUSH.get(),
                "_stage" + stage,
                ModelTemplates.CROSS.extend().renderType("minecraft:cutout").build(),
                TextureMapping::cross)
            ))
        ));
    }

    private ResourceLocation blockLocation(Block block) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
        return ResourceLocation.fromNamespaceAndPath(DarkCaverns.MODID, "block/" + location.getPath());
    }
}
