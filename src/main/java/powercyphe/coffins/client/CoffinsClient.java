package powercyphe.coffins.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import powercyphe.coffins.client.event.RenderCoffinOutlineEvent;
import powercyphe.coffins.client.init.CPipelines;
import powercyphe.coffins.client.render.CoffinBlockEntityRenderer;
import powercyphe.coffins.client.screen.CoffinScreen;
import powercyphe.coffins.common.init.CBlockEntityTypes;
import powercyphe.coffins.common.init.CMenuTypes;
import powercyphe.coffins.common.init.CParticleTypes;
import powercyphe.coffins.common.init.CPayloads;

import static powercyphe.coffins.common.Coffins.*;

public class CoffinsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CPipelines.init();
        CPayloads.initClient();
        CParticleTypes.initClient();

        BlockEntityRenderers.register(CBlockEntityTypes.COFFIN, CoffinBlockEntityRenderer::new);
        MenuScreens.register(CMenuTypes.COFFIN, CoffinScreen::new);

        LevelRenderEvents.END_EXTRACTION.register(new RenderCoffinOutlineEvent());

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(this::registerResourcePacks);
    }

    public void registerResourcePacks(ModContainer container) {
        ResourceLoader.registerBuiltinPack(id("dark_mode_coffin"), container,
                Component.literal("Dark Mode Coffin"), PackActivationType.NORMAL);
    }
}
