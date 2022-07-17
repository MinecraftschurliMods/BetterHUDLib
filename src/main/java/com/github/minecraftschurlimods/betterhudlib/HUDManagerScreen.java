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
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;

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
            super(element.getX(), element.getY(), element.getWidth(), element.getHeight(), name);
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
            Minecraft minecraft = Minecraft.getInstance();
            int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
            int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
            this.x = Mth.clamp(this.x + Mth.ceil(pDragX), 0, scaledWidth - this.width);
            this.y = Mth.clamp(this.y + Mth.ceil(pDragY), 0, scaledHeight - this.height);
            this.element.setPosition(this.x, this.y);
        }

        @Override
        public void playDownSound(SoundManager pHandler) {}

        @Override
        public IGuiOverlay getHUDElement() {
            return element;
        }
    }
}
