package powercyphe.coffins.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class ModBlockLootTableProvider extends SimpleFabricLootTableProvider {
    public ModBlockLootTableProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator, LootContextTypes.BLOCK);
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
       /* biConsumer.accept(new Identifier(Mod.MOD_ID, "blocks/coffin"),
                BlockLootTableGenerator.drops(ModBlocks.COFFIN.asItem())); */
    }
}
