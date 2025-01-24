package com.memorysettings.mixin;

import com.memorysettings.MemoryErrorScreen;
import com.memorysettings.MemorysettingsMod;
import com.memorysettings.config.CommonConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/gui/font/FontManager")
public class FontManagerMixin
{
    @Inject(method = "apply", at = @At("RETURN"))
    private void onInit(
      final FontManager.Preparation preparation, final ProfilerFiller profilerFiller, final CallbackInfo ci)
    {
        if (!(Minecraft.getInstance().screen instanceof MemoryErrorScreen) && !MemorysettingsMod.memorycheckresult.getSiblings().isEmpty()
            && !CommonConfiguration.config.getCommonConfig().disableWarnings)
        {
            Minecraft.getInstance().setScreen(new MemoryErrorScreen(MemorysettingsMod.memorycheckresult));
        }
    }
}
