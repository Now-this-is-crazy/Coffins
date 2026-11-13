package dev.powercyphe.coffins.mixin.interaction;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.powercyphe.coffins.common.block.CoffinBlock;
import dev.powercyphe.coffins.common.util.CSyncedGamerules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@ModifyExpressionValue(method = "mayInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean coffins$mayInteractIfCoffin(boolean original, Entity entity, BlockPos pos) {
		return original || (CSyncedGamerules.isCoffinAccessibleOutsideWorldborder(entity.level())
				&& CoffinBlock.isGraveCoffin(entity.level(), pos));
	}
}