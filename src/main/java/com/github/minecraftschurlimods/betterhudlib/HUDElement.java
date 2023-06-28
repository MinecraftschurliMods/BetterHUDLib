package com.github.minecraftschurlimods.betterhudlib;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class HUDElement implements IGuiOverlay {
    private final Supplier<AnchorX> defaultAnchorX;
    private final Supplier<AnchorY> defaultAnchorY;
    private final IntSupplier defaultX;
    private final IntSupplier defaultY;
    private final IntSupplier defaultWidth;
    private final IntSupplier defaultHeight;
    private boolean defaultsApplied = false;
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
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        applyDefaults();
        graphics.pose().pushPose();
        graphics.pose().translate(getNormalizedX(screenWidth), getNormalizedY(screenHeight), 0);
        draw(gui, graphics, partialTick);
        graphics.pose().popPose();
    }

    private void applyDefaults() {
        if (defaultsApplied) return;
        defaultsApplied = true;
        anchorX = defaultAnchorX.get();
        x = defaultX.getAsInt();
        width = defaultWidth.getAsInt();
        anchorY = defaultAnchorY.get();
        y = defaultY.getAsInt();
        height = defaultHeight.getAsInt();
    }

    private int getNormalizedX(int screenWidth) {
        int x = this.getX(screenWidth);
        int elementWidth = this.getWidth();
        if (x < 0) {
            x = 0;
        }
        if (x + elementWidth > screenWidth) {
            x = screenWidth - elementWidth;
        }
        return x;
    }

    private int getNormalizedY(int screenHeight) {
        int y = this.getY(screenHeight);
        int elementHeight = this.getHeight();
        if (y < 0) {
            y = 0;
        }
        if (y + elementHeight > screenHeight) {
            y = screenHeight - elementHeight;
        }
        return y;
    }

    public abstract void draw(ForgeGui gui, GuiGraphics graphics, float partialTick);

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
