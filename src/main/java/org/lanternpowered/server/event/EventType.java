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

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.event.Event;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class EventType<T extends Event> {

    private final Class<T> eventType;
    @Nullable private final TypeToken<?> genericType;

    private int hashCode;

    EventType(Class<T> eventType, @Nullable TypeToken<?> genericType) {
        this.genericType = genericType;
        this.eventType = eventType;
    }

    public Class<T> getType() {
        return this.eventType;
    }

    @Nullable
    public TypeToken<?> getGenericType() {
        return this.genericType;
    }

    @Override
    public String toString() {
        String value = this.eventType.getName();
        if (this.genericType != null) {
            value += "<" + this.genericType.toString() + ">";
        }
        return value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof EventType)) {
            return false;
        }
        final EventType that = (EventType) o;
        return that.eventType.equals(this.eventType) &&
                Objects.equals(that.genericType, this.genericType);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Objects.hash(this.eventType, this.genericType);
        }
        return this.hashCode;
    }
}
