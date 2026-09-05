package powercyphe.coffins.client.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.menu.CoffinMenu;

public record LootButtonPayload() implements CustomPacketPayload {
    public static final Type<LootButtonPayload> TYPE = new Type<>(Coffins.id("loot_button"));
    public static final StreamCodec<ByteBuf, LootButtonPayload> CODEC = StreamCodec.unit(new LootButtonPayload());

    public static void send() {
        ClientPlayNetworking.send(new LootButtonPayload());
    }

    public static void handle(LootButtonPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();

        if (player.containerMenu instanceof CoffinMenu coffinMenu) {
            coffinMenu.collectLoot(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
