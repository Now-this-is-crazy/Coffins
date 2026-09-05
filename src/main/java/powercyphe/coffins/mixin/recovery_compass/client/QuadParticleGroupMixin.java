package powercyphe.coffins.mixin.recovery_compass.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.QuadParticleGroup;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import powercyphe.coffins.client.particle.RecoveryPingParticle;

@Mixin(QuadParticleGroup.class)
public class QuadParticleGroupMixin {

    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/culling/Frustum;pointInFrustum(DDD)Z"))
    private boolean coffins$alwaysRenderPing(boolean original, @Local(name = "particle") SingleQuadParticle particle) {
        if (particle instanceof RecoveryPingParticle) {
            return true;
        }
        return original;
    }
}
