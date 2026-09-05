package powercyphe.coffins.common.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import powercyphe.coffins.common.api.CoffinsEvents;
import powercyphe.coffins.common.compat.slot.TrinketsSlotProvider;
import powercyphe.coffins.common.init.CRegistries;
import powercyphe.coffins.common.menu.slot.SavedSlotProvider;

import static powercyphe.coffins.common.Coffins.id;

public class BuiltInCoffinsCompat {

    public static final String TRINKETS_ID = "trinkets_updated";
    public static final boolean TRINKETS = FabricLoader.getInstance().isModLoaded(TRINKETS_ID);
    public static SavedSlotProvider.Type<TrinketsSlotProvider> TRINKETS_TYPE = null;

    public static void init() {
        if (TRINKETS) {
            CoffinsEvents.STORE_ITEMS.register(id(TRINKETS_ID), new TrinketsCompatibility());
            TRINKETS_TYPE = Registry.register(
                    CRegistries.SAVED_SLOT_PROVIDER_TYPE, id(TRINKETS_ID),
                    SavedSlotProvider.Type.of(TrinketsSlotProvider.CODEC)
            );


        }
    }
}
