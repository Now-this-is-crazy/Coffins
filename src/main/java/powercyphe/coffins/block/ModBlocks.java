package powercyphe.coffins.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.custom.CoffinBlock;
import powercyphe.coffins.item.ModItemGroup;

public class ModBlocks {

    // Adding Blocks
    public static final Block COFFIN = registerBlock("coffin",
            new CoffinBlock(FabricBlockSettings.copyOf(Blocks.MANGROVE_PLANKS).sounds(BlockSoundGroup.WOOD)), ModItemGroup.COFFINS);

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(Mod.MOD_ID, name), block);

    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registry.ITEM, new Identifier(Mod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(tab)));
    }


    public static void registerModBlocks() {
        Mod.debugMessage("Registering Blocks");
    }
}

