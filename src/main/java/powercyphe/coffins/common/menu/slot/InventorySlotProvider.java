package powercyphe.coffins.common.menu.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.init.CSavedSlotProviders;

public record InventorySlotProvider(int savedSlot) implements SavedSlotProvider {
    public static final MapCodec<InventorySlotProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("savedSlot").forGetter(InventorySlotProvider::savedSlot))
                    .apply(instance, InventorySlotProvider::new)
    );

    @Override
    public boolean placeBackInSlot(ServerPlayer player, ServerLevel level, ItemStack stack) {
        Inventory inv = player.getInventory();

        if (inv.getItem(this.savedSlot).isEmpty()) {
            inv.setItem(this.savedSlot, stack);
            return true;
        }
        return false;
    }

    @Override
    public Type<? extends SavedSlotProvider> type() {
        return CSavedSlotProviders.INVENTORY;
    }
}
