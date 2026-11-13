package dev.powercyphe.coffins.common.init;

import dev.powercyphe.coffins.common.Coffins;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public interface CBlockItemIds {

    BlockItemId COFFIN = register("coffin");

    static void init() {}

    static BlockItemId register(String name) {
        Identifier id = Coffins.id(name);
        return BlockItemId.create(id, id);
    }
}
