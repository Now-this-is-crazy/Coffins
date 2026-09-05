package powercyphe.coffins.common.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.client.screen.CoffinScreen;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import powercyphe.coffins.common.init.CMenuTypes;

import java.util.ArrayList;
import java.util.List;

public class CoffinMenu extends AbstractContainerMenu {
    public static final int ROWS_MAX = 6;
    public static final int SLOTS_PER_ROW = 7;

    public Container container;
    public DataSlot experience = new DataSlot() {
        private int value;

        @Override
        public int get() {
            return this.value;
        }

        @Override
        public void set(int value) {
            this.value = value;
        }
    };


    public int inventorySlots;
    public CoffinBlockEntity.DeathData deathData;

    public boolean isClient;
    public float scroll = 0F;

    public CoffinMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(0));
    }

    public CoffinMenu(int containerId, Inventory inventory, Container container) {
        super(CMenuTypes.COFFIN, containerId);
        this.isClient = inventory.player.level().isClientSide();

        this.container = container;

        this.addStandardInventorySlots(inventory, 8, 139);
        this.inventorySlots = this.slots.size();
        if (!this.isClient) {
            this.addCoffinSlots(this.container);
        }

        this.addDataSlot(this.experience);
        if (container instanceof CoffinBlockEntity coffin) {
            this.experience.set(coffin.getExperience());
            this.deathData = coffin.getDeathData();
        }

        this.container.startOpen(inventory.player);
    }

    @Override
    public void initializeContents(int stateId, List<ItemStack> items, ItemStack carried) {
        this.container = new SimpleContainer(items.size() - this.inventorySlots);

        this.addCoffinSlots(this.container);
        this.updateSlots();

        super.initializeContents(stateId, items, carried);
        if (Minecraft.getInstance().screen instanceof CoffinScreen coffinScreen) {
            coffinScreen.scroller.updateScrollbar();
        }
    }

    public void addCoffinSlots(Container container) {
        for (int index = 0; index < container.getContainerSize(); index++) {
            this.addSlot(new CoffinSlot(container, index, 0, 0));
        }
    }

    public void updateSlots() {
        int x = 0;
        int y = 0;

        for (Slot slot : this.slots) {
            if (slot instanceof CoffinSlot cSlot) {
                if (this.isInRange(slot.getContainerSlot())) {
                    var nSlot = new CoffinSlot(this.container, slot.getContainerSlot(), x * 18 + 8, y * 18 + 17);
                    this.slots.set(slot.index, nSlot);

                    nSlot.index = slot.index;
                    nSlot.shown = true;

                    x++;
                    if (x >= SLOTS_PER_ROW) {
                        x = 0;
                        y++;
                    }
                } else {
                    cSlot.shown = false;
                }
            }
        }
    }

    public int getRows() {
        return (int) Math.ceil(this.container.getContainerSize() / (double) SLOTS_PER_ROW);
    }

    public int startRow() {
        return (int) (Math.max(0F, this.getRows() - ROWS_MAX) * this.scroll);
    }

    public boolean isInRange(int slot) {
        if (!this.isClient) {
            return true;
        }
        return this.startRow() * 7 - 1 < slot &&
                slot < this.startRow() * SLOTS_PER_ROW + CoffinBlockEntity.DEFAULT_GRAVE_SIZE;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < this.inventorySlots) {
                if (!this.moveItemStackTo(stack, this.inventorySlots + this.startRow() * SLOTS_PER_ROW,
                        this.inventorySlots + this.startRow() * SLOTS_PER_ROW + CoffinBlockEntity.DEFAULT_GRAVE_SIZE, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, this.inventorySlots, true)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void collectExperience(ServerPlayer player) {
        if (this.container instanceof CoffinBlockEntity coffin) {
            player.giveExperiencePoints(coffin.getExperience());

            coffin.setExperience(0);
            this.experience.set(0);
        }
    }

    public void collectLoot(ServerPlayer player) {
        if (this.container instanceof CoffinBlockEntity coffin) {
            Inventory inv = player.getInventory();
            List<ItemStack> leftovers = new ArrayList<>();

            coffin.forEachItem(pair -> {
                ItemStack stack = pair.getFirst();
                var provider = pair.getSecond();

                provider.ifPresentOrElse(
                        p -> {
                            if (p.placeBackInSlot(player, player.level(), stack.copy())) {
                                stack.copyAndClear();
                            } else {
                                leftovers.add(stack);
                            }
                        }, () -> leftovers.add(stack)
                );
            });

            for (ItemStack stack : leftovers) {
                int slot = inv.getSlotWithRemainingSpace(stack);
                if (slot != -1) {
                    inv.add(slot, stack.copyAndClear());
                } else if (inv.getFreeSlot() != -1) {
                    inv.add(inv.getFreeSlot(), stack.copyAndClear());
                }
            }
        }
    }

    public class CoffinSlot extends Slot {
        public boolean shown = true;

        public CoffinSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean isActive() {
            return super.isActive() && this.shown;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !this.getItem().isEmpty() && super.mayPlace(stack);
        }
    }
}
