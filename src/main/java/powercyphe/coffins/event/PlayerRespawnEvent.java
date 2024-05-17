package powercyphe.coffins.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import powercyphe.coffins.Mod;
import powercyphe.coffins.util.IEntityDataSaver;
import powercyphe.coffins.util.RecoveryCompassData;

public class PlayerRespawnEvent implements ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!newPlayer.getWorld().isClient()) {
            if (RecoveryCompassData.getNbt((IEntityDataSaver) oldPlayer) != null) {
                NbtCompound itemNbt = RecoveryCompassData.getNbt((IEntityDataSaver) oldPlayer);
                int itemCount = RecoveryCompassData.getCount((IEntityDataSaver) oldPlayer);
                if (newPlayer.getWorld().getGameRules().getBoolean(Mod.KEEP_RECOVERY_COMPASS)) {
                    ItemStack item = Items.RECOVERY_COMPASS.getDefaultStack();
                    item.setNbt(itemNbt);
                    item.setCount(itemCount);
                    if (!item.isEmpty()) {
                        newPlayer.getInventory().offerOrDrop(item);
                        newPlayer.playSound(SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.5f, 1f);
                    }
                }
            }
            RecoveryCompassData.setNbt((IEntityDataSaver) oldPlayer, ItemStack.EMPTY);
            RecoveryCompassData.setCount((IEntityDataSaver) oldPlayer, 0);
        }
    }
}