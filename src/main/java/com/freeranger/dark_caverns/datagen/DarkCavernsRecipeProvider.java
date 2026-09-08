package com.freeranger.dark_caverns.datagen;

import com.freeranger.dark_caverns.DarkCaverns;
import com.freeranger.dark_caverns.registry.CustomBlocks;
import com.freeranger.dark_caverns.registry.CustomEquipment;
import com.freeranger.dark_caverns.registry.CustomItems;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

final class DarkCavernsRecipeProvider extends RecipeProvider {
    private static final RecipeCategory CATEGORY = RecipeCategory.MISC;
    private static final Ingredient SMITHING_TEMPLATE =
            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);

    DarkCavernsRecipeProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        RecipeOutput flatAdvancements = flattenRecipeAdvancements(output);
        buildStoneFamilyRecipes(flatAdvancements, carfstoneFamily());
        buildStoneFamilyRecipes(flatAdvancements, moltenCarfstoneFamily());
        buildMaterialRecipes(flatAdvancements);
        buildCookingRecipes(flatAdvancements);
        buildSmithingRecipes(flatAdvancements);
    }

    private void buildStoneFamilyRecipes(RecipeOutput output, StoneFamily family) {
        shaped(
                output,
                family.smoothName() + "_from_crafting",
                family.smooth(),
                4,
                input(family.base()),
                "##",
                "##");
        stonecut(
                output,
                family.smoothName() + "_from_stonecutting",
                family.smooth(),
                1,
                input(family.base()));

        shaped(
                output,
                family.bricksName() + "_from_crafting",
                family.bricks(),
                4,
                input(family.smooth()),
                "##",
                "##");
        stonecut(
                output,
                family.bricksName() + "_from_stonecutting",
                family.bricks(),
                1,
                input(family.smooth()));
        stonecut(
                output,
                family.bricksName() + "_from_stonecutting_2",
                family.bricks(),
                1,
                input(family.base()));

        shapeRecipes(output, family.base(), family.base(), family.baseShapes());
        shapeRecipes(output, family.smooth(), family.base(), family.smoothShapes());
        shapeRecipes(output, family.bricks(), family.base(), family.brickShapes());
    }

    private void shapeRecipes(
            RecipeOutput output, ItemLike material, ItemLike familyBase, StoneShapes shapes) {
        RecipeInput input = input(material);
        shaped(output, name(shapes.slab()) + "_from_crafting", shapes.slab(), 6, input, "###");
        shaped(
                output,
                name(shapes.stairs()) + "_from_crafting_1",
                shapes.stairs(),
                4,
                input,
                "#  ",
                "## ",
                "###");
        shaped(
                output,
                name(shapes.stairs()) + "_from_crafting_2",
                shapes.stairs(),
                4,
                input,
                "  #",
                " ##",
                "###");
        shaped(
                output,
                name(shapes.wall()) + "_from_crafting",
                shapes.wall(),
                6,
                input,
                "###",
                "###");

        stonecut(output, name(shapes.slab()) + "_from_stonecutting", shapes.slab(), 2, input);
        stonecut(output, name(shapes.stairs()) + "_from_stonecutting", shapes.stairs(), 1, input);
        if (material == familyBase) {
            stonecut(output, name(shapes.wall()) + "_from_stonecutting", shapes.wall(), 1, input);
            return;
        }

        RecipeInput baseInput = input(familyBase);
        stonecut(output, name(shapes.slab()) + "_from_stonecutting_2", shapes.slab(), 2, baseInput);
        stonecut(
                output,
                name(shapes.stairs()) + "_from_stonecutting_2",
                shapes.stairs(),
                1,
                baseInput);
        stonecut(output, name(shapes.wall()) + "_from_stonecutting", shapes.wall(), 1, baseInput);
        stonecut(output, name(shapes.wall()) + "_from_stonecutting_2", shapes.wall(), 1, input);
    }

    private void buildMaterialRecipes(RecipeOutput output) {
        threeByThree(
                output,
                "platinum_block",
                CustomBlocks.PLATINUM_BLOCK.get(),
                itemTag("ingots/platinum"));
        unpack(
                output,
                "platinum_ingot_from_block",
                CustomItems.PLATINUM_INGOT.get(),
                9,
                itemTag("storage_blocks/platinum"));
        unpack(
                output,
                "platinum_piece_from_ingot",
                CustomItems.PLATINUM_PIECE.get(),
                9,
                itemTag("ingots/platinum"));

        threeByThree(
                output,
                "luminite_block",
                CustomBlocks.LUMINITE_BLOCK.get(),
                itemTag("dusts/luminite"));
        unpack(
                output,
                "luminite_dust_from_block",
                CustomItems.LUMINITE_DUST.get(),
                9,
                itemTag("storage_blocks/luminite"));

        threeByThree(
                output,
                "hellstone_block",
                CustomBlocks.HELLSTONE_BLOCK.get(),
                itemTag("gems/hellstone"));
        unpack(
                output,
                "hellstone_from_block",
                CustomItems.HELLSTONE.get(),
                9,
                itemTag("storage_blocks/hellstone"));

        threeByThree(
                output,
                "shroomstone_block",
                CustomBlocks.SHROOMSTONE_BLOCK.get(),
                itemTag("gems/shroomstone"));
        unpack(
                output,
                "shroomstone_from_block",
                CustomItems.SHROOMSTONE.get(),
                9,
                itemTag("storage_blocks/shroomstone"));

        ShapelessRecipeBuilder.shapeless(CATEGORY, CustomItems.PLATINUM_INGOT.get())
                .requires(Ingredient.of(itemTag("ingots/iron")), 4)
                .requires(Ingredient.of(itemTag("nuggets/platinum")), 4)
                .unlockedBy("has_ingredient", has(itemTag("ingots/iron")))
                .save(output, id("platinum_ingot"));
        ShapelessRecipeBuilder.shapeless(CATEGORY, CustomItems.HELLSTONE.get())
                .requires(Ingredient.of(itemTag("gems/diamond")), 4)
                .requires(CustomItems.HELLSTONE_ROCK.get(), 4)
                .unlockedBy("has_ingredient", has(itemTag("gems/diamond")))
                .save(output, id("hellstone"));
        ShapelessRecipeBuilder.shapeless(CATEGORY, CustomItems.SHROOMSTONE.get())
                .requires(Ingredient.of(itemTag("gems/emerald")), 4)
                .requires(CustomItems.SHROOMSTONE_PIECE.get(), 4)
                .unlockedBy("has_ingredient", has(itemTag("gems/emerald")))
                .save(output, id("shroomstone"));
        ShapelessRecipeBuilder.shapeless(CATEGORY, CustomItems.SCORCHSTEEL_INGOT.get())
                .requires(Ingredient.of(itemTag("ingots/iron")), 4)
                .requires(CustomItems.SCORCHLING_TAIL.get(), 4)
                .unlockedBy("has_ingredient", has(itemTag("ingots/iron")))
                .save(output, id("scorchsteel"));

        ShapedRecipeBuilder.shaped(CATEGORY, CustomEquipment.LUMINITE_HELMET.get())
                .define('#', itemTag("dusts/luminite"))
                .pattern("###")
                .pattern("# #")
                .unlockedBy("has_ingredient", has(itemTag("dusts/luminite")))
                .save(output, id("luminite_helmet"));
        ShapedRecipeBuilder.shaped(CATEGORY, CustomItems.LUMINITE_TORCH.get())
                .define('#', itemTag("dusts/luminite"))
                .define('/', itemTag("rods/wooden"))
                .pattern("#")
                .pattern("/")
                .unlockedBy("has_ingredient", has(itemTag("dusts/luminite")))
                .save(output, id("luminite_torch"));
        ShapedRecipeBuilder.shaped(CATEGORY, CustomBlocks.LUMINITE_LANTERN.get())
                .define('X', Items.IRON_NUGGET)
                .define('#', CustomItems.LUMINITE_TORCH.get())
                .pattern("XXX")
                .pattern("X#X")
                .pattern("XXX")
                .unlockedBy("has_ingredient", has(CustomItems.LUMINITE_TORCH.get()))
                .save(output, id("luminite_lantern"));
        ShapedRecipeBuilder.shaped(CATEGORY, CustomItems.THROWABLE_LUMINITE_TORCH.get(), 4)
                .define('#', CustomItems.LUMINITE_TORCH.get())
                .define('S', itemTag("slimeballs"))
                .pattern(" # ")
                .pattern("#S#")
                .pattern(" # ")
                .unlockedBy("has_ingredient", has(CustomItems.LUMINITE_TORCH.get()))
                .save(output, id("throwable_luminite_torch"));
        ShapelessRecipeBuilder.shapeless(CATEGORY, CustomItems.SHROOMBOMB.get(), 2)
                .requires(Ingredient.of(itemTag("mushrooms")), 2)
                .requires(Ingredient.of(itemTag("gunpowder")), 3)
                .unlockedBy("has_ingredient", has(itemTag("mushrooms")))
                .save(output, id("shroombomb"));
    }

    private void buildCookingRecipes(RecipeOutput output) {
        cookPair(output, "coal", input(CustomBlocks.CARFSTONE_COAL_ORE.get()), Items.COAL, 0.1F);
        cookPair(
                output,
                "iron",
                input(CustomBlocks.CARFSTONE_IRON_ORE.get()),
                Items.IRON_INGOT,
                0.1F);
        cookPair(
                output,
                "gold",
                input(CustomBlocks.CARFSTONE_GOLD_ORE.get()),
                Items.GOLD_INGOT,
                0.1F);
        cookPair(
                output,
                "diamond",
                input(CustomBlocks.CARFSTONE_DIAMOND_ORE.get()),
                Items.DIAMOND,
                0.1F);
        cookPairWithCategory(
                output,
                "redstone",
                input(CustomBlocks.CARFSTONE_REDSTONE_ORE.get()),
                Items.REDSTONE,
                0.1F,
                CookingBookCategory.MISC);
        cookPair(
                output,
                "lapis",
                input(CustomBlocks.CARFSTONE_LAPIS_ORE.get()),
                Items.LAPIS_LAZULI,
                0.1F);
        cookPair(
                output,
                "platinum_piece",
                input(itemTag("ores/platinum")),
                CustomItems.PLATINUM_PIECE.get(),
                0.1F);
        cookPair(
                output,
                "luminite_dust_from_ore",
                input(itemTag("ores/luminite")),
                CustomItems.LUMINITE_DUST.get(),
                0.9F);
        cookPair(
                output,
                "luminite_dust_from_helmet",
                input(CustomEquipment.LUMINITE_HELMET.get()),
                CustomItems.LUMINITE_DUST.get(),
                0.9F);
        cookPair(
                output,
                "hellstone_rock",
                input(itemTag("ores/hellstone")),
                CustomItems.HELLSTONE_ROCK.get(),
                1.0F);
    }

    private void cookPair(
            RecipeOutput output,
            String name,
            RecipeInput input,
            ItemLike result,
            float experience) {
        SimpleCookingRecipeBuilder.smelting(input.ingredient(), CATEGORY, result, experience, 200)
                .unlockedBy("has_ingredient", input.criterion())
                .save(output, id(cookingId(name, "smelting")));
        SimpleCookingRecipeBuilder.blasting(input.ingredient(), CATEGORY, result, experience, 100)
                .unlockedBy("has_ingredient", input.criterion())
                .save(output, id(cookingId(name, "blasting")));
    }

    private void cookPairWithCategory(
            RecipeOutput output,
            String name,
            RecipeInput input,
            ItemLike result,
            float experience,
            CookingBookCategory category) {
        ResourceLocation smeltingId = id(cookingId(name, "smelting"));
        saveCookingRecipe(
                output,
                smeltingId,
                input,
                new SmeltingRecipe(
                        "", category, input.ingredient(), new ItemStack(result), experience, 200));

        ResourceLocation blastingId = id(cookingId(name, "blasting"));
        saveCookingRecipe(
                output,
                blastingId,
                input,
                new BlastingRecipe(
                        "", category, input.ingredient(), new ItemStack(result), experience, 100));
    }

    private void saveCookingRecipe(
            RecipeOutput output, ResourceLocation id, RecipeInput input, Recipe<?> recipe) {
        Advancement.Builder advancement =
                output.advancement()
                        .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                        .addCriterion("has_ingredient", input.criterion())
                        .rewards(AdvancementRewards.Builder.recipe(id))
                        .requirements(AdvancementRequirements.Strategy.OR);
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/misc/")));
    }

    private String cookingId(String name, String method) {
        return name.startsWith("luminite_dust_from_")
                ? name + "_" + method
                : name + "_from_" + method;
    }

    private void buildSmithingRecipes(RecipeOutput output) {
        List<Item> diamondGear =
                List.of(
                        Items.DIAMOND_SWORD,
                        Items.DIAMOND_PICKAXE,
                        Items.DIAMOND_AXE,
                        Items.DIAMOND_SHOVEL,
                        Items.DIAMOND_HOE,
                        Items.DIAMOND_HELMET,
                        Items.DIAMOND_CHESTPLATE,
                        Items.DIAMOND_LEGGINGS,
                        Items.DIAMOND_BOOTS);
        List<Item> platinumGear = gear("platinum");
        List<Item> hellstoneGear = gear("hellstone");
        List<Item> shroomstoneGear = gear("shroomstone");
        List<Item> netheriteGear =
                List.of(
                        Items.NETHERITE_SWORD,
                        Items.NETHERITE_PICKAXE,
                        Items.NETHERITE_AXE,
                        Items.NETHERITE_SHOVEL,
                        Items.NETHERITE_HOE,
                        Items.NETHERITE_HELMET,
                        Items.NETHERITE_CHESTPLATE,
                        Items.NETHERITE_LEGGINGS,
                        Items.NETHERITE_BOOTS);
        Ingredient cavernCatalyst = Ingredient.of(itemTag("dusts/luminite"));
        smithingSet(output, cavernCatalyst, diamondGear, itemTag("ingots/platinum"), platinumGear);
        smithingSet(output, cavernCatalyst, platinumGear, itemTag("gems/hellstone"), hellstoneGear);
        smithingSet(
                output, cavernCatalyst, platinumGear, itemTag("gems/shroomstone"), shroomstoneGear);
        smithingSet(
                output,
                SMITHING_TEMPLATE,
                platinumGear,
                itemTag("ingots/netherite"),
                netheriteGear);
        smithingSet(
                output,
                cavernCatalyst,
                platinumGear.subList(5, 9),
                itemTag("ingots/scorchsteel"),
                gear("scorchsteel").subList(5, 9));
    }

    private void smithingSet(
            RecipeOutput output,
            Ingredient template,
            List<Item> bases,
            TagKey<Item> addition,
            List<Item> results) {
        for (int index = 0; index < bases.size(); index++) {
            Item result = results.get(index);
            SmithingTransformRecipeBuilder.smithing(
                            template,
                            Ingredient.of(bases.get(index)),
                            Ingredient.of(addition),
                            CATEGORY,
                            result)
                    .unlocks("has_ingredient", has(addition))
                    .save(output, id(name(result)));
        }
    }

    private List<Item> gear(String material) {
        return List.of(
                item(material + "_sword"),
                item(material + "_pickaxe"),
                item(material + "_axe"),
                item(material + "_shovel"),
                item(material + "_hoe"),
                item(material + "_helmet"),
                item(material + "_chestplate"),
                item(material + "_leggings"),
                item(material + "_boots"));
    }

    private void shaped(
            RecipeOutput output,
            String id,
            ItemLike result,
            int count,
            RecipeInput input,
            String... pattern) {
        ShapedRecipeBuilder builder =
                ShapedRecipeBuilder.shaped(CATEGORY, result, count)
                        .define('#', input.ingredient())
                        .unlockedBy("has_ingredient", input.criterion());
        for (String row : pattern) {
            builder.pattern(row);
        }
        builder.save(output, id(id));
    }

    private void stonecut(
            RecipeOutput output, String id, ItemLike result, int count, RecipeInput input) {
        SingleItemRecipeBuilder.stonecutting(input.ingredient(), CATEGORY, result, count)
                .unlockedBy("has_ingredient", input.criterion())
                .save(output, id(id));
    }

    private void threeByThree(
            RecipeOutput output, String id, ItemLike result, TagKey<Item> ingredient) {
        ShapedRecipeBuilder.shaped(CATEGORY, result)
                .define('#', ingredient)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_ingredient", has(ingredient))
                .save(output, id(id));
    }

    private void unpack(
            RecipeOutput output, String id, ItemLike result, int count, TagKey<Item> ingredient) {
        ShapelessRecipeBuilder.shapeless(CATEGORY, result, count)
                .requires(ingredient)
                .unlockedBy("has_ingredient", has(ingredient))
                .save(output, id(id));
    }

    private static RecipeOutput flattenRecipeAdvancements(RecipeOutput delegate) {
        return new RecipeOutput() {
            @Override
            public void accept(
                    ResourceLocation id,
                    Recipe<?> recipe,
                    @Nullable AdvancementHolder advancement,
                    ICondition... conditions) {
                AdvancementHolder flatAdvancement =
                        advancement == null
                                ? null
                                : new AdvancementHolder(
                                        id.withPrefix("recipes/"), advancement.value());
                delegate.accept(id, recipe, flatAdvancement, conditions);
            }

            @Override
            public Advancement.Builder advancement() {
                return delegate.advancement();
            }
        };
    }

    private static RecipeInput input(ItemLike item) {
        return new RecipeInput(Ingredient.of(item), has(item));
    }

    private static RecipeInput input(TagKey<Item> tag) {
        return new RecipeInput(Ingredient.of(tag), has(tag));
    }

    private static TagKey<Item> itemTag(String path) {
        return DarkCavernsTags.commonItem(path);
    }

    private static Item item(String path) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(id(path));
    }

    private static ResourceLocation id(String path) {
        return DarkCaverns.id(path);
    }

    private static String name(ItemLike item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private static StoneFamily carfstoneFamily() {
        return new StoneFamily(
                CustomBlocks.CARFSTONE.get(),
                CustomBlocks.SMOOTH_CARFSTONE.get(),
                CustomBlocks.CARFSTONE_BRICKS.get(),
                new StoneShapes(
                        CustomBlocks.CARFSTONE_STAIRS.get(),
                        CustomBlocks.CARFSTONE_SLAB.get(),
                        CustomBlocks.CARFSTONE_WALL.get()),
                new StoneShapes(
                        CustomBlocks.SMOOTH_CARFSTONE_STAIRS.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_SLAB.get(),
                        CustomBlocks.SMOOTH_CARFSTONE_WALL.get()),
                new StoneShapes(
                        CustomBlocks.CARFSTONE_BRICK_STAIRS.get(),
                        CustomBlocks.CARFSTONE_BRICK_SLAB.get(),
                        CustomBlocks.CARFSTONE_BRICK_WALL.get()));
    }

    private static StoneFamily moltenCarfstoneFamily() {
        return new StoneFamily(
                CustomBlocks.MOLTEN_CARFSTONE.get(),
                CustomBlocks.SMOOTH_MOLTEN_CARFSTONE.get(),
                CustomBlocks.MOLTEN_CARFSTONE_BRICKS.get(),
                new StoneShapes(
                        CustomBlocks.MOLTEN_CARFSTONE_STAIRS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_SLAB.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_WALL.get()),
                new StoneShapes(
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_STAIRS.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_SLAB.get(),
                        CustomBlocks.SMOOTH_MOLTEN_CARFSTONE_WALL.get()),
                new StoneShapes(
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_STAIRS.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_SLAB.get(),
                        CustomBlocks.MOLTEN_CARFSTONE_BRICK_WALL.get()));
    }

    private record RecipeInput(Ingredient ingredient, Criterion<?> criterion) {}

    private record StoneShapes(ItemLike stairs, ItemLike slab, ItemLike wall) {}

    private record StoneFamily(
            ItemLike base,
            ItemLike smooth,
            ItemLike bricks,
            StoneShapes baseShapes,
            StoneShapes smoothShapes,
            StoneShapes brickShapes) {
        String baseName() {
            return name(base);
        }

        String smoothName() {
            return name(smooth);
        }

        String bricksName() {
            return name(bricks);
        }
    }
}
