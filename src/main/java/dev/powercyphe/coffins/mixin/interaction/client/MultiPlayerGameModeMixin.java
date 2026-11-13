package dev.powercyphe.coffins.mixin.interaction.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.powercyphe.coffins.common.block.CoffinBlock;
import dev.powercyphe.coffins.common.util.CSyncedGamerules;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean coffins$useItemOnIfCoffin(WorldBorder instance, BlockPos pos, Operation<Boolean> original, LocalPlayer player) {
		return original.call(instance, pos) || (CSyncedGamerules.isCoffinAccessibleOutsideWorldborder(player.level())
				&& CoffinBlock.isGraveCoffin(player.level(), pos));
	}
}