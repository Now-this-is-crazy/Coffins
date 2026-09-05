package powercyphe.coffins.common.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import powercyphe.coffins.client.payload.ExperienceButtonPayload;
import powercyphe.coffins.client.payload.LootButtonPayload;
import powercyphe.coffins.common.payload.CoffinRobbingSyncPayload;
import powercyphe.coffins.common.payload.CoffinDeathDataPayload;
import powercyphe.coffins.common.payload.TweakRecoveryCompassSyncPayload;

public interface CPayloads {

    static void init() {
        PayloadTypeRegistry.clientboundPlay().register(CoffinRobbingSyncPayload.TYPE, CoffinRobbingSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TweakRecoveryCompassSyncPayload.TYPE, TweakRecoveryCompassSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(CoffinDeathDataPayload.TYPE, CoffinDeathDataPayload.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(ExperienceButtonPayload.TYPE, ExperienceButtonPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(LootButtonPayload.TYPE, LootButtonPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ExperienceButtonPayload.TYPE, ExperienceButtonPayload::handle);
        ServerPlayNetworking.registerGlobalReceiver(LootButtonPayload.TYPE, LootButtonPayload::handle);
    }

    static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(CoffinRobbingSyncPayload.TYPE, CoffinRobbingSyncPayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(TweakRecoveryCompassSyncPayload.TYPE, TweakRecoveryCompassSyncPayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(CoffinDeathDataPayload.TYPE, CoffinDeathDataPayload::handle);
    }
}
