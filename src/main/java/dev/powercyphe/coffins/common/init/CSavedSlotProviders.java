package dev.powercyphe.coffins.common.init;

import net.minecraft.core.Registry;
import dev.powercyphe.coffins.common.Coffins;
import dev.powercyphe.coffins.common.menu.slot.InventorySlotProvider;
import dev.powercyphe.coffins.common.menu.slot.SavedSlotProvider;

import static dev.powercyphe.coffins.common.menu.slot.SavedSlotProvider.Type;

public interface CSavedSlotProviders {

    Type<InventorySlotProvider> INVENTORY = register("inventory", Type.of(InventorySlotProvider.CODEC));

    static void init() {}

    static <T extends SavedSlotProvider> Type<T> register(String name, Type<T> slotProviderType) {
        return Registry.register(CRegistries.SAVED_SLOT_PROVIDER_TYPE, Coffins.id(name), slotProviderType);
    }
}
