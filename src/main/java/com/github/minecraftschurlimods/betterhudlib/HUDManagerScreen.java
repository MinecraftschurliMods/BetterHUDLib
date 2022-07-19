package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
        protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
            int newX = this.element.getRawX() + Mth.ceil(pDragX);
            int newY = this.element.getRawY() + Mth.ceil(pDragY);
            setPosition(newX, newY);
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
            } else {
                switch (pKeyCode) {
                    case GLFW.GLFW_KEY_RIGHT -> this.setPosition(this.element.getRawX() + 1, this.element.getRawY());
                    case GLFW.GLFW_KEY_LEFT -> this.setPosition(this.element.getRawX() - 1, this.element.getRawY());
                    case GLFW.GLFW_KEY_DOWN -> this.setPosition(this.element.getRawX(), this.element.getRawY() + 1);
                    case GLFW.GLFW_KEY_UP -> this.setPosition(this.element.getRawX(), this.element.getRawY() - 1);
                    case GLFW.GLFW_KEY_HOME -> this.setPosition(0, this.element.getRawY());
                    case GLFW.GLFW_KEY_END -> this.setPosition(this.element.getRawX(), 0);
                }
            }
            return true;
        }

        private void setPosition(int newX, int newY) {
            Minecraft minecraft = Minecraft.getInstance();
            int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
            int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            this.element.setPosition(newX, newY);
            this.x = this.element.getX(scaledWidth);
            this.y = this.element.getY(scaledHeight);
        }

        @Override
        public void playDownSound(SoundManager pHandler) {}

        @Override
        public IGuiOverlay getHUDElement() {
            return element;
        }
    }
}
