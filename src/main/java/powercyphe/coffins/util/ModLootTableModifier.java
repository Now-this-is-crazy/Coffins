package powercyphe.coffins.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import powercyphe.coffins.Mod;
import powercyphe.coffins.item.ModItems;

public class ModLootTableModifier {
    public static final Identifier BURIED_TREASURE
            = new Identifier("minecraft", "chests/buried_treasure");
    public static final Identifier FISHING_TREASURE
            = new Identifier("minecraft", "gameplay/fishing/treasure");
    public static final Identifier ANCIENT_CITY_CHEST
            = new Identifier("minecraft", "chests/ancient_city");

    public static void modifyLootTables() {
        Mod.debugMessage("Modifying Loot Tables");
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (BURIED_TREASURE.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1)) // repeated 3 times
                        .conditionally(RandomChanceLootCondition.builder(0.02f)) // 2% chance to spawn
                        .with(ItemEntry.builder(ModItems.TOME_OF_FRAGILITY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }
            if (ANCIENT_CITY_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1)) // repeated 3 times
                        .conditionally(RandomChanceLootCondition.builder(0.07f)) // 2% chance to spawn
                        .with(ItemEntry.builder(ModItems.TOME_OF_FRAGILITY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }
            if (FISHING_TREASURE.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1)) // repeated 1 time
                        .conditionally(RandomChanceLootCondition.builder(0.05f)) // 5% chance to spawn
                        .with(ItemEntry.builder(ModItems.TOME_OF_FRAGILITY))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
