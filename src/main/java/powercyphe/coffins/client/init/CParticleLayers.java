package powercyphe.coffins.client.init;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;

public interface CParticleLayers {

    SingleQuadParticle.Layer PING_PARTICLE = new SingleQuadParticle.Layer(true, TextureAtlas.LOCATION_PARTICLES,
            CPipelines.PING_PARTICLE);

    static void init() {}
}
