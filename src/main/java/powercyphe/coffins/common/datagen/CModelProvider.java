package powercyphe.coffins.common.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import powercyphe.coffins.common.init.CBlocks;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class CModelProvider extends FabricModelProvider {
    public CModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        createCoffin(gen, CBlocks.COFFIN);
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {}

    public void createCoffin(BlockModelGenerators gen, Block block) {
        Material openTop = TextureMapping.getBlockTexture(block, "_top_open");
        MultiVariant closedModel = plainVariant(TexturedModel.CUBE_TOP_BOTTOM.create(block, gen.modelOutput));
        MultiVariant openModel = plainVariant(
                TexturedModel.CUBE_TOP_BOTTOM
                        .get(block)
                        .updateTextures(t -> t.put(TextureSlot.TOP, openTop))
                        .createWithSuffix(block, "_open", gen.modelOutput)
        );
        gen.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block)
                                .with(PropertyDispatch.initial(BlockStateProperties.OPEN)
                                        .select(false, closedModel)
                                        .select(true, openModel))
                                .with(PropertyDispatch.modify(BlockStateProperties.FACING)
                                        .select(Direction.DOWN, X_ROT_180)
                                        .select(Direction.UP, NOP)
                                        .select(Direction.NORTH, X_ROT_90)
                                        .select(Direction.SOUTH, X_ROT_90.then(Y_ROT_180))
                                        .select(Direction.WEST, X_ROT_90.then(Y_ROT_270))
                                        .select(Direction.EAST, X_ROT_90.then(Y_ROT_90))
                                )
                );
    }
}
