package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModel.GuiLight;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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
        registerTwistwood();
    }

    private void registerSimpleBlocks() {
        List.of(
                        CustomBlocks.CARFSTONE.get(),
                        CustomBlocks.SMOOTH_CARFSTONE.get(),
                        CustomBlocks.CARFSTONE_BRICKS.get(),
                        CustomBlocks.CHISELED_CARFSTONE_BRICKS.get(),
                        CustomBlocks.CRACKED_BEDROCK.get(),
                        CustomBlocks.MOLTEN_CARFSTONE.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get(),
                        CustomBlocks.CHISELED_MOLTEN_CARFSTONE_BRICKS.get(),
                        CustomBlocks.LUMINITE_BLOCK.get(),
                        CustomBlocks.PLATINUM_ORE.get(),
                        CustomBlocks.PLATINUM_BLOCK.get(),
                        CustomBlocks.CARFSTONE_COAL_ORE.get(),
                        CustomBlocks.CARFSTONE_IRON_ORE.get(),
                        CustomBlocks.CARFSTONE_COPPER_ORE.get(),
                        CustomBlocks.CARFSTONE_GOLD_ORE.get(),
                        CustomBlocks.CARFSTONE_DIAMOND_ORE.get(),
                        CustomBlocks.CARFSTONE_REDSTONE_ORE.get(),
                        CustomBlocks.CARFSTONE_LAPIS_ORE.get(),
                        CustomBlocks.HELLSTONE_ORE.get(),
                        CustomBlocks.HELLSTONE_BLOCK.get(),
                        CustomBlocks.SHROOMSTONE_BLOCK.get())
                .forEach(this::simpleCubeWithItem);

        registerLuminiteOre();

        Block ashyGround = CustomBlocks.ASHY_MOLTEN_CARFSTONE.get();
        simpleBlockWithItem(
                ashyGround,
                models().cubeBottomTop(
                                name(ashyGround),
                                modLoc("block/ashy_molten_carfstone_side"),
                                modLoc("block/molten_carfstone"),
                                modLoc("block/ashy_molten_carfstone_top")));

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

    private void registerTwistwood() {
        Block ground = CustomBlocks.OVERGROWN_CARFSTONE.get();
        simpleBlockWithItem(
                ground,
                models().cubeBottomTop(
                                name(ground),
                                modLoc("block/overgrown_carfstone_side"),
                                modLoc("block/carfstone"),
                                modLoc("block/overgrown_carfstone_top")));
        for (var log :
                List.of(
                        CustomBlocks.TWISTWOOD_LOG.get(),
                        CustomBlocks.STRIPPED_TWISTWOOD_LOG.get())) {
            logBlock(log);
            simpleBlockItem(log, existingBlockModel(log));
        }
        axisBlock(
                CustomBlocks.TWISTWOOD_WOOD.get(),
                modLoc("block/twistwood_log"),
                modLoc("block/twistwood_log"));
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_WOOD.get(),
                existingBlockModel(CustomBlocks.TWISTWOOD_WOOD.get()));
        axisBlock(
                CustomBlocks.STRIPPED_TWISTWOOD_WOOD.get(),
                modLoc("block/stripped_twistwood_log"),
                modLoc("block/stripped_twistwood_log"));
        simpleBlockItem(
                CustomBlocks.STRIPPED_TWISTWOOD_WOOD.get(),
                existingBlockModel(CustomBlocks.STRIPPED_TWISTWOOD_WOOD.get()));
        simpleCubeWithItem(CustomBlocks.TWISTWOOD_PLANKS.get());
        ResourceLocation twistwoodPlanksTexture = blockTexture(CustomBlocks.TWISTWOOD_PLANKS.get());
        stairsBlock(CustomBlocks.TWISTWOOD_STAIRS.get(), twistwoodPlanksTexture);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_STAIRS.get(),
                existingBlockModel(CustomBlocks.TWISTWOOD_STAIRS.get()));
        slabBlock(
                CustomBlocks.TWISTWOOD_SLAB.get(),
                blockModel(CustomBlocks.TWISTWOOD_PLANKS.get()),
                twistwoodPlanksTexture);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_SLAB.get(),
                existingBlockModel(CustomBlocks.TWISTWOOD_SLAB.get()));
        fenceBlock(CustomBlocks.TWISTWOOD_FENCE.get(), twistwoodPlanksTexture);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_FENCE.get(),
                models().fenceInventory(
                                name(CustomBlocks.TWISTWOOD_FENCE.get()) + "_inventory",
                                twistwoodPlanksTexture));
        fenceGateBlock(CustomBlocks.TWISTWOOD_FENCE_GATE.get(), twistwoodPlanksTexture);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_FENCE_GATE.get(),
                existingBlockModel(CustomBlocks.TWISTWOOD_FENCE_GATE.get()));
        pressurePlateBlock(CustomBlocks.TWISTWOOD_PRESSURE_PLATE.get(), twistwoodPlanksTexture);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_PRESSURE_PLATE.get(),
                existingBlockModel(CustomBlocks.TWISTWOOD_PRESSURE_PLATE.get()));
        buttonBlock(CustomBlocks.TWISTWOOD_BUTTON.get(), twistwoodPlanksTexture);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_BUTTON.get(),
                models().buttonInventory(
                                name(CustomBlocks.TWISTWOOD_BUTTON.get()) + "_inventory",
                                twistwoodPlanksTexture));
        simpleBlockWithItem(
                CustomBlocks.TWISTWOOD_LEAVES.get(),
                models().cubeAll("twistwood_leaves", modLoc("block/twistwood_leaves"))
                        .renderType(CUTOUT));
        doorBlockWithRenderType(
                CustomBlocks.TWISTWOOD_DOOR.get(),
                modLoc("block/twistwood_door_bottom"),
                modLoc("block/twistwood_door_top"),
                CUTOUT);
        trapdoorBlockWithRenderType(
                CustomBlocks.TWISTWOOD_TRAPDOOR.get(),
                modLoc("block/twistwood_trapdoor"),
                true,
                CUTOUT);
        simpleBlockItem(
                CustomBlocks.TWISTWOOD_TRAPDOOR.get(),
                models().getExistingFile(modLoc("block/twistwood_trapdoor_bottom")));
        signBlock(
                CustomBlocks.TWISTWOOD_SIGN.get(),
                CustomBlocks.TWISTWOOD_WALL_SIGN.get(),
                twistwoodPlanksTexture);
        hangingSignBlock(
                CustomBlocks.TWISTWOOD_HANGING_SIGN.get(),
                CustomBlocks.TWISTWOOD_WALL_HANGING_SIGN.get(),
                twistwoodPlanksTexture);
        crossBlock(CustomBlocks.UNDERSPROUTS.get());
        doublePlantBlock(CustomBlocks.TALL_UNDERSPROUTS.get());
        mightyUndersproutsBlock(CustomBlocks.MIGHTY_UNDERSPROUTS.get());
        mightyUndersproutsItem();
        crossBlock(CustomBlocks.TWISTWOOD_SAPLING.get());
        ModelFile pottedTwistwoodSapling =
                models().withExistingParent(
                                "potted_twistwood_sapling", mcLoc("block/flower_pot_cross"))
                        .texture("plant", modLoc("block/twistwood_sapling"))
                        .renderType(CUTOUT);
        simpleBlock(CustomBlocks.POTTED_TWISTWOOD_SAPLING.get(), pottedTwistwoodSapling);
        for (String plant : List.of("undersprouts", "tall_undersprouts", "twistwood_sapling"))
            itemModels()
                    .withExistingParent(plant, mcLoc("item/generated"))
                    .texture("layer0", modLoc("block/" + plant));
        Block waterSproutlets = CustomBlocks.WATER_SPROUTLETS.get();
        ModelFile waterSproutletsModel =
                models().withExistingParent(name(waterSproutlets), mcLoc("block/lily_pad"))
                        .texture("particle", modLoc("block/water_sproutlets"))
                        .texture("texture", modLoc("block/water_sproutlets"))
                        .renderType(CUTOUT);
        simpleBlock(waterSproutlets, ConfiguredModel.allYRotations(waterSproutletsModel, 0, false));
        itemModels()
                .withExistingParent("water_sproutlets", mcLoc("item/generated"))
                .texture("layer0", modLoc("block/water_sproutlets"));
        itemModels()
                .withExistingParent("twistwood_door", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/twistwood_door"));
    }

    private void mightyUndersproutsBlock(Block block) {
        var builder =
                models().getBuilder(name(block))
                        .ao(false)
                        .renderType(CUTOUT)
                        .texture("particle", modLoc("block/mighty_undersprouts"))
                        .texture("texture", modLoc("block/mighty_undersprouts"));

        builder.element()
                .from(-16.0F, 0.0F, 8.0F)
                .to(32.0F, 32.0F, 8.0F)
                .shade(false)
                .face(Direction.NORTH)
                .uvs(0.0F, 0.0F, 12.0F, 8.0F)
                .texture("#texture")
                .end()
                .face(Direction.SOUTH)
                .uvs(12.0F, 0.0F, 0.0F, 8.0F)
                .texture("#texture")
                .end();

        builder.element()
                .from(8.0F, 0.0F, -16.0F)
                .to(8.0F, 32.0F, 32.0F)
                .shade(false)
                .face(Direction.WEST)
                .uvs(0.0F, 0.0F, 12.0F, 8.0F)
                .texture("#texture")
                .end()
                .face(Direction.EAST)
                .uvs(12.0F, 0.0F, 0.0F, 8.0F)
                .texture("#texture")
                .end();

        builder.element()
                .from(-16.0F, 0.0F, 8.0F)
                .to(32.0F, 32.0F, 8.0F)
                .shade(false)
                .rotation()
                .origin(8.0F, 0.0F, 8.0F)
                .axis(Direction.Axis.Y)
                .angle(45.0F)
                .rescale(false)
                .end()
                .face(Direction.NORTH)
                .uvs(0.0F, 0.0F, 12.0F, 8.0F)
                .texture("#texture")
                .end()
                .face(Direction.SOUTH)
                .uvs(12.0F, 0.0F, 0.0F, 8.0F)
                .texture("#texture")
                .end();

        builder.element()
                .from(-16.0F, 0.0F, 8.0F)
                .to(32.0F, 32.0F, 8.0F)
                .shade(false)
                .rotation()
                .origin(8.0F, 0.0F, 8.0F)
                .axis(Direction.Axis.Y)
                .angle(-45.0F)
                .rescale(false)
                .end()
                .face(Direction.NORTH)
                .uvs(0.0F, 0.0F, 12.0F, 8.0F)
                .texture("#texture")
                .end()
                .face(Direction.SOUTH)
                .uvs(12.0F, 0.0F, 0.0F, 8.0F)
                .texture("#texture")
                .end();

        simpleBlock(block, builder);
    }

    private void mightyUndersproutsItem() {
        var model =
                itemModels()
                        .getBuilder("mighty_undersprouts")
                        .texture("plant", modLoc("block/mighty_undersprouts"))
                        .ao(false)
                        .guiLight(GuiLight.FRONT);
        var plane = model.element().from(1.0F, 4.5F, 8.0F).to(15.0F, 11.5F, 8.0F).shade(false);
        plane.face(Direction.NORTH).uvs(0.0F, 2.0F, 12.0F, 8.0F).texture("#plant");
        plane.face(Direction.SOUTH).uvs(0.0F, 2.0F, 12.0F, 8.0F).texture("#plant");

        var transforms = model.transforms();
        transforms
                .transform(ItemDisplayContext.GROUND)
                .translation(0.0F, 2.0F, 0.0F)
                .scale(0.5F)
                .end();
        transforms
                .transform(ItemDisplayContext.HEAD)
                .rotation(0.0F, 180.0F, 0.0F)
                .translation(0.0F, 13.0F, 7.0F)
                .end();
        transforms
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .translation(0.0F, 3.0F, 1.0F)
                .scale(0.55F)
                .end();
        transforms
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .translation(0.0F, 3.0F, 1.0F)
                .scale(0.55F)
                .end();
        transforms
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0.0F, -90.0F, 25.0F)
                .translation(1.13F, 3.2F, 1.13F)
                .scale(0.68F)
                .end();
        transforms
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(0.0F, 90.0F, -25.0F)
                .translation(1.13F, 3.2F, 1.13F)
                .scale(0.68F)
                .end();
        transforms.transform(ItemDisplayContext.FIXED).rotation(0.0F, 180.0F, 0.0F).end();
    }

    private void doublePlantBlock(DoublePlantBlock block) {
        ModelFile upper = tallPlantHalf("tall_undersprouts_top", 0.0F, 8.0F);
        ModelFile lower = tallPlantHalf("tall_undersprouts_bottom", 8.0F, 16.0F);
        getVariantBuilder(block)
                .forAllStates(
                        state ->
                                ConfiguredModel.builder()
                                        .modelFile(
                                                state.getValue(DoublePlantBlock.HALF)
                                                                == DoubleBlockHalf.UPPER
                                                        ? upper
                                                        : lower)
                                        .build());
    }

    private ModelFile tallPlantHalf(String modelName, float minV, float maxV) {
        var model =
                models().getBuilder(modelName)
                        .texture("cross", modLoc("block/tall_undersprouts"))
                        .texture("particle", "#cross")
                        .ao(false)
                        .renderType(CUTOUT);

        var northSouth = model.element().from(0.8F, 0.0F, 8.0F).to(15.2F, 16.0F, 8.0F).shade(false);
        northSouth
                .rotation()
                .origin(8.0F, 8.0F, 8.0F)
                .axis(Direction.Axis.Y)
                .angle(45.0F)
                .rescale(true);
        northSouth.face(Direction.NORTH).uvs(0.0F, minV, 16.0F, maxV).texture("#cross");
        northSouth.face(Direction.SOUTH).uvs(0.0F, minV, 16.0F, maxV).texture("#cross");

        var eastWest = model.element().from(8.0F, 0.0F, 0.8F).to(8.0F, 16.0F, 15.2F).shade(false);
        eastWest.rotation()
                .origin(8.0F, 8.0F, 8.0F)
                .axis(Direction.Axis.Y)
                .angle(45.0F)
                .rescale(true);
        eastWest.face(Direction.WEST).uvs(0.0F, minV, 16.0F, maxV).texture("#cross");
        eastWest.face(Direction.EAST).uvs(0.0F, minV, 16.0F, maxV).texture("#cross");
        return model;
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
        crossBlock(CustomBlocks.ASHY_CHARRED_GRASS.get());

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

    private void registerLuminiteOre() {
        Block ore = CustomBlocks.LUMINITE_ORE.get();
        ModelFile model =
                models().getBuilder(name(ore))
                        .renderType(CUTOUT)
                        .texture("particle", blockTexture(ore))
                        .texture("base", blockTexture(ore))
                        .texture("emissive", modLoc("block/carfstone_luminite_ore_emissive"))
                        .element()
                        .cube("#base")
                        .end()
                        .element()
                        .cube("#emissive")
                        .emissivity(15, 15)
                        .shade(false)
                        .end();
        simpleBlockWithItem(ore, model);
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
