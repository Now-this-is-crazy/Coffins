package dev.powercyphe.coffins.common.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import dev.powercyphe.coffins.client.screen.PlayerCoffinsScreen;
import dev.powercyphe.coffins.common.Coffins;

import java.util.List;

public record OpenPlayerCoffinsPayload(Component playerName, List<GlobalPos> coffins) implements CustomPacketPayload {
    public static final Type<OpenPlayerCoffinsPayload> TYPE = new Type<>(Coffins.id("open_player_coffins"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPlayerCoffinsPayload> CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, OpenPlayerCoffinsPayload::playerName,
            ByteBufCodecs.<ByteBuf, GlobalPos>list().apply(GlobalPos.STREAM_CODEC), OpenPlayerCoffinsPayload::coffins,
            OpenPlayerCoffinsPayload::new
    );

    public static void send(ServerPlayer player, ServerPlayer other, List<GlobalPos> coffins) {
        ServerPlayNetworking.send(player, new OpenPlayerCoffinsPayload(other.getDisplayName(), coffins));
    }

    @Environment(EnvType.CLIENT)
    public static void handle(OpenPlayerCoffinsPayload payload, ClientPlayNetworking.Context context) {
        Minecraft minecraft = context.client();
        minecraft.setScreenAndShow(new PlayerCoffinsScreen(payload.playerName, payload.coffins));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
