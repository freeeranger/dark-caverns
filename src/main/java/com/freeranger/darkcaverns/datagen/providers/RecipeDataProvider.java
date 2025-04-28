package com.freeranger.darkcaverns.datagen.providers;

import com.freeranger.darkcaverns.registries.BlockRegistry;
import com.freeranger.darkcaverns.registries.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class RecipeDataProvider extends RecipeProvider {
    protected RecipeDataProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // luminite dust -> luminite block (crafting)
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.BUILDING_BLOCKS, BlockRegistry.LUMINITE_BLOCK.get())
            .pattern("XXX")
            .pattern("XXX")
            .pattern("XXX")
            .define('X', ItemRegistry.LUMINITE_DUST.get())
            .unlockedBy("has_luminite_dust", has(ItemRegistry.LUMINITE_DUST.get()))
            .save(this.output);

        // luminite block -> luminite dust (crafting)
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.BUILDING_BLOCKS, ItemRegistry.LUMINITE_DUST.get(), 9)
            .requires(BlockRegistry.LUMINITE_BLOCK.get())
            .unlockedBy("has_luminite_block", has(BlockRegistry.LUMINITE_BLOCK.get()))
            .save(this.output);

        // luminite ore -> luminite dust (smelting)
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(BlockRegistry.LUMINITE_ORE),
                RecipeCategory.FOOD,
                ItemRegistry.LUMINITE_DUST,
                0.3f,
                200
            )
            .unlockedBy("has_luminite_ore", this.has(BlockRegistry.LUMINITE_ORE))
            .save(this.output, "luminite_dust_smelting");

        // luminite ore -> luminite dust (blasting)
        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(BlockRegistry.LUMINITE_ORE),
                RecipeCategory.FOOD,
                ItemRegistry.LUMINITE_DUST,
                0.3f,
                100
            )
            .unlockedBy("has_luminite_ore", this.has(BlockRegistry.LUMINITE_ORE))
            .save(this.output, "luminite_dust_blasting");
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new RecipeDataProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Recipe Provider: Dark Caverns";
        }
    }
}
