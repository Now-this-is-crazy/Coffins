package dev.powercyphe.coffins.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import dev.powercyphe.coffins.common.init.CBlocks;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

public class CRecipeProvider extends FabricRecipeProvider {
    public CRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }


    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {

            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.DECORATIONS, CBlocks.COFFIN)
                        .pattern("III")
                        .pattern("DCD")
                        .pattern("DDD")
                        .define('I', Items.IRON_INGOT)
                        .define('D', Items.COBBLED_DEEPSLATE)
                        .define('C', Items.CHEST)
                        .unlockedBy("has_iron", has(Items.IRON_INGOT))
                        .save(output);

            }
        };
    }

    @Override
    public String getName() {
        return "CRecipeProvider";
    }
}
