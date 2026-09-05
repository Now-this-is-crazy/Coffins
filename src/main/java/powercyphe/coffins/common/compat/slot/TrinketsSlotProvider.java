package powercyphe.coffins.common.compat.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.trinkets.api.TrinketSlotReference;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.compat.BuiltInCoffinsCompat;
import powercyphe.coffins.common.menu.slot.SavedSlotProvider;

public record TrinketsSlotProvider(TrinketSlotReference savedSlot) implements SavedSlotProvider {
    /**
     *
     * Built-in Trinkets Compatibility
     * <br>
     * <a href="https://modrinth.com/mod/trinkets-updated">Trinkets Updated by Patbox</a>
     *
     */
    public static final MapCodec<TrinketsSlotProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(TrinketSlotReference.CODEC.fieldOf("savedSlot").forGetter(TrinketsSlotProvider::savedSlot))
                    .apply(instance, TrinketsSlotProvider::new)
    );

    @Override
    public boolean placeBackInSlot(ServerPlayer player, ServerLevel level, ItemStack stack) {
        var attachment = TrinketsApi.getAttachment(player);
        var slot = attachment.getSlotAccess(this.savedSlot);

        if (slot != null && slot.get().isEmpty()) {
            return slot.set(stack);
        }
        return false;
    }

    @Override
    public Type<?> type() {
        return BuiltInCoffinsCompat.TRINKETS_TYPE;
    }
}
