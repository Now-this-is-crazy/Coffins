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

public record SyncTweakRecoveryCompassPayload(boolean tweak) implements CustomPacketPayload {
    public static final Type<SyncTweakRecoveryCompassPayload> TYPE = new Type<>(Coffins.id("sync_tweak_recovery_compass"));
    public static final StreamCodec<ByteBuf, SyncTweakRecoveryCompassPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncTweakRecoveryCompassPayload::tweak, SyncTweakRecoveryCompassPayload::new
    );

    @Environment(EnvType.CLIENT)
    public static void handle(SyncTweakRecoveryCompassPayload payload, ClientPlayNetworking.Context context) {
        CSyncedGamerules.TWEAK_RECOVERY_COMPASS = payload.tweak;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
