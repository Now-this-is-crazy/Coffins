package dev.powercyphe.coffins.common.compat;

import dev.powercyphe.coffins.common.api.CoffinsEvents;
import dev.powercyphe.coffins.common.compat.slot.CASlotProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;

public class CombatAmenitiesCompatibility implements CoffinsEvents.StoreItemsEvent {
    /**
     *
     * Built-in Combat Amenities Compatibility
     * <br>
     * <a href="https://modrinth.com/mod/combat-amenities">Combat Amenities by Hollowed</a>
     *
     */

    @Override
    public void storeItems(ServerPlayer player, ServerLevel level, DamageSource damageSource, BlockPos coffinPos, ItemCollector collector) {
        EquipmentSlot backSlot = EquipmentSlot.COMBATAMENITIES_BACKSLOT;
        EquipmentSlot beltSlot = EquipmentSlot.COMBATAMENITIES_BELTSLOT;

        collector.collect(player.getItemBySlot(backSlot), new CASlotProvider(backSlot));
        collector.collect(player.getItemBySlot(beltSlot), new CASlotProvider(beltSlot));
    }
}
