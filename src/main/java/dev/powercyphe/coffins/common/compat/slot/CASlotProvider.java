package dev.powercyphe.coffins.common.compat.slot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.powercyphe.coffins.common.compat.BuiltInCoffinsCompat;
import dev.powercyphe.coffins.common.menu.slot.SavedSlotProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record CASlotProvider(EquipmentSlot slot) implements SavedSlotProvider {
    /**
     *
     * Built-in Combat Amenities Compatibility
     * <br>
     * <a href="https://modrinth.com/mod/combat-amenities">Combat Amenities by Hollowed</a>
     *
     */

    public static final MapCodec<CASlotProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(EquipmentSlot.CODEC.fieldOf("slot").forGetter(CASlotProvider::slot))
                    .apply(instance, CASlotProvider::new)
    );

    @Override
    public boolean placeBackInSlot(ServerPlayer player, ServerLevel level, ItemStack stack) {
        if (player.getItemBySlot(this.slot).isEmpty()) {
            player.setItemSlot(this.slot, stack);
            return true;
        }
        return false;
    }

    @Override
    public Type<?> type() {
        return BuiltInCoffinsCompat.COMBAT_AMENITIES_TYPE;
    }
}
