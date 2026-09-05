package powercyphe.coffins.common.init;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.*;
import powercyphe.coffins.common.Coffins;

public interface CGamerules {

    GameRule<Boolean> COFFIN_ROBBING = registerBoolean("coffin_robbing", GameRuleCategory.PLAYER, true);
    GameRule<Integer> COFFIN_STORED_EXPERIENCE = registerInt("coffin_stored_experience", GameRuleCategory.PLAYER, 0, 100, 100);

    GameRule<Boolean> TWEAK_RECOVERY_COMPASS = registerBoolean("tweak_recovery_compass", GameRuleCategory.PLAYER, true);
    GameRule<Boolean> KEEP_POST_RESPAWN_ITEMS = registerBoolean("keep_post_respawn_items", GameRuleCategory.PLAYER, true);

    static void init() {}

    static GameRule<Boolean> registerBoolean(String name, GameRuleCategory category, boolean defaultValue) {
        return register(name, new GameRule<>(category, GameRuleType.BOOL, BoolArgumentType.bool(),
                        GameRuleTypeVisitor::visitBoolean, Codec.BOOL, bl -> bl ? 1 : 0, defaultValue, FeatureFlagSet.of()));
    }

    static GameRule<Integer> registerInt(String name, GameRuleCategory category, int min, int max, int defaultValue) {
        return register(name, new GameRule<>(category, GameRuleType.INT, IntegerArgumentType.integer(min, max),
                        GameRuleTypeVisitor::visitInteger, Codec.INT, Integer::intValue, defaultValue, FeatureFlagSet.of()));
    }

    static <T> GameRule<T> register(String name, GameRule<T> gameRule) {
        return Registry.register(BuiltInRegistries.GAME_RULE, Coffins.id(name), gameRule);
    }
}
