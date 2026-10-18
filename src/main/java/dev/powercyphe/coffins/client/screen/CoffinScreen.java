package dev.powercyphe.coffins.client.screen;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import dev.powercyphe.coffins.client.payload.LootCoffinPayload;
import dev.powercyphe.coffins.common.Coffins;
import dev.powercyphe.coffins.common.block.entity.CoffinBlockEntity;
import dev.powercyphe.coffins.common.menu.CoffinMenu;

public class CoffinScreen extends AbstractContainerScreen<CoffinMenu> {
    private static final Identifier COFFIN_TEXTURE = Coffins.id("textures/gui/container/coffin.png");

    private static final Identifier SCROLLER_TEXTURE = Coffins.id("container/coffin/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Coffins.id("container/coffin/scroller_disabled");

    private static final Identifier BUTTON_INFO_TEXTURE = Coffins.id("container/coffin/button_info");
    private static final Identifier BUTTON_EXPERIENCE_TEXTURE = Coffins.id("container/coffin/button_experience");
    private static final Identifier BUTTON_ITEMS_TEXTURE = Coffins.id("container/coffin/button_items");
    private static final Identifier BUTTON_LOOT_TEXTURE = Coffins.id("container/coffin/button_loot");

    public Scroller scroller;
    private InfoButton infoButton;

    public CoffinScreen(CoffinMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 222);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.scroller = new Scroller(this.leftPos + 138, this.topPos + 17);

        this.addRenderableWidget(this.scroller);
        this.infoButton = this.addRenderableWidget(new InfoButton(this.leftPos + 155, this.topPos + 16));

        this.addRenderableWidget(new ExperienceButton(this.leftPos + 155, this.topPos + 70));
        this.addRenderableWidget(new ItemsButton(this.leftPos + 155, this.topPos + 90));
        this.addRenderableWidget(new LootButton(this.leftPos + 155, this.topPos + 110));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.infoButton != null) {
            this.infoButton.updateTooltip();
        }
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
        private final Identifier baseTexture;

        public CoffinButton(int x, int y, Identifier baseTexture, Component tooltip) {
            super(x, y, 14, 14, Component.empty());
            this.baseTexture = baseTexture;

            this.setTooltip(Tooltip.create(tooltip));
        }

        public Identifier getTexture(int mouseX, int mouseY, float a) {
            return this.isDisabled() ? this.baseTexture.withSuffix("_disabled") :
                    this.isHoveredOrFocused() ? this.baseTexture.withSuffix("_highlighted")
                            : this.baseTexture;
        }

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
        private CoffinBlockEntity.DeathData deathData;

        public InfoButton(int x, int y) {
            super(x, y, BUTTON_INFO_TEXTURE, Component.empty());
            this.updateTooltip();
        }

        public void updateTooltip() {
            this.deathData = CoffinScreen.this.menu.deathData;

            if (this.deathData != null) {
                int items = 0;
                for (ItemStack stack : CoffinScreen.this.menu.container) {
                    if (!stack.isEmpty()) {
                        items++;
                    }
                }
                int experience = CoffinScreen.this.menu.experience.get();

                this.setTooltip(Tooltip.create(
                        this.deathData.deathMessage().copy()
                                .append("\n").append(this.deathData.getFormattedTime())
                                .append("\n").append(Component.empty())
                                .append("\n").append(Component.translatable("button.coffins.info_button.items", items))
                                .append("\n").append(Component.translatable("button.coffins.info_button.experience", experience)),
                        Component.translatable("button.coffins.info_button")
                ));
            }
        }

        @Override
        public CursorType getCursorType() {
            return CursorType.DEFAULT;
        }

        @Override
        public void playDownSound(SoundManager soundManager) {}

        @Override
        public void onPress(InputWithModifiers input) {}
    }

    public class ExperienceButton extends CoffinButton {
        public ExperienceButton(int x, int y) {
            super(x, y, BUTTON_EXPERIENCE_TEXTURE, Component.translatable("button.coffins.experience_button"));
        }

        @Override
        public boolean isDisabled() {
            return this.experience() <= 0;
        }

        @Override
        public void onPress(InputWithModifiers input) {
            LootCoffinPayload.send(false, true);
        }

        public int experience() {
            return CoffinScreen.this.menu.experience.get();
        }
    }

    public class ItemsButton extends CoffinButton {
        public ItemsButton(int x, int y) {
            super(x, y, BUTTON_ITEMS_TEXTURE, Component.translatable("button.coffins.items_button"));
        }

        @Override
        public boolean isDisabled() {
            return CoffinScreen.this.menu.container.isEmpty();
        }

        @Override
        public void onPress(InputWithModifiers input) {
            LootCoffinPayload.send(true, false);
        }
    }

    public class LootButton extends CoffinButton {
        public LootButton(int x, int y) {
            super(x, y, BUTTON_LOOT_TEXTURE, Component.translatable("button.coffins.loot_button"));
        }

        @Override
        public boolean isDisabled() {
            return CoffinScreen.this.menu.container.isEmpty() && CoffinScreen.this.menu.experience.get() <= 0;
        }

        @Override
        public void onPress(InputWithModifiers input) {
            LootCoffinPayload.send(true, true);
        }
    }
}
