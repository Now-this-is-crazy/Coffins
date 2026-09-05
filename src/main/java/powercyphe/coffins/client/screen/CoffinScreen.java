package powercyphe.coffins.client.screen;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import powercyphe.coffins.client.payload.ExperienceButtonPayload;
import powercyphe.coffins.client.payload.LootButtonPayload;
import powercyphe.coffins.common.Coffins;
import powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import powercyphe.coffins.common.menu.CoffinMenu;
import powercyphe.coffins.common.util.CUtil;

import java.util.List;

public class CoffinScreen extends AbstractContainerScreen<CoffinMenu> {
    private static final Identifier COFFIN_TEXTURE = Coffins.id("textures/gui/container/coffin.png");

    private static final Identifier SCROLLER_TEXTURE = Coffins.id("container/coffin/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Coffins.id("container/coffin/scroller_disabled");

    private static final Identifier BUTTON_INFO_TEXTURE = Coffins.id("container/coffin/button_info");
    private static final Identifier BUTTON_INFO_HIGHLIGHTED_TEXTURE = Coffins.id("container/coffin/button_info_highlighted");
    private static final Identifier BUTTON_INFO_DISABLED_TEXTURE = Coffins.id("container/coffin/button_info_disabled");

    private static final Identifier BUTTON_EXPERIENCE_TEXTURE = Coffins.id("container/coffin/button_experience");
    private static final Identifier BUTTON_EXPERIENCE_HIGHLIGHTED_TEXTURE = Coffins.id("container/coffin/button_experience_highlighted");
    private static final Identifier BUTTON_EXPERIENCE_DISABLED_TEXTURE = Coffins.id("container/coffin/button_experience_disabled");

    private static final Identifier BUTTON_LOOT_TEXTURE = Coffins.id("container/coffin/button_loot");
    private static final Identifier BUTTON_LOOT_HIGHLIGHTED_TEXTURE = Coffins.id("container/coffin/button_loot_highlighted");
    private static final Identifier BUTTON_LOOT_DISABLED_TEXTURE = Coffins.id("container/coffin/button_loot_disabled");

    public Scroller scroller;

    public CoffinScreen(CoffinMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 222);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.scroller = new Scroller(this.leftPos + 138, this.topPos + 17);

        this.addRenderableWidget(this.scroller);
        this.addRenderableWidget(new InfoButton(this.leftPos + 155, this.topPos + 16));
        this.addRenderableWidget(new ExperienceButton(this.leftPos + 155, this.topPos + 90));
        this.addRenderableWidget(new LootButton(this.leftPos + 155, this.topPos + 110));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, COFFIN_TEXTURE, this.leftPos, this.topPos - 1,
                0F, 0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        this.scroller.mouseScrolled(x, y, scrollX, scrollY);
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    public class Scroller extends AbstractWidget {
        private static final int SCROLL_LENGTH = 91;

        private boolean scrolling = false;
        private final int baseY;

        public Scroller(int x, int y) {
            super(x, y, 12, 15, Component.empty());
            this.baseY = y;
            
            this.updateScrollbar();
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.canScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE,
                    this.getX(), this.getY(), this.getWidth(), this.getHeight());

            if (this.isHovered()) {
                if (this.canScroll()) {
                    graphics.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
                } else {
                    graphics.requestCursor(CursorTypes.NOT_ALLOWED);
                }
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}

        @Override
        public void playDownSound(SoundManager soundManager) {}

        @Override
        public boolean shouldTakeFocusAfterInteraction() {
            return true;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            this.scrolling = true;
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            this.scrolling = false;
            return super.mouseReleased(event);
        }

        @Override
        public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
            this.scroll((float) -scrollY / Math.max(1, CoffinScreen.this.menu.getRows() - 6));
            return super.mouseScrolled(x, y, scrollX, scrollY);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
            if (this.scrolling) {
                int yscr = this.getY();
                int yscr2 = yscr + SCROLL_LENGTH;
                this.setScroll(((float)event.y() - yscr - 7.5F) / (yscr2 - yscr - 15.0F));
            }
            return super.mouseDragged(event, dx, dy);
        }

        public void updateScrollbar() {
            this.setRectangle(this.getWidth(), this.getHeight(),
                    this.getX(), this.baseY + (int) (SCROLL_LENGTH * this.getScroll()));
        }

        public void scroll(float amount) {
            if (this.canScroll()) {
                this.setScroll(this.getScroll() + amount);
                this.updateScrollbar();
            }
        }

        public void setScroll(float scroll) {
            CoffinScreen.this.menu.scroll = Math.clamp(scroll, 0F, 1F);
            CoffinScreen.this.menu.updateSlots();
        }

        public float getScroll() {
            return CoffinScreen.this.menu.scroll;
        }

        public boolean canScroll() {
            return CoffinScreen.this.menu.getRows() > 6;
        }
    }

