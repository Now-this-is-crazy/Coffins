package powercyphe.coffins.client.render.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class CoffinBlockEntityRenderState extends BlockEntityRenderState {
    public BlockModelRenderState modelState = new BlockModelRenderState();

    public int outlineColor = 0;
    public float outlineScale = 1F;
}
