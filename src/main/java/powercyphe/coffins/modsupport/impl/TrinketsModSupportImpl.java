package powercyphe.coffins.modsupport.impl;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import powercyphe.coffins.event.TrinketDropEvent;
import powercyphe.coffins.modsupport.ModSupporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Mod support for Trinkets
 * @author redvortex
 */
public class TrinketsModSupportImpl implements ModSupporter {
    @Override
    public String supportingModId() {
        return "trinkets";
    }

    @Override
    public void onInit() {
        TrinketDropEvent.registerCallback();
    }

    @Override
    public List<ItemStack> getDrops(PlayerEntity player) {
        List<ItemStack> items = new ArrayList<>();
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> trinkets.forEach((ref, stack) -> {
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }));
        return items;
    }
}
