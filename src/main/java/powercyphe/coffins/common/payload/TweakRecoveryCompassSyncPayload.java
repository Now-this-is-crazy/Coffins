package powercyphe.coffins.common.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.util.CSyncedValues;

public record TweakRecoveryCompassSyncPayload(boolean tweak) implements CustomPacketPayload {
    public static final Type<TweakRecoveryCompassSyncPayload> TYPE = new Type<>(Coffins.id("tweak_recovery_compass_sync"));
    public static final StreamCodec<ByteBuf, TweakRecoveryCompassSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, TweakRecoveryCompassSyncPayload::tweak, TweakRecoveryCompassSyncPayload::new
    );

    public static void handle(TweakRecoveryCompassSyncPayload payload, ClientPlayNetworking.Context context) {
        CSyncedValues.TWEAK_RECOVERY_COMPASS = payload.tweak;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
