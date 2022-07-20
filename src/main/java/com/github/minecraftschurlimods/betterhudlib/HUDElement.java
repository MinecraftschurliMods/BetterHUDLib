package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class HUDElement extends GuiComponent implements IGuiOverlay {
    private Supplier<AnchorX> defaultAnchorX;
    private Supplier<AnchorY> defaultAnchorY;
    private IntSupplier defaultX;
    private IntSupplier defaultY;
    private IntSupplier defaultWidth;
    private IntSupplier defaultHeight;
    private AnchorX anchorX;
    private AnchorY anchorY;
    private int x;
    private int y;
    private int width;
    private int height;

    protected HUDElement(AnchorX anchorX, AnchorY anchorY, int x, int y, int width, int height) {
        this(() -> anchorX, () -> anchorY, () -> x, () -> y, () -> width, () -> height);
    }

    protected HUDElement(Supplier<AnchorX> anchorX, Supplier<AnchorY> anchorY, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) {
        this.defaultAnchorX = anchorX;
        this.defaultAnchorY = anchorY;
        this.defaultX = x;
        this.defaultY = y;
        this.defaultWidth = width;
        this.defaultHeight = height;
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (anchorX == null) {
            anchorX = defaultAnchorX.get();
            anchorY = defaultAnchorY.get();
            x = defaultX.getAsInt();
            y = defaultY.getAsInt();
            width = defaultWidth.getAsInt();
            height = defaultHeight.getAsInt();
        }
        poseStack.pushPose();
        int x = this.getX(screenWidth);
        int y = this.getY(screenHeight);
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

    protected final int getX(int screenWidth) {
        return switch (anchorX) {
            case LEFT -> x;
            case CENTER -> x + screenWidth / 2 - width / 2;
            case RIGHT -> screenWidth - x - width;
        };
    }

    protected final int getY(int screenHeight) {
        return switch (anchorY) {
            case TOP -> y;
            case CENTER -> y + screenHeight / 2 - height / 2;
            case BOTTOM -> screenHeight - y - height;
        };
    }

    void setPosition(int x, int y, int screenWidth, int screenHeight) {
        this.x = switch (anchorX) {
            case LEFT -> x;
            case CENTER -> x - screenWidth / 2 + width / 2;
            case RIGHT -> screenWidth - x - width;
        };
        this.y = switch (anchorY) {
            case TOP -> y;
            case CENTER -> y - screenHeight / 2 + height / 2;
            case BOTTOM -> screenHeight - y - height;
        };
        onPositionUpdate(this.anchorX, this.anchorY, this.x, this.y);
    }

    void setAnchorX(AnchorX anchorX) {
        this.anchorX = anchorX;
        onPositionUpdate(anchorX, this.anchorY, this.x, this.y);
    }

    void setAnchorY(AnchorY anchorY) {
        this.anchorY = anchorY;
        onPositionUpdate(this.anchorX, anchorY, this.x, this.y);
    }

    void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        onSizeUpdate(width, height);
    }

    protected final int getWidth() {
        return this.width;
    }

    protected final int getHeight() {
        return this.height;
    }

    protected void onPositionUpdate(AnchorX anchorX, AnchorY anchorY, int x, int y) {}

    protected void onSizeUpdate(int width, int height) {}

    protected void save() {}

    public enum AnchorX {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum AnchorY {
        TOP,
        CENTER,
        BOTTOM
    }
}
