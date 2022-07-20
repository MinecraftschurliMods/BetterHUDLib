package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import org.lwjgl.glfw.GLFW;

public final class HUDManagerScreen extends Screen {

    HUDManagerScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        for (NamedGuiOverlay entry : GuiOverlayManager.getOverlays()) {
            if (entry.overlay() instanceof HUDElement element) {
                addRenderableWidget(new HUDElementWrapper(Component.translatable(Util.makeDescriptionId("hud_element", entry.id())), element));
            } else {
                addRenderableOnly(new StandardHUDElement(entry));
            }
        }
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

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if (getFocused() != null) {
            getFocused().mouseMoved(pMouseX, pMouseY);
        }
    }

    private interface HUDWidget extends Widget {
        @Override
        default void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            getHUDElement().render(
                    ((ForgeGui) minecraft.gui),
                    pPoseStack,
                    pPartialTick,
                    minecraft.getWindow().getGuiScaledWidth(),
                    minecraft.getWindow().getGuiScaledHeight()
            );
        }

        IGuiOverlay getHUDElement();
    }

    private record StandardHUDElement(NamedGuiOverlay entry) implements HUDWidget {
        @Override
        public IGuiOverlay getHUDElement() {
            return entry.overlay();
        }
    }

    private static class HUDElementWrapper extends AbstractWidget implements HUDWidget {

        private final HUDElement element;
        private Double holdX;
        private Double holdY;

        public HUDElementWrapper(Component name, HUDElement element) {
            super(element.getX(Minecraft.getInstance().getWindow().getGuiScaledWidth()), element.getY(Minecraft.getInstance().getWindow().getGuiScaledHeight()), element.getWidth(), element.getHeight(), name);
            this.element = element;
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            HUDWidget.super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            this.holdX = pMouseX - this.x;
            this.holdY = pMouseY - this.y;
        }

        @Override
        public void onRelease(double pMouseX, double pMouseY) {
            this.holdX = null;
            this.holdY = null;
        }

        @Override
        public void mouseMoved(double pMouseX, double pMouseY) {
            if (this.holdX != null && this.holdY != null) {
                int newX = (int) (pMouseX - this.holdX);
                int newY = (int) (pMouseY - this.holdY);
                setPosition(newX, newY);
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
                this.x = this.element.getX(Minecraft.getInstance().getWindow().getGuiScaledWidth());
                this.y = this.element.getY(Minecraft.getInstance().getWindow().getGuiScaledHeight());
            } else {
                switch (pKeyCode) {
                    case GLFW.GLFW_KEY_RIGHT -> this.setPosition(this.x + 1, this.y);
                    case GLFW.GLFW_KEY_LEFT -> this.setPosition(this.x - 1, this.y);
                    case GLFW.GLFW_KEY_DOWN -> this.setPosition(this.x, this.y + 1);
                    case GLFW.GLFW_KEY_UP -> this.setPosition(this.x, this.y - 1);
                }
            }
            return true;
        }

        private void setPosition(int newX, int newY) {
            Minecraft minecraft = Minecraft.getInstance();
            int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
            int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            this.x = newX;
            this.y = newY;
            this.element.setPosition(newX, newY, scaledWidth, scaledHeight);
        }

        @Override
        public void playDownSound(SoundManager pHandler) {}

        @Override
        public IGuiOverlay getHUDElement() {
            return element;
        }
    }
}
