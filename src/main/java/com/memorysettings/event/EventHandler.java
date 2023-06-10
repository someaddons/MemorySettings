package com.memorysettings.event;

import com.memorysettings.MemoryErrorScreen;
import com.memorysettings.MemorysettingsMod;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Forge event bus handler, ingame events are fired here
 */
public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiOpen(ScreenEvent.Opening event)
    {
        if (event.getNewScreen() instanceof TitleScreen && !MemorysettingsMod.memorycheckresult.getSiblings().isEmpty()
              && !MemorysettingsMod.config.getCommonConfig().disableWarnings.get())
        {
            event.setNewScreen(new MemoryErrorScreen(MemorysettingsMod.memorycheckresult));
        }
    }
}
