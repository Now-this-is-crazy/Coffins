package dev.powercyphe.coffins.common.event;

import dev.powercyphe.coffins.common.payload.SyncCoffinAccessibleOutsideWorldborderPayload;
import dev.powercyphe.coffins.common.payload.SyncCoffinGlowDistancePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import dev.powercyphe.coffins.common.init.CGamerules;
import dev.powercyphe.coffins.common.payload.SyncCoffinProtectionTimePayload;
import dev.powercyphe.coffins.common.payload.SyncTweakRecoveryCompassPayload;

import java.util.function.Supplier;

public class GameruleSyncEvent {

    public static void syncAll(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        GameRules rules = server.getGameRules();

        syncCoffinProtectionTime(rules.get(CGamerules.COFFIN_PROTECTION_TIME), server);
        syncCoffinGlowDistance(rules.get(CGamerules.COFFIN_GLOW_DISTANCE), server);
        syncCoffinAccessibleOutsideWorldborder(rules.get(CGamerules.COFFIN_ACCESSIBLE_OUTSIDE_WORLDBORDER), server);
        syncTweakRecoveryCompass(rules.get(CGamerules.TWEAK_RECOVERY_COMPASS), server);
    }

    public static void syncCoffinProtectionTime(Integer value, MinecraftServer server) {
        sendToAll(server, () -> new SyncCoffinProtectionTimePayload(value));
    }

    public static void syncCoffinGlowDistance(Integer value, MinecraftServer server) {
        sendToAll(server, () -> new SyncCoffinGlowDistancePayload(value));
    }

    public static void syncCoffinAccessibleOutsideWorldborder(Boolean value, MinecraftServer server) {
        sendToAll(server, () -> new SyncCoffinAccessibleOutsideWorldborderPayload(value));
    }

    public static void syncTweakRecoveryCompass(Boolean value, MinecraftServer server) {
        sendToAll(server, () -> new SyncTweakRecoveryCompassPayload(value));
    }

    public static <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, Supplier<T> payloadSup) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payloadSup.get());
        }
    }
}
