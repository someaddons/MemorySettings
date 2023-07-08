package com.memorysettings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.net.URI;
import java.net.URISyntaxException;

import static net.minecraft.network.chat.CommonComponents.GUI_PROCEED;

public class MemoryErrorScreen extends Screen
{
    private final TextComponent message;
    private       Button        button_proceed  = null;
    private       Button        button_howto    = null;
    private       Button        button_noremind = null;

    public MemoryErrorScreen(final TextComponent message)
    {
        super(new TextComponent(""));
        this.message = message;
    }

    protected void init()
    {
        super.init();

        button_proceed = new Button(this.width / 2 - 100, 140, 200, 20, GUI_PROCEED, (button) -> {
            this.minecraft.setScreen((Screen) null);
        });

        button_howto = new Button(this.width / 2 - 100, 120, 200, 20, new TranslatableComponent("button.howto"), (button) -> {
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

        button_noremind = new Button(this.width / 2 - 100, 160, 200, 20, new TranslatableComponent("button.stopremind"), (button) -> {
            this.minecraft.setScreen((Screen) null);
            MemorysettingsMod.config.getCommonConfig().disableWarnings = true;
            MemorysettingsMod.config.save();
        });


        this.addButton(button_howto);
        this.addButton(button_proceed);
        this.addButton(button_noremind);
    }

    public void render(PoseStack poseStack, int x, int y, float z)
    {
        fillGradient(poseStack, 0, 0, this.width, this.height, -12574688, -11530224);

        font.drawWordWrap(message, this.width / 2 - 100, 20, 220, 16777215);

        int yOffset = 20;
        for (final FormattedCharSequence component : font.split(message, 220))
        {
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
