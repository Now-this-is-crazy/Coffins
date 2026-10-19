package dev.powercyphe.coffins.common.util;

import com.google.common.collect.ImmutableList;
import dev.powercyphe.coffins.common.init.CItems;
import dev.powercyphe.coffins.mixin.accessor.AbstractArrowAccessor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import dev.powercyphe.coffins.common.api.CoffinsEvents;
import dev.powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import dev.powercyphe.coffins.common.init.CAttachmentTypes;
import dev.powercyphe.coffins.common.init.CGamerules;
import dev.powercyphe.coffins.common.init.CTags;
import dev.powercyphe.coffins.common.menu.slot.InventorySlotProvider;
import dev.powercyphe.coffins.common.menu.slot.SavedSlotProvider;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.powercyphe.coffins.common.block.entity.CoffinBlockEntity.*;

public interface CUtil {

    static boolean tryConsumeCoffin(ServerPlayer player, ServerLevel level) {
        return (!level.getGameRules().get(CGamerules.COFFIN_REQUIRES_ITEM) || player.getInventory().contains(stack -> {
            if (stack.is(CItems.COFFIN)) {
                stack.shrink(1);
                return true;
            }
            return false;
        }));
    }

    static @Nullable BlockPos findCoffinPos(Player player) {
        Level level = player.level();
        BlockPos origin = clampPos(level, player.blockPosition());

        if (canPlaceCoffinAt(level, origin) && hasFloor(level, origin)) {
            return origin;
        } else {
            List<BlockPos> available = new ArrayList<>();

            for (int offset = 1; offset <= 16; offset++) {
                BlockPos corner1 = clampPos(level, origin.offset(-offset, -offset, -offset));
                BlockPos corner2 = clampPos(level, origin.offset(offset, offset, offset));

                BlockBox box = BlockBox.of(corner1, corner2);
                for (BlockPos pos : box) {
                    pos = pos.mutable().immutable();
                    if (corner1.getX() < pos.getX() && pos.getX() < corner2.getX()
                            && corner1.getY() < pos.getY() && pos.getY() < corner2.getY()
                            && corner1.getZ() < pos.getZ() && pos.getZ() < corner2.getZ()) {
                        continue;
                    }

                    if (canPlaceCoffinAt(level, pos)) {
                        if (hasFloor(level, pos)) {
                            return pos;
                        } else {
                            available.add(pos);
                        }
                    }
                }
            }

            if (!available.isEmpty()) {
                available.sort(Comparator.comparingDouble(blockPos -> blockPos.distSqr(origin)));
                return available.getFirst();
            }
            return null;
        }
    }

