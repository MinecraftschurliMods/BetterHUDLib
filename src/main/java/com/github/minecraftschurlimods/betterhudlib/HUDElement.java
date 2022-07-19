package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public abstract class HUDElement extends GuiComponent implements IGuiOverlay {
    private int x;
    private int y;
    private int width;
    private int height;

    protected HUDElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        poseStack.pushPose();
        int x = this.x;
        int y = this.y;
        int elementWidth = this.getWidth();
        int elementHeight = this.getHeight();
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x + elementWidth > screenWidth) {
            x = screenWidth - elementWidth;
        }
        if (y + elementHeight > screenHeight) {
            y = screenHeight - elementHeight;
        }
        poseStack.translate(x, y, 0);
        draw(gui, poseStack, partialTick);
        poseStack.popPose();
    }

    public abstract void draw(ForgeGui gui, PoseStack poseStack, float partialTick);

    protected final int getX() {
        return x;
    }

    protected final int getY() {
        return y;
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        onPositionUpdate(x, y);
    }

    protected void onPositionUpdate(int x, int y) {
    }

    void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        onSizeUpdate(width, height);
    }

    protected void onSizeUpdate(int width, int height) {
    }

    protected final int getWidth() {
        return this.width;
    }

    protected final int getHeight() {
        return this.height;
    }
}
