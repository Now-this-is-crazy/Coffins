package powercyphe.coffins.common.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.util.CSyncedValues;

public record CoffinRobbingSyncPayload(boolean robbing) implements CustomPacketPayload {
    public static final Type<CoffinRobbingSyncPayload> TYPE = new Type<>(Coffins.id("coffin_robbing_sync"));
    public static final StreamCodec<ByteBuf, CoffinRobbingSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CoffinRobbingSyncPayload::robbing, CoffinRobbingSyncPayload::new
    );

    public static void handle(CoffinRobbingSyncPayload payload, ClientPlayNetworking.Context context) {
        CSyncedValues.COFFIN_ROBBING = payload.robbing;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
