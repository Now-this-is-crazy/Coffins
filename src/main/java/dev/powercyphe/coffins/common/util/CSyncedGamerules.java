package dev.powercyphe.coffins.common.util;

import dev.powercyphe.coffins.common.init.CGamerules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CSyncedGamerules {
    public static int COFFIN_PROTECTION_TIME = -1;
    public static int COFFIN_GLOW_DISTANE = -1;
    public static boolean TWEAK_RECOVERY_COMPASS = true;

    public static int getCoffinProtectionTime(Level level) {
        return (level instanceof ServerLevel serverLevel
                ? serverLevel.getGameRules().get(CGamerules.COFFIN_PROTECTION_TIME)
                : CSyncedGamerules.COFFIN_PROTECTION_TIME
        );
    }
}
