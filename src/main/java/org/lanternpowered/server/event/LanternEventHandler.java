package org.lanternpowered.server.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventHandler;

public interface LanternEventHandler<T extends Event> extends EventHandler<T> {

    Object getHandle();
}
