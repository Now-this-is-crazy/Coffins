package powercyphe.coffins.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.block.CoffinBlock;

import java.util.function.Function;

public interface CBlocks {

    Block COFFIN = registerWithoutItem("coffin", CoffinBlock::new, BlockBehaviour.Properties.of()
            .strength(3F, 9999).pushReaction(PushReaction.IGNORE)
            .sound(SoundType.WOOD).mapColor(MapColor.WOOD));

    static void init() {}

    static Block registerWithoutItem(String name, Function<BlockBehaviour.Properties, Block> blockFunction, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Coffins.id(name));
        return Registry.register(BuiltInRegistries.BLOCK, key, blockFunction.apply(properties.setId(key)));
    }

    static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFunction, BlockBehaviour.Properties properties) {
        var block = registerWithoutItem(name, blockFunction, properties);
        CItems.register(name, prop -> new BlockItem(block, prop),
                new Item.Properties().useBlockDescriptionPrefix());

        return block;
    }
}
