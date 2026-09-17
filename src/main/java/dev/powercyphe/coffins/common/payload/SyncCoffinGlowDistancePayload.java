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

public record SyncCoffinGlowDistancePayload(int glowDistance) implements CustomPacketPayload {
    public static final Type<SyncCoffinGlowDistancePayload> TYPE = new Type<>(Coffins.id("sync_coffin_glow_distance"));
    public static final StreamCodec<ByteBuf, SyncCoffinGlowDistancePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncCoffinGlowDistancePayload::glowDistance, SyncCoffinGlowDistancePayload::new
    );

    @Environment(EnvType.CLIENT)
    public static void handle(SyncCoffinGlowDistancePayload payload, ClientPlayNetworking.Context context) {
        CSyncedGamerules.COFFIN_GLOW_DISTANE = payload.glowDistance;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
