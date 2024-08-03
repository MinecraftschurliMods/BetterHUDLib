package com.github.minecraftschurlimods.betterhudlib;

import com.github.minecraftschurlimods.betterhudlib.mixin.GuiAccessor;
import com.github.minecraftschurlimods.betterhudlib.mixin.GuiLayerManagerAccessor;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class HUDManagerScreen extends Screen {

    HUDManagerScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;
        for (GuiLayerManager.NamedLayer entry : getGuiLayers(minecraft.gui)) {
            if (entry.layer() instanceof HUDElement element) {
                addRenderableWidget(new HUDElementWrapper(Component.translatable(Util.makeDescriptionId("hud_element", entry.name())), element));
            } else {
                addRenderableOnly(new StandardHUDElement(entry));
            }
        }
    }

    private List<GuiLayerManager.NamedLayer> getGuiLayers(Gui gui) {
        return ((GuiLayerManagerAccessor) ((GuiAccessor) gui).getLayerManager()).getLayers();
    }

    @Override
    public void onClose() {
        for (GuiEventListener child : children()) {
            if (child instanceof HUDElementWrapper wrapper) {
                wrapper.element.save();
            }
        }
        super.onClose();
    }

    private interface HUDWidget extends Renderable {

        @Override
        default void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            getHUDElement().render(graphics, new DeltaTracker() {
                @Override
                public float getGameTimeDeltaTicks() {
                    return pPartialTick;
                }

                @Override
                public float getGameTimeDeltaPartialTick(boolean b) {
                    return pPartialTick;
                }

                @Override
                public float getRealtimeDeltaTicks() {
                    return pPartialTick;
                }
            });
        }

        LayeredDraw.Layer getHUDElement();
    }

    private record StandardHUDElement(GuiLayerManager.NamedLayer entry) implements HUDWidget {
        @Override
        public LayeredDraw.Layer getHUDElement() {
            return entry.layer();
        }
    }

    private static class HUDElementWrapper extends AbstractWidget implements HUDWidget {

        private final HUDElement element;
        private @Nullable Double holdX;
        private @Nullable Double holdY;

        public HUDElementWrapper(Component name, HUDElement element) {
            super(element.getX(Minecraft.getInstance().getWindow().getGuiScaledWidth()), element.getY(Minecraft.getInstance().getWindow().getGuiScaledHeight()), element.getWidth(), element.getHeight(), name);
            this.element = element;
        }

        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            HUDWidget.super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
            pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
        }

        @Override
        public void onClick(double pMouseX, double pMouseY, int pButton) {
            if (pButton != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
            this.holdX = pMouseX - this.getX();
            this.holdY = pMouseY - this.getY();
        }

        @Override
        public void onRelease(double pMouseX, double pMouseY) {
            this.holdX = null;
            this.holdY = null;
        }

        @Override
        protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
            if (this.holdX != null && this.holdY != null) {
                int newX = (int) (pMouseX - this.holdX);
                int newY = (int) (pMouseY - this.holdY);
                setElementPosition(newX, newY);
            }
        }

        @Override
        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            if (Screen.hasControlDown()) {
                switch (pKeyCode) {
                    case GLFW.GLFW_KEY_RIGHT -> this.element.setAnchorX(HUDElement.AnchorX.RIGHT);
                    case GLFW.GLFW_KEY_LEFT -> this.element.setAnchorX(HUDElement.AnchorX.LEFT);
                    case GLFW.GLFW_KEY_DOWN -> this.element.setAnchorY(HUDElement.AnchorY.BOTTOM);
                    case GLFW.GLFW_KEY_UP -> this.element.setAnchorY(HUDElement.AnchorY.TOP);
                    case GLFW.GLFW_KEY_HOME -> this.element.setAnchorX(HUDElement.AnchorX.CENTER);
                    case GLFW.GLFW_KEY_END -> this.element.setAnchorY(HUDElement.AnchorY.CENTER);
                }
                this.setPosition(
                        this.element.getX(Minecraft.getInstance().getWindow().getGuiScaledWidth()),
                        this.element.getY(Minecraft.getInstance().getWindow().getGuiScaledHeight())
                );
            } else {
                switch (pKeyCode) {
                    case GLFW.GLFW_KEY_RIGHT -> this.setElementPosition(this.getX() + 1, this.getY());
                    case GLFW.GLFW_KEY_LEFT -> this.setElementPosition(this.getX() - 1, this.getY());
                    case GLFW.GLFW_KEY_DOWN -> this.setElementPosition(this.getX(), this.getY() + 1);
                    case GLFW.GLFW_KEY_UP -> this.setElementPosition(this.getX(), this.getY() - 1);
                }
            }
            return true;
        }

        private void setElementPosition(int newX, int newY) {
            Minecraft minecraft = Minecraft.getInstance();
            int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
            int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            setPosition(newX, newY);
            this.element.setPosition(newX, newY, scaledWidth, scaledHeight);
        }

        @Override
        public void playDownSound(SoundManager pHandler) {}

        @Override
        public LayeredDraw.Layer getHUDElement() {
            return element;
        }
    }
}
