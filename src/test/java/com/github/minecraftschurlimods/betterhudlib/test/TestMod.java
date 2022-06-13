package com.github.minecraftschurlimods.betterhudlib.test;

import com.github.minecraftschurlimods.betterhudlib.HUDElement;
import com.github.minecraftschurlimods.betterhudlib.HUDManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
            FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLClientSetupEvent evt) -> {
                OverlayRegistry.registerOverlayTop("TestHUD", new HUDElement(0, 0, 100, 100) {
                    @Override
                    public void draw(ForgeIngameGui gui, PoseStack poseStack, float partialTick) {
                        fill(poseStack, 0, 0, getHeight(), getWidth(), 0xFF00FF00);
                    }
                });
            });
        }
    }
}
