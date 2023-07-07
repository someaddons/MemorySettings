package com.memorysettings;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.MinecraftForge;

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
        MemorysettingsMod.didDisplay = true;
    }

    protected void init()
    {
        super.init();

        button_proceed = Button.builder(CommonComponents.GUI_PROCEED, (button) -> {
            MinecraftForge.EVENT_BUS.start();
            this.minecraft.setScreen((Screen) null);
        }).bounds(this.width / 2 - 100, 140, 200, 20).build();

        button_howto = Button.builder(Component.translatable("button.howto"), (button) -> {
            this.minecraft.keyboardHandler.setClipboard(MemorysettingsMod.config.getCommonConfig().howtolink);
            try
            {
                Util.getPlatform().openUri(new URI(MemorysettingsMod.config.getCommonConfig().howtolink));
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }).bounds(this.width / 2 - 100, 120, 200, 20).build();

        button_noremind = Button.builder(Component.translatable("button.stopremind"), (button) -> {
            MinecraftForge.EVENT_BUS.start();
            this.minecraft.setScreen((Screen) null);
            MemorysettingsMod.config.getCommonConfig().disableWarnings = true;
            MemorysettingsMod.config.save();
        }).bounds(this.width / 2 - 100, 160, 200, 20).build();


        this.addRenderableWidget(button_howto);
        this.addRenderableWidget(button_proceed);
        this.addRenderableWidget(button_noremind);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float z)
    {
        {
            graphics.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);

            int yOffset = 20;
            for (final FormattedCharSequence component : font.split(message, 220))
            {
                graphics.drawCenteredString(this.font, component, this.width / 2, yOffset, 16777215);
                yOffset += 10;
            }

            button_proceed.setY(20 + yOffset);
            button_howto.setY(40 + yOffset);
            button_noremind.setY(60 + yOffset);

            super.render(graphics, x, y, z);
        }
    }

    public boolean shouldCloseOnEsc()
    {
        return true;
    }
}
