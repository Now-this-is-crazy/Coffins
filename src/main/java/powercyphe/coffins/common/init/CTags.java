package powercyphe.coffins.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import powercyphe.coffins.common.Coffins;

import static net.minecraft.core.registries.Registries.*;

public interface CTags {

    interface Items {
        TagKey<Item> POST_RESPAWN_ITEMS = key(ITEM, "post_respawn_items");
    }

    interface Blocks {
        TagKey<Block> COFFIN_REPLACEABLE = key(BLOCK, "coffin_replaceable");
    }

    static <T> TagKey<T> key(ResourceKey<Registry<T>> registry, String path) {
        return TagKey.create(registry, Coffins.id(path));
    }
}
