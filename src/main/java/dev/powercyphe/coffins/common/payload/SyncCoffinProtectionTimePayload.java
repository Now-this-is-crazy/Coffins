package dev.powercyphe.coffins.common.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import dev.powercyphe.coffins.common.Coffins;
import dev.powercyphe.coffins.common.util.CSyncedGamerules;

public record SyncCoffinProtectionTimePayload(int protectionTime) implements CustomPacketPayload {
    public static final Type<SyncCoffinProtectionTimePayload> TYPE = new Type<>(Coffins.id("sync_coffin_protection_time"));
    public static final StreamCodec<ByteBuf, SyncCoffinProtectionTimePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncCoffinProtectionTimePayload::protectionTime, SyncCoffinProtectionTimePayload::new
    );

    @Environment(EnvType.CLIENT)
    public static void handle(SyncCoffinProtectionTimePayload payload, ClientPlayNetworking.Context context) {
        CSyncedGamerules.COFFIN_PROTECTION_TIME = payload.protectionTime;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
