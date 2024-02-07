package com.github.minecraftschurlimods.betterhudlib;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.UnknownNullability;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class HUDManager {
    private static final Lock KEYBIND_LOCK = new ReentrantLock();
    private static final Lock INIT_LOCK = new ReentrantLock();
    private static boolean KEYBIND_ENABLED = false;
    private static boolean INITIALIZED = false;
    private static @UnknownNullability KeyMapping KEY;

    public static void initialize() {
        try {
            INIT_LOCK.lock();
            if (INITIALIZED) return;
            INITIALIZED = true;
        } finally {
            INIT_LOCK.unlock();
        }

        NeoForge.EVENT_BUS.addListener(HUDManager::onBeforeHUD);
    }

    public static void enableKeybind(IEventBus modBus) {
        if (!INITIALIZED) {
            initialize();
        }
        try {
            KEYBIND_LOCK.lock();
            if (KEYBIND_ENABLED) return;
            KEYBIND_ENABLED = true;
        } finally {
            KEYBIND_LOCK.unlock();
        }

        modBus.addListener(HUDManager::onClientInit);
        NeoForge.EVENT_BUS.addListener(HUDManager::onInput);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new HUDManagerScreen());
    }

    private static void onInput(InputEvent.Key evt) {
        if (evt.getAction() != InputConstants.PRESS) return;
        if (!KEY.isActiveAndMatches(InputConstants.getKey(evt.getKey(), evt.getScanCode()))) return;

        open();
    }

    private static void onBeforeHUD(RenderGuiEvent.Pre evt) {
        if (!(Minecraft.getInstance().screen instanceof HUDManagerScreen)) return;

        evt.setCanceled(true);
    }

    private static void onClientInit(RegisterKeyMappingsEvent evt) {
        if (KEY != null) return;

        KEY = new KeyMapping(
                "hud_manager.open",
                KeyConflictContext.IN_GAME,
                KeyModifier.ALT,
                InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_M),
                "hud_manager"
        );
        evt.register(KEY);
    }
}
