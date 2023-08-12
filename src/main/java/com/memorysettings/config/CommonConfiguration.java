package com.memorysettings.config;

import com.cupboard.config.ICommonConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.memorysettings.MemorysettingsMod;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommonConfiguration implements ICommonConfig
{
    public int                   minimumClient     = 2500;
    public int                   minimumServer     = 2500;
    public int                   maximumClient     = 8500;
    public int                   maximumServer     = 8500;
    public int                   warningTolerance  = 30;
    public boolean               disableWarnings   = false;
    public String                howtolink         = "https://apexminecrafthosting.com/how-to-allocate-more-ram/";
    public Map<Integer, Integer> recommendedMemory = new LinkedHashMap<>();

    public CommonConfiguration()
    {
        // System memory - recommended memory
        recommendedMemory.put(3000, 2000);
        recommendedMemory.put(4000, 3000);
        recommendedMemory.put(5000, 3500);
        recommendedMemory.put(6000, 3700);
        recommendedMemory.put(7000, 4000);
        recommendedMemory.put(8000, 4200);
        recommendedMemory.put(10000, 5000);
        recommendedMemory.put(12000, 6000);
        recommendedMemory.put(16000, 7000);
        recommendedMemory.put(20000, 8000);
        recommendedMemory.put(24000, 9000);
        recommendedMemory.put(32000, 9500);
        recommendedMemory.put(64000, 10000);
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

        final JsonObject entry8 = new JsonObject();
        entry8.addProperty("desc:", "Set how many percent the memory is allowed to deviate from the recommended for the system before warning about it, default: 30, max 100");
        entry8.addProperty("warningTolerance", warningTolerance);
        root.add("warningTolerance", entry8);

        final JsonObject entry7 = new JsonObject();
        entry7.addProperty("desc:", "Set the recommended memory values based off system memory in MB. [\"system memory:recommended\"]");
        JsonArray array = new JsonArray();
        for (final Map.Entry<Integer, Integer> entrydata : recommendedMemory.entrySet())
        {
            array.add(entrydata.getKey().toString() + ":" + entrydata.getValue().toString());
        }

        entry7.add("memory values", array);
        root.add("recommendedMemory", entry7);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        minimumClient = data.get("minimumClient").getAsJsonObject().get("minimumClient").getAsInt();
        maximumClient = data.get("maximumClient").getAsJsonObject().get("maximumClient").getAsInt();
        minimumServer = data.get("minimumServer").getAsJsonObject().get("minimumServer").getAsInt();
        maximumServer = data.get("maximumServer").getAsJsonObject().get("maximumServer").getAsInt();
        warningTolerance = data.get("warningTolerance").getAsJsonObject().get("warningTolerance").getAsInt();
        disableWarnings = data.get("disableWarnings").getAsJsonObject().get("disableWarnings").getAsBoolean();
        howtolink = data.get("howtolink").getAsJsonObject().get("howtolink").getAsString();
        Map<Integer, Integer> loading = new LinkedHashMap<>();
        for (final JsonElement entry : data.get("recommendedMemory").getAsJsonObject().get("memory values").getAsJsonArray())
        {
            String[] parsedEntry = entry.getAsString().split(":");
            loading.put(Integer.parseInt(parsedEntry[0]), Integer.parseInt(parsedEntry[1]));
        }

        recommendedMemory = loading;

        // Fix old config
        if (warningTolerance > 200)
        {
            warningTolerance = 30;
            MemorysettingsMod.config.save();
        }
    }
}
