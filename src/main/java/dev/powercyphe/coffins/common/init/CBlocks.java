package dev.powercyphe.coffins.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import dev.powercyphe.coffins.common.block.CoffinBlock;

import java.util.function.Function;

public interface CBlocks {

    Block COFFIN = registerWithoutItem(CBlockItemIds.COFFIN.block(), CoffinBlock::new, BlockBehaviour.Properties.of()
            .strength(3F, 9999).pushReaction(PushReaction.IMMOVEABLE)
            .sound(SoundType.WOOD).mapColor(MapColor.WOOD));

    static void init() {}

    static Block registerWithoutItem(ResourceKey<Block> key, Function<BlockBehaviour.Properties, Block> blockFunction, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, key, blockFunction.apply(properties.setId(key)));
    }
}
