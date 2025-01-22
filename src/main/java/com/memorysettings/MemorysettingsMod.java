package com.memorysettings;

import com.cupboard.config.CupboardConfig;
import com.memorysettings.config.CommonConfiguration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.memorysettings.Memory.*;
import static net.fabricmc.api.EnvType.SERVER;

// The value here should match an entry in the META-INF/mods.toml file
public class MemorysettingsMod implements ModInitializer
{
    public static final  String                              MODID                  = "memorysettings";
    public static final  Logger                              LOGGER                 = LogManager.getLogger();
    private static final String                              DISABLE_WARNING_BUTTON = "Stop showing";
    public static        CupboardConfig<CommonConfiguration> config                 = new CupboardConfig<>(MODID, new CommonConfiguration());
    public static        Random                              rand                   = new Random();
    public static        MutableComponent                    memorycheckresult      = Component.empty();

    public MemorysettingsMod()
    {
        if (!config.getCommonConfig().disableWarnings)
        {
            doWarning();
        }
    }

    @Override
    public void onInitialize()
    {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
    }

    public static void doWarning()
    {
        final int configMax = (FabricLoader.getInstance().getEnvironmentType() != SERVER) ? config.getCommonConfig().maximumClient : config.getCommonConfig().maximumServer;
        final int configMin = (FabricLoader.getInstance().getEnvironmentType() != SERVER) ? config.getCommonConfig().minimumClient : config.getCommonConfig().minimumServer;
        final int recommendMemory = Math.min(getRecommendedMemoryForSystemMemory(systemMemory), configMax);

        String message = "";
        if (heapSetting > configMax + 250)
        {
            message += "You have more memory allocated(" + heapSetting + "mb) than recommended for this pack, the maximum is: " + configMax
                + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(Component.translatable("warning.toomuch",
                Component.literal(heapSetting + "").withStyle(ChatFormatting.YELLOW),
                Component.literal(configMax + "").withStyle(ChatFormatting.BLUE),
                Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (heapSetting < configMin - 250)
        {
            message += "You have less memory allocated(" + heapSetting + "mb) than recommended for this pack, the minimum is: " + configMin
                + "mb.\nThe recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(Component.translatable("warning.toolow",
                Component.literal(heapSetting + "").withStyle(ChatFormatting.YELLOW),
                Component.literal(configMin + "").withStyle(ChatFormatting.BLUE),
                Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if ((Math.abs(heapSetting - recommendMemory) / (double) recommendMemory) * 100 > config.getCommonConfig().warningTolerance && !(heapSetting > configMax
            || heapSetting < configMin))
        {
            message += "You have " + (heapSetting > recommendMemory ? "more" : "less")
                + " more memory allocated than recommended for your system, the recommended amount for your system is: " + recommendMemory + " mb.\n";
            memorycheckresult.append(Component.translatable(heapSetting > recommendMemory ? "warning.overrecommended" : "warning.underrecommended",
                Component.literal(heapSetting + "").withStyle(ChatFormatting.YELLOW),
                Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN)));
        }

        if (recommendMemory < configMin - 250)
        {
            message += "The recommended for your system is lower than the required minimum of " + configMin
                + "mb for this pack, things may not work out so well.\nMost common sign of insufficient ram is frequent stutters.\n";
            memorycheckresult.append(Component.translatable("warning.recommendedbelowmin",
                Component.literal(recommendMemory + "").withStyle(ChatFormatting.GREEN),
                Component.literal(configMin + "").withStyle(ChatFormatting.RED)));
        }

        if (message.equals(""))
        {
            return;
        }

        LOGGER.warn(message);
    }
}