    static boolean canPlaceCoffinAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(CTags.Blocks.COFFIN_REPLACEABLE);
    }

    static boolean hasFloor(Level level, BlockPos pos) {
        return level.isEmptyBlock(pos) && level.getBlockState(pos.below())
                .isFaceSturdy(level, pos.below(), Direction.UP, SupportType.RIGID);
    }

    static BlockPos clampPos(Level level, BlockPos pos) {
        BlockPos clamped = level.getWorldBorder().clampToBounds(pos);
        if (!level.isInsideBuildHeight(clamped)) {
            clamped = new BlockPos(
                    clamped.getX(),
                    Math.clamp(clamped.getY(), level.getMinY() + 1, level.getMaxY()),
                    clamped.getZ()
            );
        }
        return clamped;
    }

    static void loadInventory(ValueInput input, CoffinBlockEntity coffin) {
        NonNullList<ItemStack> items;

        if (coffin.isGrave()) {
            ArrayList<ItemStack> premade = new ArrayList<>();
            for (int i = 0; i < DEFAULT_GRAVE_SIZE; i++) {
                premade.add(ItemStack.EMPTY);
            }

            for (ItemStackWithSlot slot : input.listOrEmpty(ITEMS, ItemStackWithSlot.CODEC)) {
                if (slot.slot() < premade.size()) {
                    premade.set(slot.slot(), slot.stack());
                } else {
                    premade.add(slot.stack());
                }
            }
            while (premade.size() % 7 != 0) {
                premade.add(ItemStack.EMPTY);
            }

            items = new NonNullList<>(premade, ItemStack.EMPTY);
        } else {
            items = NonNullList.withSize(DEFAULT_INVENTORY_SIZE, ItemStack.EMPTY);

            for (ItemStackWithSlot slot : input.listOrEmpty(ITEMS, ItemStackWithSlot.CODEC)) {
                if (slot.slot() < items.size()) {
                    items.set(slot.slot(), slot.stack());
                }
            }
        }
        coffin.setItemsAfterLoad(items);
    }

    static void saveInventory(ValueOutput output, CoffinBlockEntity coffin) {
        var items = output.list(ITEMS, ItemStackWithSlot.CODEC);

        for (int i = 0; i < coffin.getItems().size(); i++) {
            ItemStack itemStack = coffin.getItem(i);
            if (!itemStack.isEmpty()) {
                items.add(new ItemStackWithSlot(i, itemStack));
            }
        }

        if (items.isEmpty()) {
            output.discard(ITEMS);
        }
    }

    static int getExperiencePoints(Player player, float percent) {
        int points = 0;
        int levels = player.experienceLevel;

        for (int level = 0; level < levels; level++) {
            player.experienceLevel = level;
            points += player.getXpNeededForNextLevel();
        }

        player.experienceLevel = levels;
        points += (int) (player.experienceProgress * player.getXpNeededForNextLevel());
        return (int) (points * percent);
    }

    static boolean isHoldingRecoveryCompass(Player player, InteractionHand hand) {
        return player != null && player.getItemInHand(hand).is(Items.RECOVERY_COMPASS);
    }

    static boolean isHoldingRecoveryCompass(Player player) {
        return isHoldingRecoveryCompass(player, InteractionHand.MAIN_HAND)
                || isHoldingRecoveryCompass(player, InteractionHand.OFF_HAND);
    }

    static boolean shouldTweakRecoveryCompass(Level level) {
        return (level instanceof ServerLevel serverLevel
                ? serverLevel.getGameRules().get(CGamerules.TWEAK_RECOVERY_COMPASS)
                : CSyncedGamerules.TWEAK_RECOVERY_COMPASS);
    }

    static List<GlobalPos> getCoffins(Player player) {
        return ImmutableList.copyOf(player.getAttachedOrCreate(CAttachmentTypes.COFFINS));
    }

    static void addToCoffins(Player player, GlobalPos pos) {
        var coffins = new ArrayList<>(getCoffins(player));
        coffins.add(pos);

        if (coffins.size() > 100) {
            coffins.removeFirst();
        }

        player.setAttached(CAttachmentTypes.COFFINS, coffins);
    }

    static void removeFromCoffins(Player player, GlobalPos coffinPos) {
        var coffins = new ArrayList<>(getCoffins(player));
        coffins.remove(coffinPos);

        player.setAttached(CAttachmentTypes.COFFINS, coffins);
    }

    static void validateCoffins(ServerPlayer player) {
        var coffins = getCoffins(player);

        MinecraftServer server = player.level().getServer();
        for (GlobalPos pos : coffins) {
            ServerLevel level = server.getLevel(pos.dimension());

            if (level != null) {
                level.getChunkSource().addTicketAndLoadWithRadius(TicketType.FORCED, ChunkPos.containing(pos.pos()), 1)
                        .thenAccept((_) -> {
                            if (!(level.getBlockEntity(pos.pos()) instanceof CoffinBlockEntity coffin
                                    && coffin.isOwner(player) && !coffin.isRemoved())) {
                                removeFromCoffins(player, pos);
                            }
                        });
            } else {
                removeFromCoffins(player, pos);
            }
        }
    }

    @Nullable
    static Consumer<CoffinBlockEntity> store(ServerPlayer player, ServerLevel level, DamageSource source, BlockPos coffinPos) {
        var itemConsumer = Optional.ofNullable(storeItems(player, level, source, coffinPos));
        var experienceConsumer = Optional.ofNullable(storeExperience(player, level));

        return itemConsumer.isEmpty() && experienceConsumer.isEmpty() ? null : coffin -> {
            itemConsumer.ifPresent(con -> con.accept(coffin));
            experienceConsumer.ifPresent(con -> con.accept(coffin));
        };
    }

    static Consumer<CoffinBlockEntity> storeItems(ServerPlayer player, ServerLevel level, DamageSource source, BlockPos coffinPos) {
        Inventory inv = player.getInventory();
        List<BiSupplier<ItemStack, Optional<SavedSlotProvider>>> slots = new ArrayList<>();

        for (int index = 0; index < inv.getContainerSize(); index++) {
            ItemStack stack = inv.getItem(index);

            if (!stack.isEmpty() && !storePostRespawn(player, level, stack)) {
                slots.add(BiSupplier.create(stack, Optional.of(new InventorySlotProvider(index))));
            }
        }

        CoffinsEvents.STORE_ITEMS.invoker().storeItems(player, level, source, coffinPos,
                new CoffinsEvents.StoreItemsEvent.ItemCollector() {
                    @Override
                    public void collect(ItemStack stack) {
                        if (!stack.isEmpty() && !storePostRespawn(player, level, stack)) {
                            slots.add(BiSupplier.create(stack, Optional.empty()));
                        }
                    }

                    @Override
                    public void collect(ItemStack stack, SavedSlotProvider savedSlot) {
                        if (!stack.isEmpty() && !storePostRespawn(player, level, stack)) {
                            slots.add(BiSupplier.create(stack, Optional.ofNullable(savedSlot)));
                        }
                    }
                }
        );

        for (ItemStack stack : collectTridents(player, level)) {
            slots.add(BiSupplier.create(stack, Optional.empty()));
        }

        return slots.stream().allMatch((bi) -> bi.getFirst().isEmpty()) ? null : coffin -> {
            List<ItemStack> items = new ArrayList<>();
            Map<Integer, SavedSlotProvider> providers = new HashMap<>();

            for (var sup : slots) {
                ItemStack stack = sup.getFirst();

                if (!stack.isEmpty()) {
                    sup.getSecond().ifPresent(provider -> providers.put(items.size(), provider));
                    items.add(stack.copyAndClear());
                }
            }

            while (items.size() % 7 != 0 || items.size() < DEFAULT_GRAVE_SIZE) {
                items.add(ItemStack.EMPTY);
            }

            coffin.setItems(new NonNullList<>(items, ItemStack.EMPTY));
            providers.forEach(coffin::addSavedSlot);
        };
    }

    static Consumer<CoffinBlockEntity> storeExperience(ServerPlayer player, ServerLevel level) {
        int experience = CUtil.getExperiencePoints(player, level.getGameRules().get(CGamerules.COFFIN_STORED_EXPERIENCE) / 100F);
        return experience <= 0 ? null : coffin -> {
            coffin.setExperience(experience);

            player.setExperienceLevels(0);
            player.setExperiencePoints(0);
        };
    }

    static boolean storePostRespawn(ServerPlayer player, ServerLevel level, ItemStack stack) {
        if (level.getGameRules().get(CGamerules.KEEP_POST_RESPAWN_ITEMS)) {
            if (stack.is(CTags.Items.POST_RESPAWN_ITEMS)) {
                List<ItemStack> postRespawn = new ArrayList<>(player.getAttachedOrCreate(CAttachmentTypes.POST_RESPAWN_ITEMS));
                postRespawn.add(stack.copyAndClear());

                player.setAttached(CAttachmentTypes.POST_RESPAWN_ITEMS, postRespawn);
                return true;
            }

        }
        return false;
    }

    static List<ItemStack> collectTridents(ServerPlayer player, ServerLevel level) {
        List<ItemStack> stacks = new ArrayList<>();

        level.getEntities(EntityType.TRIDENT, trident -> player == trident.getOwner()
                        && trident.pickup == AbstractArrow.Pickup.ALLOWED)
                .forEach(trident -> {
                        stacks.add(((AbstractArrowAccessor) trident).coffins$getPickupItem().copy());
                        trident.discard();
                });
        return stacks;
    }

    static Vec3 centerPos(BlockPos blockPos) {
        return new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    static String formatProtectionTimer(int ticks, float tickrate) {
        int seconds = Mth.floor((float)ticks / tickrate);

        int minutes = seconds / 60;
        seconds %= 60;
        int hours = minutes / 60;
        minutes %= 60;
        int days = hours / 24;
        hours %= 24;
        if (hours > 0 || days > 0) {
            if (days > 0) {
                return String.format(Locale.ROOT, "%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
            }
            return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }
}
