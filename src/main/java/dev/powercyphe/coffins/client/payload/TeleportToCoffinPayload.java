package dev.powercyphe.coffins.client.payload;

import dev.powercyphe.coffins.common.util.CUtil;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import dev.powercyphe.coffins.common.Coffins;

public record TeleportToCoffinPayload(GlobalPos coffinPos) implements CustomPacketPayload {
    public static final Type<TeleportToCoffinPayload> TYPE = new Type<>(Coffins.id("teleport_to_coffin"));
    public static final StreamCodec<ByteBuf, TeleportToCoffinPayload> CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, TeleportToCoffinPayload::coffinPos, TeleportToCoffinPayload::new);

    public static void send(GlobalPos coffinPos) {
        ClientPlayNetworking.send(new TeleportToCoffinPayload(coffinPos));
    }

    public static void handle(TeleportToCoffinPayload payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.server();

        ServerPlayer player = context.player();
        GlobalPos coffinPos = payload.coffinPos;

        if (player.permissions().hasPermission(Permissions.COMMANDS_ADMIN)) {
            ServerLevel level = server.getLevel(coffinPos.dimension());

            if (level != null) {
                Vec3 tpPos = CUtil.centerPos(coffinPos.pos());

                player.teleportTo(level, tpPos.x(), tpPos.y(), tpPos.z(),
                        Relative.ROTATION, 0, 0, false);
                level.playSound(null, coffinPos.pos(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 0.5F, 1F);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
