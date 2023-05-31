package com.memorysettings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.net.URI;
import java.net.URISyntaxException;

public class MemoryErrorScreen extends Screen
{
    private final Component message;
    private       Button    button_proceed  = null;
    private       Button    button_howto    = null;
    private       Button    button_noremind = null;

    public MemoryErrorScreen(final Component message)
    {
        super(Component.empty());
        this.message = message;
    }

    protected void init()
    {
        super.init();

        button_proceed = new Button(this.width / 2 - 100, 140, 200, 20, CommonComponents.GUI_PROCEED, (button) -> {
            this.minecraft.setScreen((Screen) null);
        });

        button_howto = new Button(this.width / 2 - 100, 120, 200, 20, Component.translatable("button.howto"), (button) -> {
            this.minecraft.keyboardHandler.setClipboard(MemorysettingsMod.config.getCommonConfig().howtolink);
            try
            {
                Util.getPlatform().openUri(new URI(MemorysettingsMod.config.getCommonConfig().howtolink));
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        });

        button_noremind = new Button(this.width / 2 - 100, 160, 200, 20, Component.translatable("button.stopremind"), (button) -> {
            this.minecraft.setScreen((Screen) null);
            MemorysettingsMod.config.getCommonConfig().disableWarnings = true;
            MemorysettingsMod.config.save();
        });


        this.addRenderableWidget(button_howto);
        this.addRenderableWidget(button_proceed);
        this.addRenderableWidget(button_noremind);
    }

    public void render(PoseStack poseStack, int x, int y, float z)
    {
        fillGradient(poseStack, 0, 0, this.width, this.height, -12574688, -11530224);

        int yOffset = 20;
        for (final FormattedCharSequence component : font.split(message, 220))
        {
            drawCenteredString(poseStack, this.font, component, this.width / 2, yOffset, 16777215);
            yOffset += 10;
        }

        button_proceed.y = 20 + yOffset;
        button_howto.y = 40 + yOffset;
        button_noremind.y = 60 + yOffset;

        super.render(poseStack, x, y, z);
    }

    public boolean shouldCloseOnEsc()
    {
        return true;
    }
}
