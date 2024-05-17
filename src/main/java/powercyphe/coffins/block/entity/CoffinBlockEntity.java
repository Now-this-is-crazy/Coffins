package powercyphe.coffins.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import powercyphe.coffins.block.custom.CoffinBlock;
import powercyphe.coffins.screen.CoffinScreenHandler;
import powercyphe.coffins.sound.ModSounds;

import java.util.ArrayList;

public class CoffinBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
    private String owner = "";

    public CoffinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFIN, pos, state);
    }

    public void setItems(DefaultedList<ItemStack> items) {
        this.inventory = items;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }


    @Override
    public Text getDisplayName() {
        String translation = Text.translatable("name.coffins.coffin").getString();
        if (!this.owner.isEmpty()) {
            return Text.literal("§f" + this.owner + "'s " + translation);
        }
        return Text.literal("§f" + translation);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CoffinScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        BlockPos pos = this.getPos();
        player.getWorld().playSound(null, pos, ModSounds.COFFIN_OPEN, SoundCategory.BLOCKS, 0.5f, 1f);
        this.setOpen(true);
    }

    @Override
    public void onClose(PlayerEntity player) {
        BlockPos pos = this.getPos();
        player.getWorld().playSound(null, pos, ModSounds.COFFIN_CLOSE, SoundCategory.BLOCKS, 0.5f, 1f);
        if (!this.isRemoved()) {
            this.setOpen(false);
        }
        BlockState state = this.getCachedState();
        if (state.contains(CoffinBlock.FRAGILE) && state.get(CoffinBlock.FRAGILE)) {
            ArrayList<ItemStack> inv = new ArrayList<>();
            for (ItemStack stack : this.inventory) {
                if (!stack.isEmpty()) {
                    inv.add(stack);
                }
            }
            if (inv.isEmpty()) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5;
                if (state.getBlock() instanceof CoffinBlock) {
                    player.world.breakBlock(pos, true);
                    ((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.POOF, x, y, z, 7, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("owner", this.owner);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        this.owner = nbt.getString("owner");
    }

    public void setOpen(boolean open) {
        if (!world.isClient) {
            BlockState state = this.getCachedState();
            if (state.getBlock() instanceof CoffinBlock) {
                world.setBlockState(pos, state.with(CoffinBlock.OPEN, open), Block.NOTIFY_ALL);
            }
        }
    }

    public void setFragile(boolean fragile) {
        if (!world.isClient) {
            BlockState state = this.getCachedState();
            if (state.getBlock() instanceof CoffinBlock) {
                world.setBlockState(pos, state.with(CoffinBlock.FRAGILE, fragile), Block.NOTIFY_ALL);
            }
        }
    }
}