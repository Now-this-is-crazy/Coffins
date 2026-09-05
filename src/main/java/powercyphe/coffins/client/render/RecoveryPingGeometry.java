package powercyphe.coffins.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.ARGB;

public record RecoveryPingGeometry(float alpha) implements SubmitNodeCollector.CustomGeometryRenderer {

    @Override
    public void render(PoseStack.Pose pose, VertexConsumer consumer) {
        vertex(pose, consumer, 0F, 0F);
        vertex(pose, consumer, 0F, 1F);
        vertex(pose, consumer, 1F, 1F);
        vertex(pose, consumer, 1F, 0F);
    }

    public void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y) {
        consumer.addVertex(pose, x, y, 0).setColor(ARGB.white(this.alpha)).setUv(x, y);
    }
}
