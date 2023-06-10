package com.memorysettings.event;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Forge event bus handler, ingame events are fired here
 */
public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(ScreenEvent.Opening event)
    {

    }
}
