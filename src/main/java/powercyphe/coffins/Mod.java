package powercyphe.coffins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import powercyphe.coffins.block.ModBlocks;
import powercyphe.coffins.block.entity.ModBlockEntities;
import powercyphe.coffins.event.PlayerRespawnEvent;
import powercyphe.coffins.item.ModItems;
import powercyphe.coffins.modsupport.ModSupportManager;
import powercyphe.coffins.screen.ModScreenHandlers;
import powercyphe.coffins.sound.ModSounds;
import powercyphe.coffins.util.ModLootTableModifier;


public class Mod implements ModInitializer {
	public static final String MOD_ID = "coffins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final GameRules.Key<GameRules.BooleanRule> KEEP_RECOVERY_COMPASS = GameRuleRegistry.register("keepRecoveryCompass", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> SHOW_DEATH_COORDS = GameRuleRegistry.register("showDeathCoordinates", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> ALLOW_COFFIN_ROBBING = GameRuleRegistry.register("allowCoffinRobbing", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> DEATH_REQUIRES_COFFIN = GameRuleRegistry.register("deathRequiresCoffin", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModSounds.registerModSounds();
		ModLootTableModifier.modifyLootTables();

		ServerPlayerEvents.AFTER_RESPAWN.register(new PlayerRespawnEvent());

		// Call the init method on mod support classes if the mod is loaded
		ModSupportManager.modSupportClasses.forEach(clazz -> {
			if (clazz.isModLoaded()) clazz.onInit();
		});

		FabricLoader.getInstance().getModContainer(Mod.MOD_ID).ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(Mod.id("coffins-dark-menu"), modContainer, ResourcePackActivationType.NORMAL));
		FabricLoader.getInstance().getModContainer(Mod.MOD_ID).ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(Mod.id("coffins-old-textures"), modContainer, ResourcePackActivationType.NORMAL));
	}

	public static void debugMessage(String message) {
		Mod.LOGGER.info("[" + Mod.MOD_ID + "] " + message);
	}

	public static Identifier id(String value) {
		return new Identifier(MOD_ID, value);
	}
}
