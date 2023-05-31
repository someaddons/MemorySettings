package com.memorysettings.config;

import com.google.gson.JsonObject;
import com.memorysettings.MemorysettingsMod;

public class CommonConfiguration
{
    public int     minimumClient   = 2500;
    public int     minimumServer   = 2500;
    public int     maximumClient   = 8500;
    public int     maximumServer   = 8500;
    public boolean disableWarnings = false;
    public String  howtolink       = "https://apexminecrafthosting.com/how-to-allocate-more-ram/";

    protected CommonConfiguration()
    {

    }

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Set the clients minimum memory warning threshold in MB. Choose the lowest value possible which keeps the pack playable."
                                     + " default:2500, min 2500, max 25000");
        entry.addProperty("minimumClient", minimumClient);
        root.add("minimumClient", entry);

        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:", "Set the clients maximum memory warning threshold in MB. Choose a generous maximum with some additional over the required, e.g."
                                      + "recommended memory for the pack is 6000mb then set this to ~8000mb");
        entry2.addProperty("maximumClient", maximumClient);
        root.add("maximumClient", entry2);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:", "Set the servers minimum memory warning threshold in MB. Choose the lowest value possible which keeps the pack playable." +
                                      " default:2500, min 2500, max 25000");
        entry3.addProperty("minimumServer", minimumServer);
        root.add("minimumServer", entry3);

        final JsonObject entry4 = new JsonObject();
        entry4.addProperty("desc:", "Set the servers maximum memory warning threshold in MB. Choose a generous maximum with some additional over the required, e.g."
                                      + "recommended is 6000mb then set this to ~8000mb");
        entry4.addProperty("maximumServer", maximumServer);
        root.add("maximumServer", entry4);

        final JsonObject entry5 = new JsonObject();
        entry5.addProperty("desc:", "Disable the memory warnings, default: false");
        entry5.addProperty("disableWarnings", disableWarnings);
        root.add("disableWarnings", entry5);

        final JsonObject entry6 = new JsonObject();
        entry6.addProperty("desc:", "Set the link used to guide players to a website with instructions to change memory allocation");
        entry6.addProperty("howtolink", howtolink);
        root.add("howtolink", entry6);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        if (data == null)
        {
            MemorysettingsMod.LOGGER.error("Config file was empty!");
            return;
        }

        try
        {
            minimumClient = data.get("minimumClient").getAsJsonObject().get("minimumClient").getAsInt();
            maximumClient = data.get("maximumClient").getAsJsonObject().get("maximumClient").getAsInt();
            minimumServer = data.get("minimumServer").getAsJsonObject().get("minimumServer").getAsInt();
            maximumServer = data.get("maximumServer").getAsJsonObject().get("maximumServer").getAsInt();
            disableWarnings = data.get("disableWarnings").getAsJsonObject().get("disableWarnings").getAsBoolean();
            howtolink = data.get("howtolink").getAsJsonObject().get("howtolink").getAsString();
        }
        catch (Exception e)
        {
            MemorysettingsMod.LOGGER.error("Could not parse config file", e);
        }
    }
}
