package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public abstract class HUDElement extends GuiComponent implements IIngameOverlay {
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
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
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

    public abstract void draw(ForgeIngameGui gui, PoseStack poseStack, float partialTick);

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
