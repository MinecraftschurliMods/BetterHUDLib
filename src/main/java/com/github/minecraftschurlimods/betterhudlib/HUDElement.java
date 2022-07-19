package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class HUDElement extends GuiComponent implements IGuiOverlay {
    private final Supplier<Position> defaultPosition;
    private final Supplier<Size> defaultSize;
    private Position position;
    private Size size;

    protected HUDElement(AnchorX anchorX, AnchorY anchorY, int x, int y, int width, int height) {
        this(() -> anchorX, () -> anchorY, () -> x, () -> y, () -> width, () -> height);
    }

    protected HUDElement(Supplier<AnchorX> anchorX, Supplier<AnchorY> anchorY, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) {
        this.defaultPosition = () -> new Position(anchorX.get(), anchorY.get(), x.getAsInt(), y.getAsInt());
        this.defaultSize = () -> new Size(width.getAsInt(), height.getAsInt());
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (position == null) {
            position = defaultPosition.get();
        }
        if (size == null) {
            size = defaultSize.get();
        }
        poseStack.pushPose();
        int x = this.position.getX(screenWidth);
        int y = this.position.getY(screenHeight);
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

    public int getRawX() {
        return this.position.x();
    }

    public int getRawY() {
        return this.position.y();
    }

    protected final int getX(int screenWidth) {
        return this.position.getX(screenWidth);
    }

    protected final int getY(int screenHeight) {
        return this.position.getY(screenHeight);
    }

    void setPosition(int x, int y) {
        this.position = this.position.with(x, y);
        onPositionUpdate(this.position.anchorX(), this.position.anchorY(), x, y);
    }

    void setAnchorX(AnchorX anchorX) {
        this.position = this.position.with(anchorX);
        onPositionUpdate(anchorX, this.position.anchorY(), this.position.x(), this.position.y());
    }

    void setAnchorY(AnchorY anchorY) {
        this.position = this.position.with(anchorY);
        onPositionUpdate(this.position.anchorX(), anchorY, this.position.x(), this.position.y());
    }

    void setSize(int width, int height) {
        this.size = new Size(width, height);
        onSizeUpdate(width, height);
    }

    protected void onPositionUpdate(AnchorX anchorX, AnchorY anchorY, int x, int y) {}

    protected void onSizeUpdate(int width, int height) {}

    protected void save() {}

    protected final int getWidth() {
        return this.size.width();
    }

    protected final int getHeight() {
        return this.size.height();
    }

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

    record Position(AnchorX anchorX, AnchorY anchorY, int x, int y) {
        public int getX(int screenWidth) {
            return switch (anchorX) {
                case LEFT -> x;
                case CENTER -> screenWidth / 2 + x;
                case RIGHT -> screenWidth - x;
            };
        }
        public int getY(int screenHeight) {
            return switch (anchorY) {
                case TOP -> y;
                case CENTER -> screenHeight / 2 + y;
                case BOTTOM -> screenHeight - y;
            };
        }
        public Position with(int x, int y) {
            return new Position(anchorX, anchorY, x, y);
        }
        public Position with(AnchorX anchorX) {
            return new Position(anchorX, anchorY, x, y);
        }
        public Position with(AnchorY anchorY) {
            return new Position(anchorX, anchorY, x, y);
        }
    }

    record Size(int width, int height) {}
}
