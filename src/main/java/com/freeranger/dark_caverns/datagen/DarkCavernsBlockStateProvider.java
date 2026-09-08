package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.List;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

final class DarkCavernsBlockStateProvider extends BlockStateProvider {
    private static final ResourceLocation CUTOUT = ResourceLocation.withDefaultNamespace("cutout");

    DarkCavernsBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DarkCaverns.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerSimpleBlocks();
        registerStoneFamilies();
        registerPlants();
        registerGlimmershroomBlock();
        registerLighting();
        registerGateways();
    }

    private void registerSimpleBlocks() {
        List.of(
                        CustomBlocks.CARFSTONE.get(),
                        CustomBlocks.SMOOTH_CARFSTONE.get(),
                        CustomBlocks.CARFSTONE_BRICKS.get(),
                        CustomBlocks.CRACKED_BEDROCK.get(),
                        CustomBlocks.MOLTEN_CARFSTONE.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get(),
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
                        CustomBlocks.SHROOMSTONE_BLOCK.get())
                .forEach(this::simpleCubeWithItem);

        Block glimmergrass = CustomBlocks.GLIMMERGRASS_BLOCK.get();
        ModelFile glimmergrassModel =
                models().cubeBottomTop(
                                name(glimmergrass),
                                modLoc("block/glimmergrass_block_side"),
                                modLoc("block/carfstone"),
                                modLoc("block/glimmergrass_block_top"));
        simpleBlock(glimmergrass, ConfiguredModel.allYRotations(glimmergrassModel, 0, false));
        simpleBlockItem(glimmergrass, glimmergrassModel);
    }

    private void registerStoneFamilies() {
        registerStoneFamily(
                CustomBlocks.CARFSTONE.get(),
                CustomBlocks.CARFSTONE_STAIRS.get(),
                CustomBlocks.CARFSTONE_SLAB.get(),
                CustomBlocks.CARFSTONE_WALL.get());
        registerStoneFamily(
                CustomBlocks.SMOOTH_CARFSTONE.get(),
                CustomBlocks.SMOOTH_CARFSTONE_STAIRS.get(),
                CustomBlocks.SMOOTH_CARFSTONE_SLAB.get(),
                CustomBlocks.SMOOTH_CARFSTONE_WALL.get());
        registerStoneFamily(
                CustomBlocks.CARFSTONE_BRICKS.get(),
                CustomBlocks.CARFSTONE_BRICK_STAIRS.get(),
                CustomBlocks.CARFSTONE_BRICK_SLAB.get(),
                CustomBlocks.CARFSTONE_BRICK_WALL.get());
        registerStoneFamily(
                CustomBlocks.MOLTEN_CARFSTONE.get(),
                CustomBlocks.MOLTEN_CARFSTONE_STAIRS.get(),
                CustomBlocks.MOLTEN_CARFSTONE_SLAB.get(),
                CustomBlocks.MOLTEN_CARFSTONE_WALL.get());
        registerStoneFamily(
                CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get(),
                CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS.get(),
                CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB.get(),
                CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL.get());
        registerStoneFamily(
                CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get(),
                CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS.get(),
                CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB.get(),
                CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL.get());
    }

    private void registerStoneFamily(
            Block base, StairBlock stairs, SlabBlock slab, WallBlock wall) {
        ResourceLocation texture = blockTexture(base);
        stairsBlock(stairs, texture);
        simpleBlockItem(stairs, existingBlockModel(stairs));

        slabBlock(slab, blockModel(base), texture);
        simpleBlockItem(slab, existingBlockModel(slab));

        wallBlock(wall, texture);
        ModelFile wallInventory = models().wallInventory(name(wall) + "_inventory", texture);
        simpleBlockItem(wall, wallInventory);
    }

    private void registerPlants() {
        crossBlock(CustomBlocks.GLIMMERSHROOM.get());
        crossBlock(CustomBlocks.GLIMMERGRASS.get());
        crossBlock(CustomBlocks.CHARRED_GRASS.get());

        Block bush = CustomBlocks.SCORCHED_BERRY_BUSH.get();
        getVariantBuilder(bush)
                .forAllStates(
                        state -> {
                            int age = state.getValue(BlockStateProperties.AGE_3);
                            ModelFile model =
                                    models().cross(
                                                    name(bush) + "_stage" + age,
                                                    modLoc("block/scorched_berry_bush_stage" + age))
                                            .renderType(CUTOUT);
                            return ConfiguredModel.builder().modelFile(model).build();
                        });
    }

    private void registerGlimmershroomBlock() {
        Block block = CustomBlocks.GLIMMERSHROOM_BLOCK.get();
        ModelFile outside =
                models().singleTexture(
                                name(block),
                                mcLoc("block/template_single_face"),
                                modLoc("block/glimmershroom_block"));
        ModelFile inside =
                models().singleTexture(
                                name(block) + "_inside",
                                mcLoc("block/template_single_face"),
                                modLoc("block/glimmershroom_block_inside"))
                        .ao(false);
        ModelFile inventory =
                models().cubeAll(name(block) + "_inventory", modLoc("block/glimmershroom_block"));

        var multipart = getMultipartBuilder(block);
        mushroomFace(multipart, outside, HugeMushroomBlock.NORTH, 0, 0, true);
        mushroomFace(multipart, outside, HugeMushroomBlock.EAST, 0, 90, true);
        mushroomFace(multipart, outside, HugeMushroomBlock.SOUTH, 0, 180, true);
        mushroomFace(multipart, outside, HugeMushroomBlock.WEST, 0, 270, true);
        mushroomFace(multipart, outside, HugeMushroomBlock.UP, 270, 0, true);
        mushroomFace(multipart, outside, HugeMushroomBlock.DOWN, 90, 0, true);
        mushroomFace(multipart, inside, HugeMushroomBlock.NORTH, 0, 0, false);
        mushroomFace(multipart, inside, HugeMushroomBlock.EAST, 0, 90, false);
        mushroomFace(multipart, inside, HugeMushroomBlock.SOUTH, 0, 180, false);
        mushroomFace(multipart, inside, HugeMushroomBlock.WEST, 0, 270, false);
        mushroomFace(multipart, inside, HugeMushroomBlock.UP, 270, 0, false);
        mushroomFace(multipart, inside, HugeMushroomBlock.DOWN, 90, 0, false);
        simpleBlockItem(block, inventory);
    }

    private void mushroomFace(
            net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder multipart,
            ModelFile model,
            net.minecraft.world.level.block.state.properties.BooleanProperty property,
            int rotationX,
            int rotationY,
            boolean exposed) {
        multipart
                .part()
                .modelFile(model)
                .rotationX(rotationX)
                .rotationY(rotationY)
                .uvLock(exposed && (rotationX != 0 || rotationY != 0))
                .addModel()
                .condition(property, exposed);
    }

    private void registerLighting() {
        Block torch = CustomBlocks.LUMINITE_TORCH.get();
        ModelFile torchModel =
                models().torch(name(torch), modLoc("block/luminite_torch")).renderType(CUTOUT);
        simpleBlock(torch, torchModel);

        Block wallTorch = CustomBlocks.LUMINITE_WALL_TORCH.get();
        ModelFile wallTorchModel =
                models().torchWall(name(wallTorch), modLoc("block/luminite_torch"))
                        .renderType(CUTOUT);
        horizontalBlock(wallTorch, wallTorchModel, 90);

        LanternBlock lantern = CustomBlocks.LUMINITE_LANTERN.get();
        ModelFile standing =
                models().singleTexture(
                                name(lantern),
                                mcLoc("block/template_lantern"),
                                "lantern",
                                modLoc("block/luminite_lantern"))
                        .renderType(CUTOUT);
        ModelFile hanging =
                models().singleTexture(
                                name(lantern) + "_hanging",
                                mcLoc("block/template_hanging_lantern"),
                                "lantern",
                                modLoc("block/luminite_lantern"))
                        .renderType(CUTOUT);
        getVariantBuilder(lantern)
                .partialState()
                .with(LanternBlock.HANGING, false)
                .modelForState()
                .modelFile(standing)
                .addModel()
                .partialState()
                .with(LanternBlock.HANGING, true)
                .modelForState()
                .modelFile(hanging)
                .addModel();
    }

    private void registerGateways() {
        Block overworldGateway = CustomBlocks.GATEWAY_TO_THE_CAVERNS.get();
        ModelFile overworldModel =
                models().cubeBottomTop(
                                name(overworldGateway),
                                modLoc("block/cracked_bedrock"),
                                modLoc("block/gateway_to_the_caverns"),
                                modLoc("block/gateway_to_the_caverns"));
        simpleBlockWithItem(overworldGateway, overworldModel);

        Block cavernsGateway = CustomBlocks.GATEWAY_TO_THE_OVERWORLD.get();
        ResourceLocation bedrock = modLoc("block/cracked_bedrock");
        ModelFile cavernsModel =
                models().cube(
                                name(cavernsGateway),
                                modLoc("block/gateway_to_the_caverns"),
                                bedrock,
                                bedrock,
                                bedrock,
                                bedrock,
                                bedrock)
                        .texture("particle", modLoc("block/gateway_to_the_caverns"));
        simpleBlockWithItem(cavernsGateway, cavernsModel);
    }

    private void crossBlock(Block block) {
        ModelFile model = models().cross(name(block), blockTexture(block)).renderType(CUTOUT);
        simpleBlock(block, model);
    }

    private void simpleCubeWithItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlockWithItem(block, model);
    }

    private ModelFile existingBlockModel(Block block) {
        return models().getExistingFile(blockModel(block));
    }

    private ResourceLocation blockModel(Block block) {
        return modLoc("block/" + name(block));
    }

    private String name(Block block) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
