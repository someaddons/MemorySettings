package com.memorysettings;

import com.memorysettings.config.Configuration;
import com.memorysettings.event.ClientEventHandler;
import com.memorysettings.event.EventHandler;
import com.memorysettings.event.ModEventHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.Random;

import static com.memorysettings.MemorysettingsMod.MODID;
import static net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MODID)
public class MemorysettingsMod
{
    public static final String        MODID  = "memorysettings";
    public static final Logger        LOGGER = LogManager.getLogger();
    public static       Configuration config = new Configuration();
    public static       Random        rand   = new Random();

    public MemorysettingsMod()
    {
        ModLoadingContext.get()
          .registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> org.apache.commons.lang3.tuple.Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(ModEventHandler.class);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    public static void checkMemory()
    {
        if (config.getCommonConfig().disableWarnings.get())
        {
            return;
        }

        final int systemMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1048576);
        final int heapSetting = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576);
        final int recommendMemory = (int) Math.min(((systemMemory * 0.7) * 0.8), config.getCommonConfig().maximumClient.get());

        if (System.getProperties().getProperty("sun.arch.data.model").equals("32") && systemMemory > 4096)
        {
            message("You're using 32bit java on a 64bit system, please install 64bit java.");
            return;
        }

        final int configMax = (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().maximumClient.get() : config.getCommonConfig().maximumServer.get();
        final int configMin = (FMLEnvironment.dist != DEDICATED_SERVER) ? config.getCommonConfig().minimumClient.get() : config.getCommonConfig().minimumServer.get();

        String message = "";
        if (heapSetting > configMax)
        {
            message += "You have more memory allocated than recommended for this pack, the maximum is: " + configMax
                         + "mb. The recommended amount for your system is: " + recommendMemory + " mb." + config.getCommonConfig().helpfullinkmessage.get();
        }

        if (heapSetting < configMin)
        {

            message += "You have less memory allocated than recommended for this pack, the minimum is: " + config.getCommonConfig().minimumClient.get()
                         + "mb. The recommended amount for your system is: " + recommendMemory + " mb." + config.getCommonConfig().helpfullinkmessage.get();
        }

        if (heapSetting > (recommendMemory + 523))
        {
            message += "You have more memory allocated than recommended for your system, the recommended amount for your system is: " + recommendMemory + " mb.";
        }

        if (recommendMemory < configMin)
        {
            message += "The recommended for your system is lower than the required minimum of " + config.getCommonConfig().minimumClient.get()
                         + "mb for this pack, things may not work out so well. Most common sign of insufficient ram is frequent stutters."
                         + config.getCommonConfig().helpfullinkmessage.get();
        }

        if (message.equals(""))
        {
            return;
        }

        message += config.getCommonConfig().helpfullinkmessage.get();
        message(message);
    }

    /**
     * Message to log and gui if existing
     *
     * @param s
     */
    private static void message(final String s)
    {
        try
        {
            if (FMLEnvironment.dist != DEDICATED_SERVER)
            {
                javax.swing.SwingUtilities.invokeLater(() ->
                {
                    String[] options = new String[] {"Ok", "Stop showing"};
                    JFrame jf = new JFrame();
                    jf.setAlwaysOnTop(true);
                    if (JOptionPane.showOptionDialog(
                      jf,
                      s,
                      "Memory Settings",
                      JOptionPane.YES_NO_OPTION,
                      JOptionPane.PLAIN_MESSAGE,
                      null,
                      options,
                      options[0]) == 1)
                    {
                        config.getCommonConfig().disableWarnings.set(true);
                        LOGGER.warn("No longer showing memory warnings, if you want to see them again enable those in memorysettings-common.toml");
                    }
                });
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Error during showing memory warning GUI:", e);
        }

        LOGGER.error(s);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }
}