    public abstract static class CoffinButton extends AbstractButton {
        public CoffinButton(int x, int y, Component tooltip) {
            super(x, y, 14, 14, Component.empty());
            this.setTooltip(Tooltip.create(tooltip));
        }

        public abstract Identifier getTexture(int mouseX, int mouseY, float a);

        public CursorType getCursorType() {
            return this.isDisabled() ? CursorTypes.NOT_ALLOWED : CursorTypes.POINTING_HAND;
        }

        @Override
        protected void handleCursor(GuiGraphicsExtractor graphics) {
            if (this.isHovered()) {
                graphics.requestCursor(this.getCursorType());
            }
        }

        public boolean isDisabled() {
            return false;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getTexture(mouseX, mouseY, a),
                    this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        @Override
        public boolean shouldTakeFocusAfterInteraction() {
            return false;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (this.isDisabled()) {
                return false;
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (this.isDisabled()) {
                return false;
            }
            return super.keyPressed(event);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
    }

    public class InfoButton extends CoffinButton {
        private boolean showingInfo = false;

        public InfoButton(int x, int y) {
            super(x, y, Component.translatable("button.coffins.info_button"));
        }

        @Override
        public Identifier getTexture(int mouseX, int mouseY, float a) {
            return this.isHoveredOrFocused()
                    ? BUTTON_INFO_HIGHLIGHTED_TEXTURE
                    : this.showingInfo
                    ? BUTTON_INFO_DISABLED_TEXTURE
                    : BUTTON_INFO_TEXTURE;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractContents(graphics, mouseX, mouseY, a);
            CoffinBlockEntity.DeathData deathData = CoffinScreen.this.menu.deathData;

            int items = 0;
            for (ItemStack stack : CoffinScreen.this.menu.container) {
                if (!stack.isEmpty()) {
                    items++;
                }
            }
            int experience = CoffinScreen.this.menu.experience.get();

            if (this.showingInfo && deathData != null) {
                List<ClientTooltipComponent> tooltip = CUtil.createTooltip(
                        deathData.deathMessage(),
                        deathData.getFormattedTime(),

                        Component.empty(),

                        Component.translatable("button.coffins.info_button.items", items),
                        Component.translatable("button.coffins.info_button.experience", experience)
                );

                graphics.tooltip(CoffinScreen.this.font, tooltip, this.getX() + 18, this.getY(),
                        DefaultTooltipPositioner.INSTANCE, null);
            }
        }

        @Override
        public void onPress(InputWithModifiers input) {
            this.showingInfo = !this.showingInfo;
        }
    }

    public class ExperienceButton extends CoffinButton {
        public ExperienceButton(int x, int y) {
            super(x, y, Component.translatable("button.coffins.experience_button"));
        }

        @Override
        public Identifier getTexture(int mouseX, int mouseY, float a) {
            return this.isDisabled()
                    ? BUTTON_EXPERIENCE_DISABLED_TEXTURE
                    : this.isHoveredOrFocused()
                    ? BUTTON_EXPERIENCE_HIGHLIGHTED_TEXTURE
                    : BUTTON_EXPERIENCE_TEXTURE;
        }

        @Override
        public boolean isDisabled() {
            return this.experience() <= 0;
        }

        @Override
        public void onPress(InputWithModifiers input) {
            ExperienceButtonPayload.send();
        }

        public int experience() {
            return CoffinScreen.this.menu.experience.get();
        }
    }

    public class LootButton extends CoffinButton {
        public LootButton(int x, int y) {
            super(x, y, Component.translatable("button.coffins.loot_button"));
        }

        @Override
        public Identifier getTexture(int mouseX, int mouseY, float a) {
            return this.isDisabled()
                    ? BUTTON_LOOT_DISABLED_TEXTURE
                    : this.isHoveredOrFocused()
                    ? BUTTON_LOOT_HIGHLIGHTED_TEXTURE
                    : BUTTON_LOOT_TEXTURE;
        }

        @Override
        public boolean isDisabled() {
            return CoffinScreen.this.menu.container.isEmpty();
        }

        @Override
        public void onPress(InputWithModifiers input) {
            LootButtonPayload.send();
        }
    }
}
