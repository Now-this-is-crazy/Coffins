package dev.powercyphe.coffins.common.compat;

import dev.powercyphe.coffins.common.compat.slot.CASlotProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import dev.powercyphe.coffins.common.api.CoffinsEvents;
import dev.powercyphe.coffins.common.compat.slot.TrinketsSlotProvider;
import dev.powercyphe.coffins.common.init.CRegistries;
import dev.powercyphe.coffins.common.menu.slot.SavedSlotProvider;

import static dev.powercyphe.coffins.common.Coffins.id;

public class BuiltInCoffinsCompat {

    public static final String TRINKETS_ID = "trinkets_updated";
    public static final boolean TRINKETS = FabricLoader.getInstance().isModLoaded(TRINKETS_ID);
    public static SavedSlotProvider.Type<TrinketsSlotProvider> TRINKETS_TYPE = null;

    public static final String COMBAT_AMENITIES_ID = "combatamenities";
    public static final boolean COMBAT_AMENITIES = FabricLoader.getInstance().isModLoaded(COMBAT_AMENITIES_ID);
    public static SavedSlotProvider.Type<CASlotProvider> COMBAT_AMENITIES_TYPE = null;

    public static void init() {
        if (TRINKETS) {
            CoffinsEvents.STORE_ITEMS.register(id(TRINKETS_ID), new TrinketsCompatibility());
            TRINKETS_TYPE = Registry.register(
                    CRegistries.SAVED_SLOT_PROVIDER_TYPE, id(TRINKETS_ID),
                    SavedSlotProvider.Type.of(TrinketsSlotProvider.CODEC)
            );
        }

        if (COMBAT_AMENITIES) {
            CoffinsEvents.STORE_ITEMS.register(id(COMBAT_AMENITIES_ID), new CombatAmenitiesCompatibility());
            COMBAT_AMENITIES_TYPE = Registry.register(
                    CRegistries.SAVED_SLOT_PROVIDER_TYPE, id(COMBAT_AMENITIES_ID),
                    SavedSlotProvider.Type.of(CASlotProvider.CODEC)
            );
        }
    }
}
