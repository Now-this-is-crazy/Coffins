package dev.powercyphe.coffins.common.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.ItemIds;
import dev.powercyphe.coffins.common.init.CTags;

import java.util.concurrent.CompletableFuture;

public class CItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public CItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(CTags.Items.POST_RESPAWN_ITEMS)
                .add(ItemIds.RECOVERY_COMPASS);
    }
}
