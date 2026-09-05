package powercyphe.coffins.common.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import powercyphe.coffins.common.api.CoffinsEvents;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import powercyphe.coffins.common.init.CAttachmentTypes;
import powercyphe.coffins.common.init.CGamerules;
import powercyphe.coffins.common.init.CTags;
import powercyphe.coffins.common.menu.slot.InventorySlotProvider;
import powercyphe.coffins.common.menu.slot.SavedSlotProvider;

import java.util.*;
import java.util.function.Consumer;

import static powercyphe.coffins.common.block.entity.CoffinBlockEntity.*;

public interface CUtil {

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
                    Math.clamp(clamped.getY(), level.getMinY(), level.getMaxY()),
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
                : CSyncedValues.TWEAK_RECOVERY_COMPASS);
    }

    static List<GlobalPos> getCoffins(Player player) {
        return ImmutableList.copyOf(player.getAttachedOrCreate(CAttachmentTypes.COFFINS));
    }

    static void addToCoffins(Player player, GlobalPos pos) {
        var coffins = new ArrayList<>(getCoffins(player));
        coffins.add(pos);

        if (coffins.size() > 10) {
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
                            if (!(level.getBlockEntity(pos.pos()) instanceof CoffinBlockEntity coffin && coffin.isOwner(player) && !coffin.isRemoved())) {
                                removeFromCoffins(player, pos);
                            }
                        });
            } else {
                removeFromCoffins(player, pos);
            }
        }
    }

    static void storeItems(CoffinBlockEntity coffin, ServerPlayer player, DamageSource source) {
        ServerLevel level = player.level();
        Inventory inv = player.getInventory();

        ArrayList<ItemStack> preItems = new ArrayList<>();
        Map<Integer, SavedSlotProvider> preSavedSlots = new HashMap<>();

        for (int index = 0; index < inv.getContainerSize(); index++) {
            ItemStack stack = inv.getItem(index);

            if (!stack.isEmpty() && !storePostRespawn(player, stack)) {
                preSavedSlots.put(preItems.size(), new InventorySlotProvider(index));
                preItems.add(stack.copyAndClear());
            }
        }

        CoffinsEvents.STORE_ITEMS.invoker().storeItems(player, level, source, coffin.getBlockPos(),
                new CoffinsEvents.StoreItemsEvent.ItemCollector() {
                    @Override
                    public void collect(ItemStack stack) {
                        if (!stack.isEmpty() && !storePostRespawn(player, stack)) {
                            preItems.add(stack.copyAndClear());
                        }
                    }

                    @Override
                    public void collect(ItemStack stack, SavedSlotProvider savedSlot) {
                        if (!stack.isEmpty() && !storePostRespawn(player, stack)) {
                            preSavedSlots.put(preSavedSlots.size(), savedSlot);
                            preItems.add(stack.copyAndClear());
                        }
                    }
                }
        );

        while (preItems.size() % 7 != 0 || preItems.size() < DEFAULT_GRAVE_SIZE) {
            preItems.add(ItemStack.EMPTY);
        }

        coffin.setItems(new NonNullList<>(preItems, ItemStack.EMPTY));
        preSavedSlots.forEach(coffin::addSavedSlot);
    }

    static void storeExperience(CoffinBlockEntity coffin, ServerPlayer player) {
        ServerLevel level = player.level();

        coffin.setExperience(CUtil.getExperiencePoints(player,
                level.getGameRules().get(CGamerules.COFFIN_STORED_EXPERIENCE) / 100F));

        player.setExperienceLevels(0);
        player.setExperiencePoints(0);
    }

    static boolean storePostRespawn(ServerPlayer player, ItemStack stack) {
        if (player.level().getGameRules().get(CGamerules.KEEP_POST_RESPAWN_ITEMS)) {
            if (stack.is(CTags.Items.POST_RESPAWN_ITEMS)) {
                List<ItemStack> postRespawn = new ArrayList<>(player.getAttachedOrCreate(CAttachmentTypes.POST_RESPAWN_ITEMS));
                postRespawn.add(stack.copyAndClear());

                player.setAttached(CAttachmentTypes.POST_RESPAWN_ITEMS, postRespawn);
                return true;
            }

        }
        return false;
    }

    static List<ClientTooltipComponent> createTooltip(Component... components) {
        var tooltip = new ArrayList<ClientTooltipComponent>();
        for (Component line : components) {
            tooltip.add(ClientTooltipComponent.create(line.getVisualOrderText()));
        }

        return tooltip;
    }
}
