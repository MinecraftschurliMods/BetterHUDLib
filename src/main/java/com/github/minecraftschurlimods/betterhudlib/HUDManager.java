package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class HUDManager {
    private static final Lock KEYBIND_LOCK = new ReentrantLock();
    private static final Lock INIT_LOCK = new ReentrantLock();
    private static boolean KEYBIND_ENABLED = false;
    private static boolean INITIALIZED = false;
    private static KeyMapping KEY;

    public static void initialize() {
        INIT_LOCK.lock();
        if (INITIALIZED) return;
        INITIALIZED = true;
        INIT_LOCK.unlock();

        MinecraftForge.EVENT_BUS.addListener(HUDManager::onBeforeHUD);
    }

    public static void enableKeybind() {
        if (!INITIALIZED) {
            initialize();
        }
        KEYBIND_LOCK.lock();
        if (KEYBIND_ENABLED) return;
        KEYBIND_ENABLED = true;
        KEYBIND_LOCK.unlock();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(HUDManager::onClientInit);
        MinecraftForge.EVENT_BUS.addListener(HUDManager::onInput);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new HUDManagerScreen());
    }

    private static void onInput(InputEvent.KeyInputEvent evt) {
        if (evt.getAction() != InputConstants.PRESS) return;
        if (!KEY.isActiveAndMatches(InputConstants.getKey(evt.getKey(), evt.getScanCode()))) return;

        open();
    }

    private static void onBeforeHUD(RenderGameOverlayEvent.Pre evt) {
        if (evt.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!(Minecraft.getInstance().screen instanceof HUDManagerScreen)) return;

        evt.setCanceled(true);
    }

    private static void onClientInit(FMLClientSetupEvent evt) {
        if (KEY != null) return;

        //noinspection NoTranslation
        KEY = new KeyMapping(
                "hud_manager.open",
                KeyConflictContext.IN_GAME,
                KeyModifier.ALT,
                InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_M),
                "hud_manager"
        );
    }
}
