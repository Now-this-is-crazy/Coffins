package dev.powercyphe.coffins.mixin.interaction.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.powercyphe.coffins.common.block.CoffinBlock;
import dev.powercyphe.coffins.common.util.CSyncedGamerules;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private @Nullable ClientLevel level;

    @WrapOperation(method = "extractBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean coffins$extractBlockOutlineIfCoffin(WorldBorder instance, BlockPos pos, Operation<Boolean> original) {
        return original.call(instance, pos) || (this.level != null
                && CSyncedGamerules.isCoffinAccessibleOutsideWorldborder(this.level)
                && CoffinBlock.isGraveCoffin(this.level, pos)
        );
    }
}
