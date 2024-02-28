package powercyphe.coffins.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.ModBlocks;

public class ModItemGroup {
    public static final ItemGroup COFFINS = FabricItemGroupBuilder.build(
            new Identifier(Mod.MOD_ID, "coffins"), () -> new ItemStack(ModBlocks.COFFIN));
}
