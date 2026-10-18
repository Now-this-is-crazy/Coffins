package dev.powercyphe.coffins.common.api;

import dev.powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import dev.powercyphe.coffins.common.menu.slot.SavedSlotProvider;
import dev.powercyphe.coffins.common.util.CUtil;

public interface CoffinsEvents {

    /**
     * Callback that overrides whether a coffin will be discarded.
     * This will NOT override a coffin not being placed at all.
     */
    Event<KeepPlaceEvent> KEEP_PLACE = EventFactory.createArrayBacked(KeepPlaceEvent.class, callbacks -> (player, level, source, coffinPos) -> {
        for (KeepPlaceEvent callback : callbacks) {
            if (callback.keepPlace(player, level, source, coffinPos)) {
                return true;
            }
        }
        return false;
    });

    /**
     * Callback that runs when {@link CUtil#storeItems(ServerPlayer, ServerLevel, DamageSource, BlockPos)} is called.
     * <p>This is run before the coffin is placed.
     */
    Event<StoreItemsEvent> STORE_ITEMS = EventFactory.createArrayBacked(StoreItemsEvent.class, callbacks -> (player, level, source, coffinPos, collector) -> {
        for (StoreItemsEvent callback : callbacks) {
            callback.storeItems(player, level, source, coffinPos, collector);
        }
    });

    /**
     * Callback that runs after the coffin was placed.
     */
    Event<PostPlaceEvent> POST_PLACE = EventFactory.createArrayBacked(PostPlaceEvent.class, callbacks -> ((player, level, source, coffin) -> {
        for (PostPlaceEvent callback : callbacks) {
            callback.postPlace(player, level, source, coffin);
        }
    }));

    interface KeepPlaceEvent {
        /**
         * Handles coffin placement condition on death.
         * @param player the player that the coffin is being placed for
         * @param level the level in which the coffin will reside in
         * @param damageSource the damage source the player died to
         * @param coffinPos the position the coffin will be placed at
         */
        boolean keepPlace(ServerPlayer player, ServerLevel level, DamageSource damageSource, BlockPos coffinPos);
    }

    interface StoreItemsEvent {
        /**
         * Handles item collection for when a coffin is placed.
         * @param player the player that the coffin is being placed for
         * @param level the level in which the coffin will reside in
         * @param damageSource the damage source the player died to
         * @param coffinPos the position the coffin is going to be placed at
         * @param collector the item collector to add items to the coffin
         */
        void storeItems(ServerPlayer player, ServerLevel level, DamageSource damageSource, BlockPos coffinPos, ItemCollector collector);

        interface ItemCollector {
            /**
             * Stores the item without a slot provider.
             * @param stack the item to store
             */
            void collect(ItemStack stack);

            /**
             * Stores the item with a slot provider, which will be used to return
             * the item to its previous slot when it is looted.
             * @param stack the item to store
             * @param savedSlot {@link SavedSlotProvider} that stores which slot the item was last in
             */
            void collect(ItemStack stack, SavedSlotProvider savedSlot);
        }
    }

    interface PostPlaceEvent {
        /**
         * Handles post-place actions for coffins.
         * @param player the player that the coffin is being placed for
         * @param level the level in which the coffin resides in
         * @param damageSource the damage source the player died to
         * @param coffin the coffin that was placed
         */
        void postPlace(ServerPlayer player, ServerLevel level, DamageSource damageSource, CoffinBlockEntity coffin);
    }
}
