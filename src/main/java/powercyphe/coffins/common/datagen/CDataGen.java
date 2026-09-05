package powercyphe.coffins.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import powercyphe.coffins.common.datagen.tag.CBlockTagProvider;
import powercyphe.coffins.common.datagen.tag.CItemTagProvider;

public class CDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        pack.addProvider(CItemTagProvider::new);
        pack.addProvider(CBlockTagProvider::new);

        pack.addProvider(CBlockLootTableProvider::new);

        pack.addProvider(CRecipeProvider::new);
        pack.addProvider(CModelProvider::new);
    }
}
