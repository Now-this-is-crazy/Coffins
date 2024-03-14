package powercyphe.coffins.event;


import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;

public class TrinketDropEvent {

    public static void registerCallback() {
        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {

            // If the trinkets were dropped by a player and keep inventory is off, destroy the trinket
            if (!(entity instanceof PlayerEntity player)) return rule;
            if (player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return rule;

            return TrinketEnums.DropRule.DESTROY;
        });
    }
}
