package powercyphe.coffins.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import powercyphe.coffins.client.render.state.CoffinBlockEntityRenderState;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;

public class CoffinBlockEntityRenderer implements BlockEntityRenderer<CoffinBlockEntity, CoffinBlockEntityRenderState> {
    private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelResolver blockModelResolver;

    public CoffinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public CoffinBlockEntityRenderState createRenderState() {
        return new CoffinBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(CoffinBlockEntity entity, CoffinBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breakProgress);

        if (entity.shouldGlow(Minecraft.getInstance().player)) {
            this.blockModelResolver.update(state.modelState, entity.getBlockState(), DISPLAY_CONTEXT);

            state.outlineColor = entity.getOutlineColor(Minecraft.getInstance().player, partialTicks);
            state.outlineScale = ARGB.alphaFloat(state.outlineColor);
        }
    }

    @Override
    public void submit(CoffinBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submit, CameraRenderState camera) {
        if (state.outlineScale > 0) {
            poseStack.pushPose();
            float trans = (1F - state.outlineScale) / 2;

            poseStack.translate(trans, trans, trans);
            poseStack.scale(state.outlineScale, state.outlineScale, state.outlineScale);

            state.modelState.submitOnlyOutline(poseStack, submit, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
