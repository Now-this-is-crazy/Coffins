package powercyphe.coffins.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import powercyphe.coffins.block.ModBlocks;
import powercyphe.coffins.util.ModTags;

import java.util.ArrayList;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.COFFIN);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.COFFIN);
        getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                .add(ModBlocks.COFFIN);

        getOrCreateTagBuilder(ModTags.Blocks.COFFIN_REPLACEABLE)
                .add(Blocks.AIR)
                .add(Blocks.CAVE_AIR)

                .add(Blocks.SEAGRASS)
                .add(Blocks.TALL_SEAGRASS)
                .add(Blocks.KELP)
                .add(Blocks.KELP_PLANT)

                .forceAddTag(BlockTags.SMALL_FLOWERS)
                .forceAddTag(BlockTags.TALL_FLOWERS)
                .forceAddTag(BlockTags.SAPLINGS)
                .forceAddTag(BlockTags.CORAL_PLANTS)
                .forceAddTag(BlockTags.REPLACEABLE_PLANTS)
                .forceAddTag(BlockTags.CROPS)

                .add(Blocks.SNOW)
                .add(Blocks.MOSS_CARPET)
                .add(Blocks.CAVE_VINES)
                .add(Blocks.CAVE_VINES_PLANT)
                .add(Blocks.SCULK_VEIN)

                .add(Blocks.WATER)
                .add(Blocks.LAVA);


    }
}
