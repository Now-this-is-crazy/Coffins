package dev.powercyphe.coffins.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import dev.powercyphe.coffins.client.init.CPipelines;
import dev.powercyphe.coffins.client.payload.TeleportToCoffinPayload;

import java.util.List;
import java.util.function.Supplier;

public class PlayerCoffinsScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private LinearLayout buttons;
    private ScrollableLayout scrollable;

    private final List<GlobalPos> coffins;

    public PlayerCoffinsScreen(Component playerName, List<GlobalPos> coffins) {
        super(Component.translatable("menu.coffins.player_coffins", playerName));
        this.coffins = coffins;
    }

    @Override
    protected void init() {
        super.init();
        this.layout.addTitleHeader(this.title, this.font);

        this.buttons = LinearLayout.vertical();
        this.buttons.spacing(6);
        this.addCoffins();

        this.scrollable = new ScrollableLayout(this.minecraft, this.buttons, this.layout.getContentHeight());
        this.layout.addToContents(this.scrollable);

        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }

    public void addCoffins() {
        for (var pos : this.coffins) {
            LinearLayout coffin = LinearLayout.horizontal();
            coffin.spacing(4);

            coffin.addChild(new CoffinInfo(pos, Component.empty()));
            coffin.addChild(new CoffinButton(Identifier.withDefaultNamespace("textures/item/ender_pearl.png"),
                    Component.translatable("button.coffins.teleport_to"), _ -> {
                TeleportToCoffinPayload.send(pos);
                this.minecraft.gui.setScreen(null);
            }));
            this.buttons.addChild(coffin);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float a) {
        super.extractBackground(gui, mouseX, mouseY, a);
        extractMenuBackgroundTexture(gui, MENU_BACKGROUND, this.scrollable.getX(), this.scrollable.getY() - 4,
                0, 0, this.scrollable.getWidth(), this.scrollable.getHeight() + 8);

        gui.enableScissor(
                this.scrollable.getX(), this.scrollable.getY(),
                this.scrollable.getX() + this.scrollable.getWidth(),
                this.scrollable.getY() + this.scrollable.getHeight()
        );
        this.buttons.visitChildren(el -> {
                if (!(el instanceof AbstractWidget)) {
                    extractMenuBackgroundTexture(gui, MENU_BACKGROUND, el.getX(), el.getY(),
                            0, 0, el.getWidth(), el.getHeight());
                }
        });
        gui.disableScissor();
    }

    @Override
    protected void repositionElements() {
        this.scrollable.arrangeElements();
        this.scrollable.setMaxHeight(this.layout.getContentHeight());

        this.layout.arrangeElements();
    }

    public class CoffinButton extends Button {
        private final Identifier texture;

        public CoffinButton(Identifier texture, Component message, Button.OnPress onPress) {
            super(0, 0, 24, 24, message, onPress, Supplier::get);
            this.texture = texture;
            this.setTooltip(Tooltip.create(message));
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor gui, int mouseX, int mouseY, float a) {
            if (this.isHoveredOrFocused()) {
                gui.blit(CPipelines.GUI_TEXTURED_WHITE, this.texture, this.getX() + 2, this.getY() + 2, 0, 0, 20, 20, 16, 16, 16, 16, -1);
            } else {
                gui.blit(RenderPipelines.GUI_TEXTURED, this.texture, this.getX() + 4, this.getY() + 4, 0, 0, 16, 16, 16, 16, -1);
            }
        }
    }

    public class CoffinInfo extends AbstractWidget {
        public final GlobalPos pos;

        public CoffinInfo(GlobalPos pos, Component message) {
            super(0, 0, 128, 24, message);
            this.pos = pos;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float a) {
            gui.text(PlayerCoffinsScreen.this.font, "[" + pos.pos().getX() + " " + pos.pos().getY() + " " + pos.pos().getZ() + "]",
                    this.getX() + 2, this.getY() + 2, -1);
            gui.text(PlayerCoffinsScreen.this.font, pos.dimension().identifier().toString(),
                    this.getX() + 2, this.getY() + 12, -1);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
    }
}
