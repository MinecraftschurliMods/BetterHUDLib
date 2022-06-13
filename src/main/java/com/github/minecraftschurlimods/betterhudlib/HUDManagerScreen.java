package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.function.BooleanSupplier;

public final class HUDManagerScreen extends Screen {

    HUDManagerScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        for (OverlayRegistry.OverlayEntry entry : OverlayRegistry.orderedEntries()) {
            if (entry.getOverlay() instanceof HUDElement element) {
                addRenderableWidget(new HUDElementWrapper(Component.literal(entry.getDisplayName()), element, entry::isEnabled));
            } else {
                addRenderableOnly(new StandardHUDElement(entry));
            }
        }
    }

    private interface HUDWidget extends Widget {
        @Override
        default void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (isEnabled()) {
                Minecraft minecraft = Minecraft.getInstance();
                getHUDElement().render(
                        ((ForgeIngameGui) minecraft.gui),
                        pPoseStack,
                        pPartialTick,
                        minecraft.getWindow().getGuiScaledWidth(),
                        minecraft.getWindow().getGuiScaledHeight()
                );
            }
        }

        IIngameOverlay getHUDElement();

        boolean isEnabled();
    }

    private record StandardHUDElement(OverlayRegistry.OverlayEntry entry) implements HUDWidget {
        @Override
        public IIngameOverlay getHUDElement() {
            return entry.getOverlay();
        }

        @Override
        public boolean isEnabled() {
            return entry.isEnabled();
        }
    }

    private static class HUDElementWrapper extends AbstractWidget implements HUDWidget {

        private final HUDElement element;
        private final BooleanSupplier enabled;

        public HUDElementWrapper(Component name, HUDElement element, BooleanSupplier enabled) {
            super(element.getX(), element.getY(), element.getWidth(), element.getHeight(), name);
            this.element = element;
            this.enabled = enabled;
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
        public IIngameOverlay getHUDElement() {
            return element;
        }

        @Override
        public boolean isEnabled() {
            return enabled.getAsBoolean();
        }
    }
}
