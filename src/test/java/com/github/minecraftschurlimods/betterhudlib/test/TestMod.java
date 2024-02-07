package com.github.minecraftschurlimods.betterhudlib.test;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("betterhudlibtest")
public class TestMod {
    public TestMod(IEventBus modBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientInit.init(modBus);
        }
    }
}
