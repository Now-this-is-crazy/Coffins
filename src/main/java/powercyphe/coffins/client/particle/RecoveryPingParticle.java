package powercyphe.coffins.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Ease;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import powercyphe.coffins.client.init.CParticleLayers;

public class RecoveryPingParticle extends SingleQuadParticle {
    private static final int MAX_DISTANCE = 512;
    private final SpriteSet sprites;

    public RecoveryPingParticle(ClientLevel level, double x, double y, double z, SpriteSet sprite) {
        super(level, x, y, z, sprite.first());
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        Vec3 particlePos = new Vec3(x, y, z);

        if (!camPos.closerThan(particlePos, MAX_DISTANCE)) {
            particlePos = camPos.add(camPos.vectorTo(new Vec3(x, y, z)).normalize().scale(MAX_DISTANCE));

            this.setPos(particlePos.x(), particlePos.y(), particlePos.z());
            this.xo = particlePos.x();
            this.yo = particlePos.y();
            this.zo = particlePos.z();
        }
        this.quadSize = Math.max(1F, (float) camPos.distanceTo(particlePos) / 16);

        this.lifetime = 10;
        this.sprites = sprite;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTickTime) {
        this.alpha = 1F - Ease.inQuart((this.age + partialTickTime) / (float) this.lifetime);
        super.extract(particleTypeRenderState, camera, partialTickTime);
    }

    @Override
    protected int getLightCoords(float a) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    protected Layer getLayer() {
        return CParticleLayers.PING_PARTICLE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new RecoveryPingParticle(level, x, y, z, this.sprites);
        }
    }
}
