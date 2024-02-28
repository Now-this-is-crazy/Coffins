package powercyphe.coffins;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import powercyphe.coffins.screen.CoffinScreen;
import powercyphe.coffins.screen.ModScreenHandlers;

public class ModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.COFFIN_SCREEN_HANDLER, CoffinScreen::new);

    }
}
