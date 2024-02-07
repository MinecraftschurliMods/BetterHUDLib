package com.github.minecraftschurlimods.betterhudlib.test;

import com.github.minecraftschurlimods.betterhudlib.HUDElement;
import com.github.minecraftschurlimods.betterhudlib.HUDManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

public class ClientInit {
    public static void init(IEventBus modBus) {
        HUDManager.initialize();
        HUDManager.enableKeybind(modBus);
        modBus.addListener((RegisterGuiOverlaysEvent evt) -> {
            evt.registerAboveAll(new ResourceLocation("betterhudlibtest", "test_hud"), new HUDElement(HUDElement.AnchorX.CENTER, HUDElement.AnchorY.CENTER, 0, 0, 100, 100) {
                @Override
                public void draw(ExtendedGui gui, GuiGraphics graphics, float partialTick) {
                    graphics.fill(0, 0, getHeight(), getWidth(), 0xFF00FF00);
                }
            });
            evt.registerAboveAll(new ResourceLocation("betterhudlibtest", "test_hud2"), new HUDElement(HUDElement.AnchorX.CENTER, HUDElement.AnchorY.BOTTOM, 100, 0, 100, 100) {
                @Override
                public void draw(ExtendedGui gui, GuiGraphics graphics, float partialTick) {
                    graphics.fill(0, 0, getHeight(), getWidth(), 0xFFFF0000);
                }
            });
        });
    }
}
