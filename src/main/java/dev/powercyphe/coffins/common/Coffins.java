package dev.powercyphe.coffins.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import dev.powercyphe.coffins.common.command.CoffinsCommand;
import dev.powercyphe.coffins.common.compat.BuiltInCoffinsCompat;
import dev.powercyphe.coffins.common.event.GameruleSyncEvent;
import dev.powercyphe.coffins.common.event.CoffinsRespawnEvent;
import dev.powercyphe.coffins.common.event.RecoveryCompassPingEvent;
import dev.powercyphe.coffins.common.init.*;
import dev.powercyphe.coffins.common.util.NonGraveCoffinCondition;

public class Coffins implements ModInitializer {
    public static final String MOD_ID = "coffins";

    @Override
    public void onInitialize() {
        CRegistries.init();
        CItems.init();
        CBlocks.init();
        CBlockEntityTypes.init();
        CAttachmentTypes.init();
        CMenuTypes.init();
        CPayloads.init();
        CSavedSlotProviders.init();
        CGamerules.init();
        CSounds.init();
        CParticleTypes.init();

        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Coffins.id("non_grave_coffin"), NonGraveCoffinCondition.CODEC);

        UseItemCallback.EVENT.register(new RecoveryCompassPingEvent());
        ServerPlayerEvents.AFTER_RESPAWN.register(new CoffinsRespawnEvent());

        ServerPlayerEvents.JOIN.register(GameruleSyncEvent::syncAll);
        GameRuleEvents.changeCallback(CGamerules.COFFIN_PROTECTION_TIME).register(GameruleSyncEvent::syncCoffinProtectionTime);
        GameRuleEvents.changeCallback(CGamerules.COFFIN_GLOW_DISTANCE).register(GameruleSyncEvent::syncCoffinGlowDistance);
        GameRuleEvents.changeCallback(CGamerules.COFFIN_ACCESSIBLE_OUTSIDE_WORLDBORDER).register(GameruleSyncEvent::syncCoffinAccessibleOutsideWorldborder);
        GameRuleEvents.changeCallback(CGamerules.TWEAK_RECOVERY_COMPASS).register(GameruleSyncEvent::syncTweakRecoveryCompass);

        CommandRegistrationCallback.EVENT.register(new CoffinsCommand());

        BuiltInCoffinsCompat.init();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
