package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.event.Event;

public abstract class AnnotatedEventHandler implements LanternEventHandler<Event> {

    protected final Object handle;

    protected AnnotatedEventHandler(Object handle) {
        this.handle = checkNotNull(handle, "handle");
    }

    @Override
    public Object getHandle() {
        return this.handle;
    }
}
