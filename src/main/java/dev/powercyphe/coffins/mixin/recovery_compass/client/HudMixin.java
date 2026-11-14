package dev.powercyphe.coffins.mixin.recovery_compass.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import dev.powercyphe.coffins.common.Coffins;
import dev.powercyphe.coffins.common.util.CUtil;

@Mixin(Hud.class)
public class HudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0))
    private void coffins$recoveryCompass(GuiGraphicsExtractor gui, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
        LocalPlayer player = this.minecraft.player;
        if (CUtil.isHoldingRecoveryCompass(player) && CUtil.shouldTweakRecoveryCompass(player.level())) {
            sprite = Coffins.id("hud/crosshair_recovery_compass");
        }
        original.call(gui, pipeline, sprite, x, y, width, height);
    }
}
