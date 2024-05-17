package powercyphe.coffins.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import powercyphe.coffins.block.ModBlocks;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z"))
    private boolean explosionMixin(BlockState instance) {
        if (instance.isOf(ModBlocks.COFFIN) || instance.isAir()) return true;
        return false;
    }
}
