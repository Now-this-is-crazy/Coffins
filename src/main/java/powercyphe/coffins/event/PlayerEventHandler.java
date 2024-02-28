package powercyphe.coffins.event;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PlayerEventHandler {
    private static Map<ServerPlayerEntity, ItemStack> playerNbt = new HashMap<>();

    public static ItemStack getRecoveryCompass(ServerPlayerEntity player) {
        return playerNbt.get(player);
    }

    public static void setRecoveryCompass(ServerPlayerEntity player, ItemStack recoveryCompass) {
        playerNbt.put(player, recoveryCompass);
    }
}
