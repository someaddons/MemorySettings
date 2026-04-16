package com.memorysettings;

import com.memorysettings.config.CommonConfiguration;
import net.minecraft.util.Util;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.net.URI;
import java.net.URISyntaxException;

public class EarlyWarning
{// TODO: Alternatively prevent all other resources(mods/resourcepacks etc) from loading to be able to load vanilla minecraft with the error message screen

    public static void showEarlyScreenFor(final String message, final URI link)
    {
        final int result = TinyFileDialogs.tinyfd_messageBox(
            "Memory Settings",
            message,
            "okcancel",
            "info",
            0);

        if (result == 1)
        {
            if (link != null)
            {
                try
                {
                    Util.getPlatform().openUri(new URI(CommonConfiguration.config.getCommonConfig().howtolink));
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
            }
            Runtime.getRuntime().exit(0);
        }
    }
}
