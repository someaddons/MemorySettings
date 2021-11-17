package com.memorysettings.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfiguration
{
    public final ForgeConfigSpec                      ForgeConfigSpecBuilder;
    public final ForgeConfigSpec.ConfigValue<Integer> minimumClient;
    public final ForgeConfigSpec.ConfigValue<Integer> minimumServer;
    public final ForgeConfigSpec.ConfigValue<Integer> maximumClient;
    public final ForgeConfigSpec.ConfigValue<Integer> maximumServer;
    public final ForgeConfigSpec.ConfigValue<Boolean> disableWarnings;
    public final ForgeConfigSpec.ConfigValue<String>  helpfullinkmessage;

    protected CommonConfiguration(final ForgeConfigSpec.Builder builder)
    {
        builder.push("Config category");

        builder.comment("Set the clients minimum memory warning threshold in MB. Choose the lowest value possible which keeps the pack playable.");
        minimumClient = builder.defineInRange("minimumClient", 2500, 2500, 10000);

        builder.comment("Set the clients maximum memory warning threshold in MB. Choose a generous maximum with some additional over the required, e.g."
                          + "recommended is 6000mb then set this to ~8000mb");
        maximumClient = builder.defineInRange("maximumClient", 8500, 2500, 25000);

        builder.comment("Set the servers minimum memory warning threshold in MB. Choose the lowest value possible which keeps the pack playable.");
        minimumServer = builder.defineInRange("minimumServer", 2500, 2500, 10000);

        builder.comment("Set the servers maximum memory warning threshold in MB. Choose a generous maximum with some additional over the required, e.g."
                          + "recommended is 6000mb then set this to ~8000mb");
        maximumServer = builder.defineInRange("maximumServer", 8500, 2500, 25000);

        builder.comment("Disable the memory warnings, default = false");
        disableWarnings = builder.define("disableWarnings", false);

        builder.comment("Set the help message for changing ram allocation, you can use html here e.g. for setting a link to a wiki page");
        helpfullinkmessage = builder.define("helpfullinkmessage",
          "See <a href='https://minecrafthelp.co.uk/how-to-add-more-ram-to-minecraft/'>https://minecrafthelp.co.uk/how-to-add-more-ram-to-minecraft/</a> on how to change your memory settings.");

        // Escapes the current category level
        builder.pop();
        ForgeConfigSpecBuilder = builder.build();
    }
}
