package powercyphe.coffins.common.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import powercyphe.coffins.common.init.CBlocks;
import powercyphe.coffins.common.init.CTags;

import java.util.concurrent.CompletableFuture;

public class CBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public CBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        valueLookupBuilder(CTags.Blocks.COFFIN_REPLACEABLE)
                .forceAddTag(BlockTags.BASE_STONE_OVERWORLD)
                .forceAddTag(BlockTags.SUBSTRATE_OVERWORLD)
                .forceAddTag(BlockTags.SNOW)
                .forceAddTag(BlockTags.SMALL_FLOWERS)
                .add(Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL)

                .forceAddTag(BlockTags.BASE_STONE_NETHER)
                .forceAddTag(BlockTags.NYLIUM)
                .forceAddTag(BlockTags.WART_BLOCKS)
                .add(Blocks.SOUL_SAND, Blocks.SOUL_SOIL)
        ;

        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CBlocks.COFFIN);
        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(CBlocks.COFFIN);
    }
}
