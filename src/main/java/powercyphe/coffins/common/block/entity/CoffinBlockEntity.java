package powercyphe.coffins.common.block.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import powercyphe.coffins.common.api.CoffinsEvents;
import powercyphe.coffins.common.block.CoffinBlock;
import powercyphe.coffins.common.init.CBlockEntityTypes;
import powercyphe.coffins.common.init.CGamerules;
import powercyphe.coffins.common.init.CSounds;
import powercyphe.coffins.common.menu.CoffinMenu;
import powercyphe.coffins.common.util.CSyncedValues;
import powercyphe.coffins.common.util.CUtil;
import powercyphe.coffins.common.menu.slot.InventorySlotProvider;
import powercyphe.coffins.common.menu.slot.SavedSlotMap;
import powercyphe.coffins.common.menu.slot.SavedSlotProvider;
import powercyphe.coffins.mixin.accessor.BaseContainerBlockEntityAccessor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class CoffinBlockEntity extends RandomizableContainerBlockEntity {
    public static final int DEFAULT_INVENTORY_SIZE = 54;
    public static final int DEFAULT_GRAVE_SIZE = 42;

    public static final int COFFIN_GLOW_DISTANCE = 64;

    public static final String ITEMS = "Items";
    public static final String EXPERIENCE = "Experience";

    public static final String IS_GRAVE = "IsGrave";
    public static final String SAVED_SLOTS = "SavedSlots";

    public static final String OWNER = "Owner";
    public static final String DEATH_DATA = "DeathData";

    private NonNullList<ItemStack> items;
    private int experience = 0;

    private boolean isGrave = false;
    private SavedSlotMap savedSlots = SavedSlotMap.create();

    private UUID owner;
    @Nullable
    private DeathData deathData;

    public CoffinBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(CBlockEntityTypes.COFFIN, worldPosition, blockState);
        this.setItems(NonNullList.withSize(DEFAULT_INVENTORY_SIZE, ItemStack.EMPTY));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.isGrave = input.getBooleanOr(IS_GRAVE, false);
        this.savedSlots = input.read(SAVED_SLOTS, SavedSlotMap.CODEC).orElse(SavedSlotMap.create());

        this.experience = input.getIntOr(EXPERIENCE, 0);
        this.owner = input.read(OWNER, UUIDUtil.CODEC).orElse(null);
        this.deathData = input.read(DEATH_DATA, DeathData.CODEC).orElse(null);

        CUtil.loadInventory(input, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean(IS_GRAVE, this.isGrave);
        output.store(SAVED_SLOTS, SavedSlotMap.CODEC, this.savedSlots);

        output.putInt(EXPERIENCE, this.experience);
        if (this.owner != null) {
            output.store(OWNER, UUIDUtil.CODEC, this.owner);
        }
        if (this.deathData != null) {
            output.store(DEATH_DATA, DeathData.CODEC, this.deathData);
        }

        CUtil.saveInventory(output, this);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.getLevel() instanceof ServerLevel serverLevel) {
            ExperienceOrb.award(serverLevel, pos.getCenter(), this.getExperience());

            if (this.getOwner() != null) {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(this.getOwner());
                if (player != null) {
                    this.setOwner(null);
                    CUtil.validateCoffins(player);
                }
            }
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        tag.putBoolean(IS_GRAVE, this.isGrave);
        tag.storeNullable(OWNER, UUIDUtil.CODEC, this.owner);

        return tag;
    }

    public GlobalPos getPos() {
        return this.hasLevel() ? new GlobalPos(this.getLevel().dimension(), this.getBlockPos()) : null;
    }

    public boolean shouldDisappear() {
        return this.isGrave() && this.isEmpty();
    }

    public boolean shouldGlow(Entity viewer) {
        return this.isGrave() && this.isOwner(viewer)
                && Math.sqrt(viewer.distanceToSqr(this.getBlockPos().getCenter())) < COFFIN_GLOW_DISTANCE;
    }

    public int getOutlineColor(Entity viewer, float partialTicks) {
        float dis = (float) viewer.getEyePosition(partialTicks).distanceTo(this.getBlockPos().getCenter());

        float alpha = 1F;
        if (dis > 16) {
           alpha = Ease.inOutSine(1F - (dis - 16) / (float) COFFIN_GLOW_DISTANCE);
        }

        return ARGB.color(alpha, 0x60f5fa);
    }

    public void setOwner(Entity entity) {
        this.owner = entity != null ? entity.getUUID() : null;
        this.setChanged();
    }

    public UUID getOwner() {
        return this.owner;
    }

    public boolean isOwner(Entity entity) {
        return entity != null && entity.getUUID().equals(this.getOwner());
    }

    public void setDeathData(@Nullable DeathData deathData) {
        this.deathData = deathData;
        this.setChanged();
    }

    public @Nullable DeathData getDeathData() {
        return this.deathData;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        this.setChanged();
    }

    public int getExperience() {
        return this.experience;
    }

    public boolean isGrave() {
        return this.isGrave;
    }

    public void setGrave(ServerPlayer owner) {
        this.isGrave = true;

        this.setOwner(owner);
        this.setDeathData(DeathData.create(owner));

        var ownerName = owner.getDisplayName();
        this.setName(
                ownerName.copy().append(ownerName.getString().toLowerCase().endsWith("s") ? "' " : "'s ")
                        .append(this.getDefaultName())
        );

        CUtil.validateCoffins(owner);
        CUtil.addToCoffins(owner, this.getPos());

        this.setChanged();
    }

    public void setName(Component name) {
        ((BaseContainerBlockEntityAccessor) this).coffins$setContainerName(name);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.savedSlots.clear();
        this.setChanged();
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItemsAfterLoad(NonNullList<ItemStack> items) {
        this.items = items;
        this.setChanged();
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
        this.savedSlots.clear();
        this.setChanged();
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        super.setItem(slot, itemStack);
        this.savedSlots.remove(slot);
    }

    public void forEachItem(Consumer<Pair<ItemStack, Optional<SavedSlotProvider>>> consumer) {
        for (int index = 0; index < this.getContainerSize(); index++) {
            ItemStack stack = this.getItem(index);
            SavedSlotProvider provider = this.getSavedSlotProvider(index);

            if (!stack.isEmpty()) {
                consumer.accept(new Pair<>(stack, Optional.ofNullable(provider)));
            }
        }
    }

    @Nullable
    public SavedSlotProvider getSavedSlotProvider(int slot) {
        if (this.hasSavedSlot(slot)) {
            return this.getSavedSlot(slot);
        }
        return null;
    }

    public boolean hasSavedSlot(int slot) {
        return this.savedSlots.has(slot);
    }

    public SavedSlotProvider getSavedSlot(int slot) {
        return this.savedSlots.get(slot);
    }

    public void addSavedSlot(int slot, SavedSlotProvider provider) {
        this.savedSlots.put(slot, provider);
        this.setChanged();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (this.canOpen(player)) {
            this.unpackLootTable(inventory.player);
            return this.createMenu(containerId, inventory);
        }

        if (!player.isSpectator()) {
            player.sendOverlayMessage(Component.translatable("container.isLocked", this.getDisplayName()));
            if (this.hasLevel() && !this.getLevel().isClientSide()) {
                this.playSound(CSounds.COFFIN_LOCKED);
            }
        }

        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return this.isGrave() ? new CoffinMenu(containerId, inventory, this)
                : ChestMenu.sixRows(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return this.getItems().size();
    }

    @Override
    public void startOpen(ContainerUser user) {
        super.startOpen(user);

        this.playSound(CSounds.COFFIN_OPEN);
        this.setOpen(true);
    }

    @Override
    public void stopOpen(ContainerUser user) {
        super.stopOpen(user);

        this.playSound(CSounds.COFFIN_CLOSE);
        this.setOpen(false);

        if (this.shouldDisappear() && this.getLevel() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.getBlockPos().getCenter();

            serverLevel.destroyBlock(this.getBlockPos(), false, user.getLivingEntity());
            serverLevel.sendParticles(ParticleTypes.POOF, pos.x(), pos.y(), pos.z(), 14, 0, 0.2, 0, 0.07);
        }
    }

    @Override
    public boolean canOpen(Player player) {
        return super.canOpen(player) && (this.isOwner(player) ||
                !this.isGrave() || (player.level() instanceof ServerLevel serverLevel
                ? serverLevel.getGameRules().get(CGamerules.COFFIN_ROBBING) : CSyncedValues.COFFIN_ROBBING));
    }

    public void setOpen(boolean open) {
        if (this.hasLevel()) {
            this.getLevel().setBlockAndUpdate(this.getBlockPos(),
                    this.getBlockState().setValue(CoffinBlock.OPEN, open));
        }
    }

    public void playSound(SoundEvent soundEvent) {
        if (this.hasLevel()) {
            Level l = this.getLevel();
            l.playSound(null, this.getBlockPos(), soundEvent, SoundSource.BLOCKS,
                    0.5F, 0.9F + Mth.randomBetween(l.getRandom(), 0F, 0.2F));
        }
    }

    public record DeathData(Component deathMessage, long time) {
        public static final Codec<DeathData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ComponentSerialization.CODEC.fieldOf("deathMessage").forGetter(DeathData::deathMessage),
                        Codec.LONG.optionalFieldOf("time", 0L).forGetter(DeathData::time)
                ).apply(instance, DeathData::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, DeathData> STREAM_CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC, DeathData::deathMessage,
                ByteBufCodecs.LONG, DeathData::time,
                DeathData::new
        );

        public static DeathData create(ServerPlayer player) {
            return new DeathData(player.getCombatTracker().getDeathMessage(), System.currentTimeMillis());
        }

        public Component getFormattedTime() {
            Date date = new Date(this.time);

            int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
            String dSuffix = switch (day % 10) {
                case 1 -> "'st'";
                case 2 -> "'nd'";
                case 3 -> "'rd'";
                default -> "'th'";
            };

            return Component.literal(new SimpleDateFormat("MMM d" + dSuffix + " yyy, HH:mm").format(date));
        }
    }
}
