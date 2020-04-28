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
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.GenericEvent
import java.util.Objects

class EventType<T : Event> internal constructor(val token: TypeToken<T>) {

    /**
     * Whether the event is generic.
     */
    val isGeneric = GenericEvent::class.java.isAssignableFrom(this.token.rawType)

    private var hashCode = 0

    override fun toString(): String = this.token.toString()

    override fun equals(other: Any?): Boolean {
        if (other !is EventType<*>)
            return false
        return other.token == this.token
    }

    override fun hashCode(): Int {
        if (this.hashCode == 0) {
            this.hashCode = Objects.hash(this.token)
        }
        return this.hashCode
    }
}
