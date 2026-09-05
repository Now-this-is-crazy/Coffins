package powercyphe.coffins.common.payload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import powercyphe.coffins.common.menu.CoffinMenu;

public record CoffinDeathDataPayload(CoffinBlockEntity.DeathData data) implements CustomPacketPayload {
    public static final Type<CoffinDeathDataPayload> TYPE = new Type<>(Coffins.id("coffin_death_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CoffinDeathDataPayload> CODEC = StreamCodec.composite(
            CoffinBlockEntity.DeathData.STREAM_CODEC, CoffinDeathDataPayload::data, CoffinDeathDataPayload::new
    );

    public static void send(ServerPlayer player, CoffinBlockEntity.DeathData data) {
        ServerPlayNetworking.send(player, new CoffinDeathDataPayload(data));
    }

    public static void handle(CoffinDeathDataPayload payload, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();

        if (player.containerMenu instanceof CoffinMenu coffinMenu) {
            coffinMenu.deathData = payload.data;

        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
