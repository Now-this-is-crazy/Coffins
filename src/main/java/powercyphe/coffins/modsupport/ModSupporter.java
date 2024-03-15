package powercyphe.coffins.modsupport;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ModSupporter {
    /**
     * Returns the mod ID of the mod the {@link ModSupporter} is supporting.
     * @return The supporting mod ID as a string.
     */
    String supportingModId();

    /**
     * Called on the {@link powercyphe.coffins.Mod}'s onInitialize method if the mod is loaded.
     */
    void onInit();

    /**
     * Returns a list of {@link ItemStack}s that the player has or owns, depending on the mod.
     * @param player The player that died.
     * @return A list of {@link ItemStack}s representing the player's drops.
     */
    List<ItemStack> getDrops(PlayerEntity player);

    /**
     * Checks if the mod is loaded by the client.
     * @return True if the mod is loaded, false otherwise.
     */
    default boolean isModLoaded() {
        return FabricLoader.getInstance().isModLoaded(supportingModId());
    }
}
