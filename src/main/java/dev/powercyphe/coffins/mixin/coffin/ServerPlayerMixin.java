package dev.powercyphe.coffins.mixin.coffin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import dev.powercyphe.coffins.common.api.CoffinsEvents;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import dev.powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import dev.powercyphe.coffins.common.init.CBlocks;
import dev.powercyphe.coffins.common.util.CUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Shadow
    @Final
    private static Logger LOGGER;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @WrapOperation(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void coffins$createCoffin(ServerPlayer player, ServerLevel level, DamageSource damageSource, Operation<Void> original) {
        if (!level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            BlockPos coffinPos = CUtil.findCoffinPos(player);
            this.destroyVanishingCursedItems();

            if (coffinPos != null) {
                var consumer = CUtil.store(player, level, damageSource, coffinPos);

                if (consumer != null || CoffinsEvents.KEEP_PLACE.invoker().keepPlace(player, level, damageSource, coffinPos)) {
                    if (CUtil.tryConsumeCoffin(player, level)) {
                        level.destroyBlock(coffinPos, true, this);
                        level.setBlockAndUpdate(coffinPos, CBlocks.COFFIN.defaultBlockState());

                        if (level.isEmptyBlock(coffinPos.below())) {
                            BlockBox surface = BlockBox.of(
                                    coffinPos.offset(-1, -1, -1),
                                    coffinPos.offset(1, -1, 1)
                            );
                            for (BlockPos pos : surface) {
                                if (level.isEmptyBlock(pos)) {
                                    level.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
                                }
                            }
                        }

                        if (level.getBlockEntity(coffinPos) instanceof CoffinBlockEntity coffin) {
                            coffin.setGrave(player);
                            if (consumer != null) {
                                consumer.accept(coffin);
                            }

                            CoffinsEvents.POST_PLACE.invoker().postPlace(player, level, damageSource, coffin);
                        }
                    }
                }
            } else {
                LOGGER.error("Couldn't find valid position for coffin within a reasonable radius.");
            }
        }
        original.call(player, level, damageSource);
    }
}
