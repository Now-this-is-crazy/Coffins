package powercyphe.coffins.client.payload;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.menu.CoffinMenu;

public record ExperienceButtonPayload() implements CustomPacketPayload {
    public static final Type<ExperienceButtonPayload> TYPE = new Type<>(Coffins.id("experience_button"));
    public static final StreamCodec<ByteBuf, ExperienceButtonPayload> CODEC = StreamCodec.unit(new ExperienceButtonPayload());

    public static void send() {
        ClientPlayNetworking.send(new ExperienceButtonPayload());
    }

    public static void handle(ExperienceButtonPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();

        if (player.containerMenu instanceof CoffinMenu coffinMenu) {
            coffinMenu.collectExperience(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
