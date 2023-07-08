package com.memorysettings;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.net.URI;
import java.net.URISyntaxException;

import static net.minecraft.client.gui.DialogTexts.GUI_PROCEED;

public class MemoryErrorScreen extends Screen
{
    private final ITextComponent message;
    private       Button         button_proceed  = null;
    private       Button         button_howto    = null;
    private       Button         button_noremind = null;

    public MemoryErrorScreen(final ITextComponent message)
    {
        super(new StringTextComponent(""));
        this.message = message;
        MemorysettingsMod.didDisplay = true;
    }

    protected void init()
    {
        super.init();

        button_proceed = new Button(this.width / 2 - 100, 140, 200, 20, GUI_PROCEED, (button) -> {
            MinecraftForge.EVENT_BUS.start();
            this.minecraft.setScreen((Screen) null);
        });

        button_howto = new Button(this.width / 2 - 100, 120, 200, 20, new TranslationTextComponent("button.howto"), (button) -> {
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

        button_noremind = new Button(this.width / 2 - 100, 160, 200, 20, new TranslationTextComponent("button.stopremind"), (button) -> {
            MinecraftForge.EVENT_BUS.start();
            this.minecraft.setScreen((Screen) null);
            MemorysettingsMod.config.getCommonConfig().disableWarnings = true;
            MemorysettingsMod.config.save();
        });


        this.addButton(button_howto);
        this.addButton(button_proceed);
        this.addButton(button_noremind);
    }

    public void render(MatrixStack poseStack, int x, int y, float z)
    {
        fillGradient(poseStack, 0, 0, this.width, this.height, -12574688, -11530224);

        font.drawWordWrap(message, this.width / 2 - 100, 20, 220, 16777215);

        int yOffset = 20;
        for (final IReorderingProcessor component : font.split(message, 220))
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
