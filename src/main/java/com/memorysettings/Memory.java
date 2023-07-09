package com.memorysettings;

import java.lang.management.ManagementFactory;

public class Memory
{
    public static int systemMemory = 0;
    public static int freeMemory   = 0;
    public static int heapSetting  = 0;

    public static void recordMemory()
    {
        systemMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() / 1048576);
        freeMemory = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize() / 1048576);
        heapSetting = (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576);
    }
}
