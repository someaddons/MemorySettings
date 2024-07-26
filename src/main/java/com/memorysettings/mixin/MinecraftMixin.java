package com.memorysettings.mixin;

import com.memorysettings.MemoryErrorScreen;
import com.memorysettings.MemorysettingsMod;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Inject(method = "onGameLoadFinished", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V"), cancellable = true)
    private void onInit(final Minecraft.GameLoadCookie gameLoadCookie, final CallbackInfo ci)
    {
        if (!(Minecraft.getInstance().screen instanceof MemoryErrorScreen) && !MemorysettingsMod.memorycheckresult.getSiblings().isEmpty()
              && !MemorysettingsMod.config.getCommonConfig().disableWarnings)
        {
            Minecraft.getInstance().setScreen(new MemoryErrorScreen(MemorysettingsMod.memorycheckresult));
            ci.cancel();
        }
    }
}
