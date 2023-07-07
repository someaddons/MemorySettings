package com.memorysettings.mixin;

import com.memorysettings.MemoryErrorScreen;
import com.memorysettings.MemorysettingsMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientModLoader.class)
public class ClienModLoaderMixin
{
    @Inject(method = "completeModLoading", at = @At("RETURN"), cancellable = true, remap = false)
    private static void onReturn(final CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValue() && !MemorysettingsMod.memorycheckresult.getSiblings().isEmpty()
              && !MemorysettingsMod.config.getCommonConfig().disableWarnings && !MemorysettingsMod.didDisplay)
        {
            Minecraft.getInstance().setScreen(new MemoryErrorScreen(MemorysettingsMod.memorycheckresult));
            cir.setReturnValue(true);
        }
    }
}
