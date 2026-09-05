package powercyphe.coffins.common.init;

import net.minecraft.sounds.SoundEvent;
import powercyphe.coffins.common.Coffins;

public interface CSounds {

    SoundEvent COFFIN_OPEN = register("block.coffin.open");
    SoundEvent COFFIN_CLOSE = register("block.coffin.close");
    SoundEvent COFFIN_LOCKED = register("block.coffin.locked");

    SoundEvent RECOVERY_PING = register("generic.coffins.recovery_ping");
    SoundEvent RECOVERY_PING_FAIL = register("generic.coffins.recovery_ping_fail");

    static void init() {}

    static SoundEvent register(String path) {
        return SoundEvent.createVariableRangeEvent(Coffins.id(path));
    }
}
