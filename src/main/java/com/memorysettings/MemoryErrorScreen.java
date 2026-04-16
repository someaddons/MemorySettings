package com.memorysettings;

import com.memorysettings.config.CommonConfiguration;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

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

        button_proceed = Button.builder(CommonComponents.GUI_PROCEED, (button) -> {
            this.minecraft.setScreen((Screen) null);
        }).bounds(this.width / 2 - 100, 140, 200, 20).build();

        button_howto = Button.builder(Component.translatable("button.howto"), (button) -> {
            this.minecraft.keyboardHandler.setClipboard(CommonConfiguration.config.getCommonConfig().howtolink);
            try
            {
                Util.getPlatform().openUri(new URI(CommonConfiguration.config.getCommonConfig().howtolink));
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }).bounds(this.width / 2 - 100, 120, 200, 20).build();

        button_noremind = Button.builder(Component.translatable("button.stopremind"), (button) -> {
            this.minecraft.setScreen((Screen) null);
            CommonConfiguration.config.getCommonConfig().disableWarnings = true;
            CommonConfiguration.config.save();
        }).bounds(this.width / 2 - 100, 160, 200, 20).build();

        this.addRenderableWidget(button_howto);
        this.addRenderableWidget(button_proceed);
        this.addRenderableWidget(button_noremind);
        int yOffset = 20;
        for (final FormattedCharSequence component : font.split(message, 220))
        {
            yOffset += 10;
        }

        button_proceed.setY(20 + yOffset);
        button_howto.setY(40 + yOffset);
        button_noremind.setY(60 + yOffset);

        final MultiLineTextWidget textWidget = new MultiLineTextWidget(message, this.font).setMaxWidth(220).setCentered(true);
        textWidget.setX(this.width / 2 - 100);
        textWidget.setY(20);
        this.addRenderableWidget(textWidget);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        {
            graphics.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
            super.extractRenderState(graphics, mouseX, mouseY, a);
        }
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics)
    {
        // NO blur!
    }

    public boolean shouldCloseOnEsc()
    {
        return true;
    }
}
