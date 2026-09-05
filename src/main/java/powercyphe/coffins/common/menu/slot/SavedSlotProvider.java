package powercyphe.coffins.common.menu.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.init.CRegistries;
import powercyphe.coffins.common.init.CSavedSlotProviders;

public interface SavedSlotProvider {
    Codec<SavedSlotProvider> CODEC = Type.CODEC.dispatch(SavedSlotProvider::type, Type::codec);

    /**
     *
     * @param player Player to give Item
     * @param level Server Level
     * @param stack ItemStack to place back
     * @return Whether the item has been placed back successfully
     *
     */
    boolean placeBackInSlot(ServerPlayer player, ServerLevel level, ItemStack stack);

    Type<?> type();

    interface Type<T extends SavedSlotProvider> {
        Codec<Type<?>> CODEC = CRegistries.SAVED_SLOT_PROVIDER_TYPE.byNameCodec();

        static <T extends SavedSlotProvider> Type<T> of(MapCodec<T> codec) {
            return () -> codec;
        }

        MapCodec<T> codec();
    }
}
