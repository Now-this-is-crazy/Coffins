package powercyphe.coffins.common.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.common.Coffins;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface CAttachmentTypes {

    AttachmentType<List<ItemStack>> POST_RESPAWN_ITEMS = register("post_respawn_items", builder ->
            builder.initializer(ArrayList::new).persistent(ItemStack.CODEC.listOf()));

    AttachmentType<List<GlobalPos>> COFFINS = register("coffins", builder ->
            builder.initializer(ArrayList::new).persistent(GlobalPos.CODEC.listOf())
                    .syncWith(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)), AttachmentSyncPredicate.targetOnly()));

    static void init() {}

    static <T> AttachmentType<T> register(String name, Consumer<AttachmentRegistry.Builder<T>> builder) {
        return AttachmentRegistry.create(Coffins.id(name), builder);
    }
}
