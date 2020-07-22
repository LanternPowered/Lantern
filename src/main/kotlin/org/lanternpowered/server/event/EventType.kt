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
package org.lanternpowered.server.event

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.event.Event
import org.lanternpowered.api.event.GenericEvent
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.uncheckedCast
import java.lang.reflect.TypeVariable
import java.util.Objects

class EventType<T : Event> internal constructor(val eventClass: Class<T>, val genericParameter: TypeToken<*>?) {

    /**
     * Whether the event is generic.
     */
    val isGeneric = GenericEvent::class.java.isAssignableFrom(this.eventClass)

    private var hashCode = 0

    override fun toString(): String = ToStringHelper(this)
            .add("eventClass", this.eventClass.name)
            .add("genericParameter", this.genericParameter)
            .omitNullValues()
            .toString()

    override fun equals(other: Any?): Boolean {
        if (other !is EventType<*>)
            return false
        return other.eventClass == this.eventClass &&
                other.genericParameter == this.genericParameter
    }

    override fun hashCode(): Int {
        if (this.hashCode == 0) {
            this.hashCode = Objects.hash(this.eventClass, this.genericParameter)
        }
        return this.hashCode
    }

    companion object {

        private val genericEventType: TypeVariable<*> = GenericEvent::class.java.typeParameters[0]

        fun <T : Event> of(typeToken: TypeToken<T>): EventType<T> {
            if (!GenericEvent::class.java.isAssignableFrom(typeToken.rawType))
                return EventType(typeToken.rawType.uncheckedCast(), null)
            val genericParameter = typeToken.resolveType(this.genericEventType)
            return EventType(typeToken.rawType.uncheckedCast(), genericParameter)
        }
    }
}
