package powercyphe.coffins.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import powercyphe.coffins.common.init.CBlocks;
import powercyphe.coffins.common.util.NonGraveCoffinCondition;

import java.util.concurrent.CompletableFuture;

public class CBlockLootTableProvider extends FabricBlockLootSubProvider {
    public CBlockLootTableProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        add(CBlocks.COFFIN, this::coffinDrops);
    }

    public LootTable.Builder coffinDrops(Block drop) {
        return this.createNameableBlockEntityTable(drop).modifyPools(
                pool -> pool.when(new NonGraveCoffinCondition())
        );
    }
}
