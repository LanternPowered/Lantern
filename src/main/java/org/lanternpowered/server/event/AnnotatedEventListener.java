/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.event.Event;

import java.lang.reflect.Method;

public abstract class AnnotatedEventListener implements LanternEventListener<Event> {

    protected final Object handle;

    protected AnnotatedEventListener(Object handle) {
        this.handle = checkNotNull(handle, "handle");
    }

    @Override
    public Object getHandle() {
        return this.handle;
    }

    interface Factory {

        AnnotatedEventListener create(Object handle, Method method) throws Exception;
    }
}
