package dev.powercyphe.coffins.client.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import dev.powercyphe.coffins.common.Coffins;
import dev.powercyphe.coffins.common.menu.CoffinMenu;

public record LootCoffinPayload(boolean items, boolean experience) implements CustomPacketPayload {
    public static final Type<LootCoffinPayload> TYPE = new Type<>(Coffins.id("loot_button"));
    public static final StreamCodec<ByteBuf, LootCoffinPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, LootCoffinPayload::items,
            ByteBufCodecs.BOOL, LootCoffinPayload::experience,
            LootCoffinPayload::new
    );

    public static void send(boolean items, boolean experience) {
        ClientPlayNetworking.send(new LootCoffinPayload(items, experience));
    }

    public static void handle(LootCoffinPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();

        if (player.containerMenu instanceof CoffinMenu coffinMenu) {
            if (payload.items) {
                coffinMenu.collectItems(player);
            }
            if (payload.experience) {
                coffinMenu.collectExperience(player);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
