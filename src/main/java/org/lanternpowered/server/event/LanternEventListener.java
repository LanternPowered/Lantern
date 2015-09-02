package org.lanternpowered.server.event;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

public interface LanternEventListener<T extends Event> extends EventListener<T> {

    Object getHandle();
}
