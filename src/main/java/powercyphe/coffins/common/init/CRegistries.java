package powercyphe.coffins.common.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.menu.slot.SavedSlotProvider;

public interface CRegistries {

    ResourceKey<Registry<SavedSlotProvider.Type<?>>> SAVED_SLOT_PROVIDER_TYPE_KEY = ResourceKey.createRegistryKey(Coffins.id("saved_slot_provider_type"));
    MappedRegistry<SavedSlotProvider.Type<?>> SAVED_SLOT_PROVIDER_TYPE = FabricRegistryBuilder.create(SAVED_SLOT_PROVIDER_TYPE_KEY).buildAndRegister();

    static void init() {}
}
