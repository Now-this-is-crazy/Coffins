package powercyphe.coffins.common.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import powercyphe.coffins.common.init.CTags;

import java.util.concurrent.CompletableFuture;

public class CItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public CItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        valueLookupBuilder(CTags.Items.POST_RESPAWN_ITEMS)
                .add(Items.RECOVERY_COMPASS);
    }
}
