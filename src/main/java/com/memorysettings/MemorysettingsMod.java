package com.memorysettings;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.memorysettings.Memory.heapSetting;
import static com.memorysettings.Memory.systemMemory;
import static com.memorysettings.MemorysettingsMod.MODID;
import static com.memorysettings.config.CommonConfiguration.config;
import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class MemorysettingsMod
{
    public static final String           MODID             = "memorysettings";
    public static final Logger           LOGGER            = LogManager.getLogger();
    public static       Random           rand              = new Random();
    public static       MutableComponent memorycheckresult = Component.empty();
    public static       boolean          didDisplay        = false;

    public MemorysettingsMod()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (c, b) -> true));

        if (!config.getCommonConfig().disableWarnings)
        {
            doWarning();
        }
    }

    public static void doWarning()
    {
        final int configMax =
            (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().maximumClient : config.getCommonConfig().maximumServer;
        final int configMin =
            (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().minimumClient : config.getCommonConfig().minimumServer;
        final int recommendMemory = Math.min(Memory.getRecommendedMemoryForSystemMemory(systemMemory), configMax);

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
