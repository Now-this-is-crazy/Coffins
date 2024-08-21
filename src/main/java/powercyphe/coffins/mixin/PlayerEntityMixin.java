package powercyphe.coffins.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.ModBlocks;
import powercyphe.coffins.block.entity.CoffinBlockEntity;
import powercyphe.coffins.modsupport.ModSupportManager;
import powercyphe.coffins.util.CoffinsUtil;
import powercyphe.coffins.util.IEntityDataSaver;
import powercyphe.coffins.util.RecoveryCompassData;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IEntityDataSaver {

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow protected abstract void vanishCursedItems();

    @Inject(method = "isBlockBreakingRestricted", at = @At("HEAD"), cancellable = true)
    private void coffinBlockRestriction(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (world.getBlockEntity(pos) instanceof CoffinBlockEntity blockEntity && !world.getGameRules().getBoolean(Mod.ALLOW_COFFIN_ROBBING)) {
            if (!CoffinsUtil.allowOpeningCoffin(world, blockEntity, player)) {
                player.sendMessage(Text.translatable("block.coffins.coffin.disallowBreak"), true);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void dropInventory(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (player.getWorld().isClient) return;
        RecoveryCompassData.setNbt((IEntityDataSaver) player, null);
        RecoveryCompassData.setCount((IEntityDataSaver) player, 0);
        if (player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return;
        this.vanishCursedItems();

        ArrayList<ItemStack> inventory = new ArrayList<>();
        for (int i = 0; i < this.getInventory().size(); i++) {
            inventory.add(this.getInventory().getStack(i));
        }

        // Add items from all the mods we support
        inventory.addAll(ModSupportManager.getAllModSupportedDrops(player));

        int itemsAmount = 0;
        DefaultedList<ItemStack> items = DefaultedList.ofSize(54, ItemStack.EMPTY);
        boolean hasRecoveryCompass = false;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && !stack.isOf(Items.AIR) && stack != Items.AIR.getDefaultStack() && itemsAmount < items.size()) {
                boolean keepRecoveryCompass = false;
                if (player.getWorld().getGameRules().getBoolean(Mod.KEEP_RECOVERY_COMPASS)) {
                    if (ItemStack.areItemsEqual(stack, Items.RECOVERY_COMPASS.getDefaultStack())) {
                        if (!hasRecoveryCompass) {
                            keepRecoveryCompass = true;
                        }
                    }
                }

                this.getInventory().removeOne(stack);
                if (!keepRecoveryCompass) {
                    itemsAmount = itemsAmount + 1;
                    items.set(i, stack);
                } else {
                    RecoveryCompassData.setNbt((IEntityDataSaver) player, stack);
                    RecoveryCompassData.setCount((IEntityDataSaver) player, stack.getCount());
                    hasRecoveryCompass = true;
                }
            }
        }
        World world = player.getWorld();
        @Nullable BlockPos coffinPos = CoffinsUtil.findCoffinLocation(world, player.getBlockPos(), player.getRecentDamageSource());

        if (!items.isEmpty() && itemsAmount > 0) {
            if (world.getGameRules().getBoolean(Mod.DEATH_REQUIRES_COFFIN)) {
                boolean hasCoffin = false;
                int coffinSlot = 0;
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isOf(ModBlocks.COFFIN.asItem())) {
                        hasCoffin = true;
                        coffinSlot = i;
                        break;
                    }
                }
                if (hasCoffin) {
                    ItemStack coffinStack = items.get(coffinSlot);
                    coffinStack.decrement(1);
                    items.set(coffinSlot, coffinStack);
                } else {
                    coffinPos = null;
                }
            }
            if (coffinPos != null) {
                BlockState blockState = ModBlocks.COFFIN.getDefaultState();
                if (player.getWorld().getGameRules().getBoolean(Mod.SHOW_DEATH_COORDS)) player.sendMessage(Text.literal("Your Items were dropped at " + "§7[" + coffinPos.getX() + " " + coffinPos.getY() + " " + coffinPos.getZ() + "]"));
                world.setBlockState(coffinPos, blockState);
                BlockEntity blockEntity = world.getBlockEntity(coffinPos);
                if (blockEntity instanceof CoffinBlockEntity coffinBlockEntity) {
                    coffinBlockEntity.setItems(items);
                    coffinBlockEntity.setOwner(player.getUuid().toString(), player.getName().getString());
                    coffinBlockEntity.setFragile(true);
                    world.updateListeners(coffinPos, blockState, blockState, 3);
                }
            } else {
                if (world.getGameRules().getBoolean(Mod.SHOW_DEATH_COORDS)) player.sendMessage(Text.literal("Your Items were dropped at " + "§7[" + player.getBlockPos().getX() + " " + player.getBlockPos().getY() + " " + player.getBlockPos().getZ() + "]"));
                ItemScatterer.spawn(world, player.getBlockPos(), items);
            }
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.save(null, false, serverWorld.savingDisabled);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (player.isHolding(Items.RECOVERY_COMPASS)) {
            if (player.getLastDeathPos().isPresent()) {
                BlockPos deathPos = player.getLastDeathPos().get().getPos();
                player.sendMessage(Text.literal(
                        "§fYou died at" + " §3[" + deathPos.getX() + " " + deathPos.getY() + " " + deathPos.getZ() + "]§f " +"(" + Math.round(Math.sqrt(player.squaredDistanceTo(deathPos.getX(), deathPos.getY(), deathPos.getZ()))) + " Blocks away)"),
                        true);
            }
        }
    }
}
