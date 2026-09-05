package powercyphe.coffins.client.event;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import powercyphe.coffins.client.render.state.CoffinBlockEntityRenderState;

public class RenderCoffinOutlineEvent implements LevelRenderEvents.EndExtraction {
    @Override
    public void endExtraction(LevelExtractionContext context) {
        for (BlockEntityRenderState state : context.levelState().blockEntityRenderStates) {
            if (state instanceof CoffinBlockEntityRenderState) {
                context.levelState().haveGlowingEntities = true;
            }
        }
    }
}
