package powercyphe.coffins.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.init.CAttachmentTypes;
import powercyphe.coffins.common.util.CUtil;

import java.util.List;

public class CoffinsRespawnEvent implements  ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        CUtil.getCoffins(oldPlayer).forEach(pos -> CUtil.addToCoffins(newPlayer, pos));

        List<ItemStack> postRespawn = oldPlayer.getAttachedOrCreate(CAttachmentTypes.POST_RESPAWN_ITEMS);
        if (!postRespawn.isEmpty()) {
            for (ItemStack stack : postRespawn) {
                newPlayer.getInventory().placeItemBackInInventory(stack);
            }
            newPlayer.playSound(SoundEvents.ITEM_PICKUP, 0.2F,
                    ((newPlayer.getRandom().nextFloat() - newPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);

            oldPlayer.removeAttached(CAttachmentTypes.POST_RESPAWN_ITEMS);
            newPlayer.removeAttached(CAttachmentTypes.POST_RESPAWN_ITEMS);
        }
    }
}
