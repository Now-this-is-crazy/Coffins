package powercyphe.coffins.common.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import powercyphe.coffins.common.init.CGamerules;
import powercyphe.coffins.common.payload.CoffinRobbingSyncPayload;
import powercyphe.coffins.common.payload.TweakRecoveryCompassSyncPayload;

import java.util.function.Supplier;

public class GameruleSyncEvent {

    public static void syncAll(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        GameRules rules = server.getGameRules();

        syncCoffinRobbing(rules.get(CGamerules.COFFIN_ROBBING), server);
        syncTweakRecoveryCompass(rules.get(CGamerules.TWEAK_RECOVERY_COMPASS), server);
    }

    public static void syncCoffinRobbing(Boolean value, MinecraftServer server) {
        sendToAll(server, () -> new CoffinRobbingSyncPayload(value));
    }

    public static void syncTweakRecoveryCompass(Boolean value, MinecraftServer server) {
        sendToAll(server, () -> new TweakRecoveryCompassSyncPayload(value));
    }

    public static <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, Supplier<T> payloadSup) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payloadSup.get());
        }
    }
}
