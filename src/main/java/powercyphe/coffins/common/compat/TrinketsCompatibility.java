package powercyphe.coffins.common.compat;

import eu.pb4.trinkets.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.api.CoffinsEvents;
import powercyphe.coffins.common.compat.slot.TrinketsSlotProvider;

public class TrinketsCompatibility implements CoffinsEvents.StoreItemsEvent {
    /**
     *
     * Built-in Trinkets Compatibility
     * <br>
     * <a href="https://modrinth.com/mod/trinkets-updated">Trinkets Updated by Patbox</a>
     *
     */

    @Override
    public void storeItems(ServerPlayer player, ServerLevel level, DamageSource damageSource, BlockPos coffinPos, ItemCollector collector) {
        if (BuiltInCoffinsCompat.TRINKETS) {
            var attachment = TrinketsApi.getAttachment(player);

            for (TrinketSlotAccess slot : attachment.allEquipped(false)) {
                ItemStack stack = slot.get();

                if (TrinketsApi.getDropRule(stack, slot, player, false) == TrinketDropRule.DROP) {
                    collector.collect(stack, new TrinketsSlotProvider(slot.reference()));
                }
            }
        }
    }
}
