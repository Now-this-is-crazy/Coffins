package dev.powercyphe.coffins.common.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import dev.powercyphe.coffins.common.init.CParticleTypes;
import dev.powercyphe.coffins.common.init.CSounds;
import dev.powercyphe.coffins.common.util.CUtil;

public class RecoveryCompassPingEvent implements UseItemCallback {

    @Override
    public InteractionResult interact(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (CUtil.isHoldingRecoveryCompass(player, hand) && CUtil.shouldTweakRecoveryCompass(level)
                && !player.getCooldowns().isOnCooldown(stack)) {
            var coffins = CUtil.getCoffins(player);
            boolean ping = false;

            for (GlobalPos pos : coffins) {
                Vec3 particlePos = CUtil.centerPos(pos.pos());
                if (level.dimension() == pos.dimension()) {
                    ping = true;
                    level.addParticle(
                            CParticleTypes.RECOVERY_PING,
                            particlePos.x(), particlePos.y(), particlePos.z(),
                            0, 0, 0
                    );
                }
            }

            player.getCooldowns().addCooldown(stack, 30);
            player.playSound(ping ? CSounds.RECOVERY_PING : CSounds.RECOVERY_PING_FAIL,
                    0.5F, 0.95F + Mth.randomBetween(player.getRandom(), 0F, 0.1F));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
