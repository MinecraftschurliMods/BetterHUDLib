package com.github.minecraftschurlimods.betterhudlib.test;

import com.github.minecraftschurlimods.betterhudlib.HUDElement;
import com.github.minecraftschurlimods.betterhudlib.HUDManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("betterhudlibtest")
public class TestMod {
    public TestMod() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, ClientInit::new);
    }

    private static class ClientInit implements DistExecutor.SafeRunnable {
        @Override
        public void run() {
            HUDManager.initialize();
            HUDManager.enableKeybind();
            FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterGuiOverlaysEvent evt) -> {
                evt.registerAboveAll("test_hud", new HUDElement(HUDElement.AnchorX.CENTER, HUDElement.AnchorY.CENTER, 0, 0, 100, 100) {
                    @Override
                    public void draw(ForgeGui gui, GuiGraphics graphics, float partialTick) {
                        graphics.fill(0, 0, getHeight(), getWidth(), 0xFF00FF00);
                    }
                });
                evt.registerAboveAll("test_hud2", new HUDElement(HUDElement.AnchorX.CENTER, HUDElement.AnchorY.BOTTOM, 100, 0, 100, 100) {
                    @Override
                    public void draw(ForgeGui gui, GuiGraphics graphics, float partialTick) {
                        graphics.fill(0, 0, getHeight(), getWidth(), 0xFFFF0000);
                    }
                });
            });
        }
    }
}
