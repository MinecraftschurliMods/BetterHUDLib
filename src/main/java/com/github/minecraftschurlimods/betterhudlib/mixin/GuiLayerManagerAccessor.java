package com.github.minecraftschurlimods.betterhudlib.mixin;

import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GuiLayerManager.class)
public interface GuiLayerManagerAccessor {
    @Accessor
    List<GuiLayerManager.NamedLayer> getLayers();
}
