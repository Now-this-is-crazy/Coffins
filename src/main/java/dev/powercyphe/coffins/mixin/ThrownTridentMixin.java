package dev.powercyphe.coffins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin {

    @Unique
    private int dropDelay = 0;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;isAcceptibleReturnOwner()Z"))
    private boolean coffins$tickDiscard(boolean original) {
        if (!original) {
            this.dropDelay++;
            return this.dropDelay < 5;
        } else {
            this.dropDelay = 0;
        }
        return true;
    }
}
