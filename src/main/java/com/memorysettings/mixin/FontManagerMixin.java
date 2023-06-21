package com.memorysettings.mixin;

import com.memorysettings.MemoryErrorScreen;
import com.memorysettings.MemorysettingsMod;
import com.mojang.blaze3d.font.GlyphProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(targets = "net/minecraft/client/gui/font/FontManager")
public class FontManagerMixin
{
    @Inject(method = "apply", at = @At("RETURN"))
    private void onInit(
      final FontManager.Preparation preparation, final ProfilerFiller profilerFiller, final CallbackInfo ci)
    {
        if (!(Minecraft.getInstance().screen instanceof MemoryErrorScreen) && !MemorysettingsMod.memorycheckresult.getSiblings().isEmpty()
              && !MemorysettingsMod.config.getCommonConfig().disableWarnings)
        {
            Minecraft.getInstance().setScreen(new MemoryErrorScreen(MemorysettingsMod.memorycheckresult));
        }
    }
}
