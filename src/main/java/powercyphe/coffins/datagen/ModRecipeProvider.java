package powercyphe.coffins.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import powercyphe.coffins.block.ModBlocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }


    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(ModBlocks.COFFIN.asItem())
                .pattern("010")
                .pattern("232")
                .pattern("010")
                .input('0', Items.COBBLED_DEEPSLATE)
                .input('1', ItemTags.PLANKS)
                .input('2', Items.IRON_INGOT)
                .input('3', ModItemTagProvider.BASE_CONTAINERS)
                .criterion(FabricRecipeProvider.hasItem(Items.CHEST),
                        FabricRecipeProvider.conditionsFromItem(Items.CHEST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(Items.ECHO_SHARD, 4)
                .pattern("010")
                .pattern("111")
                .pattern("010")
                .input('0', Items.AMETHYST_SHARD)
                .input('1', Items.SCULK)
                .criterion(FabricRecipeProvider.hasItem(Items.SCULK),
                        FabricRecipeProvider.conditionsFromItem(Items.SCULK))
                .offerTo(exporter);

    }
}
