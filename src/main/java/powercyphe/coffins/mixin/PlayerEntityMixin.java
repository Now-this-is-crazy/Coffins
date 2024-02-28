package powercyphe.coffins.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import powercyphe.coffins.Mod;
import powercyphe.coffins.block.ModBlocks;
import powercyphe.coffins.block.entity.CoffinBlockEntity;
import powercyphe.coffins.event.PlayerEventHandler;
import powercyphe.coffins.util.IEntityDataSaver;
import powercyphe.coffins.util.ModTags;
import powercyphe.coffins.util.RecoveryCompassData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IEntityDataSaver {

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow protected abstract void vanishCursedItems();

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void dropInventory(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if (!player.getWorld().isClient) {
            RecoveryCompassData.setNbt((IEntityDataSaver) player, null);
            RecoveryCompassData.setCount((IEntityDataSaver) player, 0);
            if (!player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                this.vanishCursedItems();

                ArrayList<ItemStack> inventory = new ArrayList<>();
                for (int i = 0; i < this.getInventory().size(); i++) {
                    inventory.add(this.getInventory().getStack(i));
                }

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

                        if (!keepRecoveryCompass) {
                            this.getInventory().removeOne(stack);
                            itemsAmount = itemsAmount + 1;
                            items.set(i, stack);
                        } else {
                            this.getInventory().removeOne(stack);
                            RecoveryCompassData.setNbt((IEntityDataSaver) player, stack);
                            RecoveryCompassData.setCount((IEntityDataSaver) player, stack.getCount());
                            hasRecoveryCompass = true;
                        }
                    }
                }

                if (!items.isEmpty() && itemsAmount > 0) {
                    TreeMap<BlockPos, Integer> distanceMap = new TreeMap<>();
                    World world = player.getWorld();

                    BlockPos playerPos = player.getBlockPos();
                    boolean shift = true;
                    while (shift) {
                        BlockPos shiftPos = playerPos.down(1);
                        if (
                                        player.getWorld().getBlockState(shiftPos).isIn(ModTags.Blocks.COFFIN_REPLACEABLE)
                        ) {
                            playerPos = shiftPos;
                        } else {
                            shift = false;
                        }

                    }

                    int xPlayer = playerPos.getX();
                    int yPlayer = playerPos.getY();
                    int zPlayer = playerPos.getZ();

                    int xMin = playerPos.getX() - 16;
                    int xMax = playerPos.getX() + 16;
                    int zMin = playerPos.getZ() - 16;
                    int zMax = playerPos.getZ() + 16;

                    for (int x = xMin; x < xMax; x++) {
                        for (int z = zMin; z < zMax; z++) {
                            for (int y = -64; y < 255; y++) {
                                BlockPos blockPos = new BlockPos(x, y, z);
                                int distance = 0;
                                if (
                                        player.getWorld().getBlockState(blockPos).isIn(ModTags.Blocks.COFFIN_REPLACEABLE) &&
                                                        player.getWorld().getWorldBorder().contains(x, z)
                                ) {
                                    distance = distance + Math.abs(x - xPlayer) + Math.abs(y - yPlayer) + Math.abs(z - zPlayer);
                                    distanceMap.put(blockPos, distance);
                                }
                            }
                        }
                    }
                    List<Map.Entry<BlockPos, Integer>> entryList = new ArrayList<>(distanceMap.entrySet());
                    entryList.sort(Map.Entry.comparingByValue());
                    int sizeCheck = entryList.size();
                    if (sizeCheck > 0) {
                        BlockPos blockPos = entryList.get(1).getKey();
                        BlockState blockState = ModBlocks.COFFIN.getDefaultState();

//                    player.sendMessage(Text.literal("Your Items were dropped at " + "[" + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + "]"));
                        world.setBlockState(blockPos, blockState);
                        BlockEntity blockEntity = world.getBlockEntity(blockPos);
                        if (blockEntity instanceof CoffinBlockEntity coffinBlockEntity) {
                            coffinBlockEntity.setItems(items);
                            coffinBlockEntity.setOwner(player.getEntityName());
                            coffinBlockEntity.setFragile(true);
                            world.updateListeners(blockPos, blockState, blockState, 3);
                        }
                    } else {
//                    player.sendMessage(Text.literal("Your Items were dropped at " + "[" + player.getBlockPos().getX() + " " + player.getBlockPos().getY() + " " + player.getBlockPos().getZ() + "]"));
                        ItemScatterer.spawn(world, player.getBlockPos(), items);
                    }
                }
            }
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
