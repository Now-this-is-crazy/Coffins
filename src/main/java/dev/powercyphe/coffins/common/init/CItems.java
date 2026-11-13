package dev.powercyphe.coffins.common.init;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

import java.util.function.Function;

public interface CItems {

    Item COFFIN = register(CBlockItemIds.COFFIN.item(), properties -> new BlockItem(CBlocks.COFFIN, properties),
            new Item.Properties().useBlockDescriptionPrefix());

    static void init() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(new CreativeModeTabEvents.ModifyOutput() {
                    @Override
                    public void modifyOutput(FabricCreativeModeTabOutput output) {
                        output.insertAfter(Items.RESPAWN_ANCHOR, CItems.COFFIN);
                    }
                });
    }

    static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> itemFunction, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, key, itemFunction.apply(properties.setId(key)));
    }
}
