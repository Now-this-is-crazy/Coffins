package powercyphe.coffins.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import powercyphe.coffins.Mod;


public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> COFFIN_REPLACEABLE =
                createTag("coffin_replaceable");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(Registry.BLOCK_KEY, new Identifier(Mod.MOD_ID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> createTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(Mod.MOD_ID, name));
        }
    }
}
