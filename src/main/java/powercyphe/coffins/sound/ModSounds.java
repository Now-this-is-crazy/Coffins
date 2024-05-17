package powercyphe.coffins.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import powercyphe.coffins.Mod;

public class ModSounds {
    public static SoundEvent COFFIN_OPEN = registerSoundevent("coffin_open");
    public static SoundEvent COFFIN_CLOSE = registerSoundevent("coffin_close");
    public static SoundEvent COFFIN_LOCKED = registerSoundevent("coffin_locked");

    private static SoundEvent registerSoundevent(String path) {
        Identifier id = new Identifier(Mod.MOD_ID, path);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }
    public static void registerModSounds() {
        Mod.debugMessage("Registering Sounds");
    }
}
