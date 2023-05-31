package com.memorysettings.mixin;

import com.memorysettings.MemoryErrorScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin extends Overlay
{
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), cancellable = true)
    private void onStart(final PoseStack p_96178_, final int p_96179_, final int p_96180_, final float p_96181_, final CallbackInfo ci)
    {
        if (Minecraft.getInstance().screen instanceof MemoryErrorScreen)
        {
            ci.cancel();
        }
    }
}
