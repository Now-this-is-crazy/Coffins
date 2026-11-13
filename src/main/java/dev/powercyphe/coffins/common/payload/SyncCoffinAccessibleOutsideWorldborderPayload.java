package dev.powercyphe.coffins.common.payload;

import dev.powercyphe.coffins.common.Coffins;
import dev.powercyphe.coffins.common.util.CSyncedGamerules;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncCoffinAccessibleOutsideWorldborderPayload(boolean accessible) implements CustomPacketPayload {
    public static final Type<SyncCoffinAccessibleOutsideWorldborderPayload> TYPE = new Type<>(Coffins.id("sync_coffin_accessible_outside_worldborder"));
    public static final StreamCodec<ByteBuf, SyncCoffinAccessibleOutsideWorldborderPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncCoffinAccessibleOutsideWorldborderPayload::accessible, SyncCoffinAccessibleOutsideWorldborderPayload::new
    );

    @Environment(EnvType.CLIENT)
    public static void handle(SyncCoffinAccessibleOutsideWorldborderPayload payload, ClientPlayNetworking.Context context) {
        CSyncedGamerules.COFFIN_ACCESSIBLE_OUTSIDE_WORLDBORDER = payload.accessible;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
