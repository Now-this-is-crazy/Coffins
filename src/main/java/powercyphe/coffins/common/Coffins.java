package powercyphe.coffins.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import powercyphe.coffins.common.api.CoffinsEvents;
import powercyphe.coffins.common.compat.BuiltInCoffinsCompat;
import powercyphe.coffins.common.compat.TrinketsCompatibility;
import powercyphe.coffins.common.event.GameruleSyncEvent;
import powercyphe.coffins.common.event.CoffinsRespawnEvent;
import powercyphe.coffins.common.event.RecoveryCompassPingEvent;
import powercyphe.coffins.common.init.*;
import powercyphe.coffins.common.util.NonGraveCoffinCondition;

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

        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Coffins.id("non_grave_coffin"), NonGraveCoffinCondition.CODEC);

        UseItemCallback.EVENT.register(new RecoveryCompassPingEvent());
        ServerPlayerEvents.AFTER_RESPAWN.register(new CoffinsRespawnEvent());

        ServerPlayerEvents.JOIN.register(GameruleSyncEvent::syncAll);
        GameRuleEvents.changeCallback(CGamerules.COFFIN_ROBBING).register(GameruleSyncEvent::syncCoffinRobbing);
        GameRuleEvents.changeCallback(CGamerules.TWEAK_RECOVERY_COMPASS).register(GameruleSyncEvent::syncTweakRecoveryCompass);

        BuiltInCoffinsCompat.init();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
