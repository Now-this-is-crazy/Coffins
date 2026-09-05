package powercyphe.coffins.mixin.recovery_compass.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import powercyphe.coffins.common.util.CUtil;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer {

    @Unique
    private boolean coffins$recoveryCompassMessage = false;

    @Shadow
    @Final
    protected Minecraft minecraft;

    public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void coffins$tickRecoveryCompass(CallbackInfo ci) {
        Level level = this.level();

        if (CUtil.isHoldingRecoveryCompass(this) && CUtil.shouldTweakRecoveryCompass(level)) {
            this.getLastDeathLocation().ifPresent(deathPos -> {
                this.coffins$recoveryCompassMessage = true;

                if (level.dimension().equals(deathPos.dimension())) {
                    BlockPos pos = deathPos.pos();
                    int dis = pos.distManhattan(this.blockPosition());

                    if (this.minecraft.showOnlyReducedInfo()) {
                        this.sendOverlayMessage(Component.translatable("coffins.recovery_compass.death_message_reduced",
                                Component.literal("" + dis).withStyle(ChatFormatting.DARK_AQUA)).withStyle(ChatFormatting.WHITE));
                    } else {
                        this.sendOverlayMessage(Component.translatable("coffins.recovery_compass.death_message",
                                Component.translatable("coffins.recovery_compass.death_location",
                                        pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.DARK_AQUA),
                                dis).withStyle(ChatFormatting.WHITE));
                    }
                } else {
                    this.sendOverlayMessage(Component.translatable("coffins.recovery_compass.death_message_dimension",
                            Component.translatable(deathPos.dimension().identifier().toString()).withStyle(ChatFormatting.DARK_AQUA)));
                }
            });
        } else if (this.coffins$recoveryCompassMessage) {
            this.coffins$recoveryCompassMessage = false;
            this.sendOverlayMessage(Component.empty());
        }
    }
}
