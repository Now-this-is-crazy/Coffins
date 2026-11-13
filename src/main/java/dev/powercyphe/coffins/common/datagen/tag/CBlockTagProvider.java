package dev.powercyphe.coffins.common.datagen.tag;

import dev.powercyphe.coffins.common.init.CBlockItemIds;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.tags.BlockTags;
import dev.powercyphe.coffins.common.init.CTags;

import java.util.concurrent.CompletableFuture;

public class CBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public CBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(CTags.Blocks.COFFIN_REPLACEABLE)
                .forceAddTag(BlockTags.BASE_STONE_OVERWORLD)
                .forceAddTag(BlockTags.SUBSTRATE_OVERWORLD)
                .forceAddTag(BlockTags.SNOW)
                .forceAddTag(BlockTags.SMALL_FLOWERS)
                .add(BlockItemIds.SAND.block(), BlockItemIds.RED_SAND.block(), BlockItemIds.GRAVEL.block())

                .forceAddTag(BlockTags.BASE_STONE_NETHER)
                .forceAddTag(BlockTags.NYLIUM)
                .forceAddTag(BlockTags.WART_BLOCKS)
                .add(BlockItemIds.SOUL_SAND.block(), BlockItemIds.SOUL_SOIL.block())
        ;

        builder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CBlockItemIds.COFFIN);
        builder(BlockTags.MINEABLE_WITH_AXE)
                .add(CBlockItemIds.COFFIN);
    }
}
