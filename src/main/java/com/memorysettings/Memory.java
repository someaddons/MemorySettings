package com.memorysettings;

import com.memorysettings.config.CommonConfiguration;
import net.neoforged.fml.loading.FMLEnvironment;

import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static net.neoforged.api.distmarker.Dist.DEDICATED_SERVER;

public class Memory
{
    public static int systemMemory = 0;
    public static int freeMemory   = 0;
    public static int heapSetting  = 0;

    public static void recordMemory()
    {
        systemMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize() / 1048576);
        freeMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreeMemorySize() / 1048576);
        heapSetting = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576);
    }

    public static void doEarlyWarnings()
    {
        if (CommonConfiguration.config.getCommonConfig().disableWarnings)
        {
            return;
        }

        final boolean dedi = FMLEnvironment.dist == DEDICATED_SERVER;

        final int configMax =
            !dedi ? CommonConfiguration.config.getCommonConfig().maximumClient : CommonConfiguration.config.getCommonConfig().maximumServer;
        final int configMin =
            !dedi ? CommonConfiguration.config.getCommonConfig().minimumClient : CommonConfiguration.config.getCommonConfig().minimumServer;
        final int recommendMemory = Math.min(getRecommendedMemoryForSystemMemory(systemMemory), configMax);

        if (heapSetting < configMin - 150)
        {
            String message = String.format(CommonConfiguration.config.getCommonConfig().earlyWarningMinimumMemory, heapSetting, configMin, recommendMemory);

            if (!dedi)
            {
                URI uri = null;
                try
                {
                    uri = new URI(CommonConfiguration.config.getCommonConfig().howtolink);
                }
                catch (URISyntaxException e)
                {
                    CommonConfiguration.LOGGER.warn("Failed to parse url: " + CommonConfiguration.config.getCommonConfig().howtolink, e);
                }

                EarlyWarning.showEarlyScreenFor(message, uri);
            }
            else
            {
                CommonConfiguration.LOGGER.warn(message);
            }
        }

        if (System.getProperties().getProperty("sun.arch.data.model").equals("32") && systemMemory > 4096)
        {
            String message = "You're using 32bit java on a 64bit system, please install 64bit java.";

            if (!dedi)
            {
                URI uri = null;
                try
                {
                    uri = new URI("https://adoptopenjdk.net/releases.html");
                }
                catch (URISyntaxException e)
                {
                    CommonConfiguration.LOGGER.warn("Failed to parse url: https://adoptopenjdk.net/releases.html", e);
                }
                EarlyWarning.showEarlyScreenFor(message, uri);
            }
            else
            {
                CommonConfiguration.LOGGER.warn(message + " you can find it here: https://adoptopenjdk.net/releases.html");
            }
        }
    }

    public static int getRecommendedMemoryForSystemMemory(final int systemMemory)
    {
        Map.Entry<Integer, Integer> lastEntry = null;
        int recommendedMemory = 0;
        for (final Map.Entry<Integer, Integer> dataEntry : CommonConfiguration.config.getCommonConfig().recommendedMemory.entrySet())
        {
            if (systemMemory > dataEntry.getKey())
            {
                lastEntry = dataEntry;
            }
            else
            {
                if (lastEntry == null)
                {
                    lastEntry = dataEntry;
                    break;
                }

                double percent = (systemMemory - lastEntry.getKey()) / (Math.max(1d, dataEntry.getKey() - lastEntry.getKey()));

                return (int) (lastEntry.getValue() + percent * (dataEntry.getValue() - lastEntry.getValue()));
            }
        }

        if (lastEntry != null)
        {
            return lastEntry.getValue();
        }

        return recommendedMemory;
    }
}
