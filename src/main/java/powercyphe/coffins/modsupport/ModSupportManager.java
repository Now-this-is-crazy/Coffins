package powercyphe.coffins.modsupport;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import powercyphe.coffins.modsupport.impl.TrinketsModSupportImpl;

import java.util.ArrayList;
import java.util.List;

public class ModSupportManager {
    public static final List<ModSupporter> modSupportClasses = List.of(
            // Mod support implementation classes go here.
            new TrinketsModSupportImpl()
    );

    public static List<ItemStack> getAllModSupportedDrops(PlayerEntity player) {
        List<ItemStack> modSupportDrops = new ArrayList<>();
        modSupportClasses.forEach(clazz -> {
            // Only add the items if the mod is loaded
            if (clazz.isModLoaded()) modSupportDrops.addAll(clazz.getDrops(player));
        });
        return modSupportDrops;
    }
}
