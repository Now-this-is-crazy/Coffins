package powercyphe.coffins.common.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.menu.slot.SavedSlotProvider;

public interface CoffinsEvents {
    Event<StoreItemsEvent> STORE_ITEMS = EventFactory.createArrayBacked(StoreItemsEvent.class, callbacks -> (player, level, source, coffinPos, collector) -> {
        for (StoreItemsEvent callback : callbacks) {
            callback.storeItems(player, level, source, coffinPos, collector);
        }
    });

    interface StoreItemsEvent {
        void storeItems(ServerPlayer player, ServerLevel level, DamageSource damageSource, BlockPos coffinPos, ItemCollector collector);

        interface ItemCollector {
            void collect(ItemStack stack);

            void collect(ItemStack stack, SavedSlotProvider savedSlot);
        }
    }
}
