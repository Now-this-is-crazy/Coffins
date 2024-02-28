package powercyphe.coffins.screen;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import powercyphe.coffins.Mod;

public class ModScreenHandlers {
    public static ScreenHandlerType<CoffinScreenHandler> COFFIN_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        Mod.debugMessage("Registering Screen Handlers");
        COFFIN_SCREEN_HANDLER = Registry.register(Registry.SCREEN_HANDLER,
                new Identifier(Mod.MOD_ID, "coffin"),
                new ScreenHandlerType<>(CoffinScreenHandler::new));
    }
}
