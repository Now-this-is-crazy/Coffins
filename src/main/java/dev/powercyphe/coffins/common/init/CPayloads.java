package dev.powercyphe.coffins.common.init;

import dev.powercyphe.coffins.common.payload.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import dev.powercyphe.coffins.client.payload.ExperienceButtonPayload;
import dev.powercyphe.coffins.client.payload.LootButtonPayload;
import dev.powercyphe.coffins.client.payload.TeleportToCoffinPayload;

public interface CPayloads {

    static void init() {
        PayloadTypeRegistry.clientboundPlay().register(SyncCoffinProtectionTimePayload.TYPE, SyncCoffinProtectionTimePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncCoffinGlowDistancePayload.TYPE, SyncCoffinGlowDistancePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncTweakRecoveryCompassPayload.TYPE, SyncTweakRecoveryCompassPayload.CODEC);

        PayloadTypeRegistry.clientboundPlay().register(CoffinDeathDataPayload.TYPE, CoffinDeathDataPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenPlayerCoffinsPayload.TYPE, OpenPlayerCoffinsPayload.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(ExperienceButtonPayload.TYPE, ExperienceButtonPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(LootButtonPayload.TYPE, LootButtonPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TeleportToCoffinPayload.TYPE, TeleportToCoffinPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ExperienceButtonPayload.TYPE, ExperienceButtonPayload::handle);
        ServerPlayNetworking.registerGlobalReceiver(LootButtonPayload.TYPE, LootButtonPayload::handle);
        ServerPlayNetworking.registerGlobalReceiver(TeleportToCoffinPayload.TYPE, TeleportToCoffinPayload::handle);
    }

    static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncCoffinProtectionTimePayload.TYPE, SyncCoffinProtectionTimePayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncCoffinGlowDistancePayload.TYPE, SyncCoffinGlowDistancePayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncTweakRecoveryCompassPayload.TYPE, SyncTweakRecoveryCompassPayload::handle);

        ClientPlayNetworking.registerGlobalReceiver(CoffinDeathDataPayload.TYPE, CoffinDeathDataPayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(OpenPlayerCoffinsPayload.TYPE, OpenPlayerCoffinsPayload::handle);
    }
}
