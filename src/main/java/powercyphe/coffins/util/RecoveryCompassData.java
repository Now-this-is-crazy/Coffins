package powercyphe.coffins.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class RecoveryCompassData {
    public static NbtCompound setNbt(IEntityDataSaver player, @Nullable ItemStack itemStack) {
        NbtCompound nbt = player.getPersistentData();
        NbtCompound itemNbt;
        if (itemStack == null) {
            itemNbt = Items.RECOVERY_COMPASS.getDefaultStack().getNbt();
        } else {
            itemNbt = itemStack.getNbt();
        }
        nbt.put("nbt", itemNbt);
        return itemNbt;
    }

    public static NbtCompound getNbt(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        return nbt.getCompound("nbt");
    }

    public static int setCount(IEntityDataSaver player, int count) {
        NbtCompound nbt = player.getPersistentData();
        nbt.putInt("count", count);
        return count;
    }

    public static int getCount(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        return nbt.getInt("count");
    }
}
