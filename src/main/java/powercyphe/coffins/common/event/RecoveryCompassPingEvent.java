package powercyphe.coffins.common.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import powercyphe.coffins.common.init.CParticleTypes;
import powercyphe.coffins.common.init.CSounds;
import powercyphe.coffins.common.util.CUtil;

public class RecoveryCompassPingEvent implements UseItemCallback {

    @Override
    public InteractionResult interact(Player player, Level level, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (CUtil.isHoldingRecoveryCompass(player, hand) && CUtil.shouldTweakRecoveryCompass(level)
                && !player.getCooldowns().isOnCooldown(stack)) {
            var coffins = CUtil.getCoffins(player);

            for (GlobalPos pos : coffins) {
                Vec3 particlePos = pos.pos().getCenter();
                level.addParticle(
                        CParticleTypes.RECOVERY_PING,
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        0, 0, 0
                );
            }

            player.getCooldowns().addCooldown(stack, 30);
            player.playSound(coffins.isEmpty() ? CSounds.RECOVERY_PING_FAIL : CSounds.RECOVERY_PING,
                    0.5F, 0.95F + Mth.randomBetween(player.getRandom(), 0F, 0.1F));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
