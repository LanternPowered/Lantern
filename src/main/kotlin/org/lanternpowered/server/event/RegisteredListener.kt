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

import com.google.common.collect.Iterables
import org.lanternpowered.api.event.Event
import org.lanternpowered.api.event.EventListener
import org.lanternpowered.api.event.Order
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.id
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.contentToString
import org.lanternpowered.server.util.TypeTokenHelper

class RegisteredListener<T : Event>(
        val plugin: PluginContainer,
        val listener: EventListener<in T>,
        val eventType: EventType<T>,
        val order: Order,
        private val includedTypes: List<Class<*>>? = null,
        private val excludedTypes: List<Class<*>>? = null
) : Comparable<RegisteredListener<T>> {

    val handle: Any get() = if (this.listener is LanternEventListener<*>) this.listener.handle else this.listener

    fun handle(event: T) {
        this.listener.handle(event)
    }

    override fun compareTo(other: RegisteredListener<T>): Int =
            this.order.compareTo(other.order)

    /**
     * Checks whether the given event type is
     * applicable to this registered listener.
     */
    fun isApplicable(type: EventType<*>): Boolean {
        if (!isGenericTypeApplicable(type))
            return false
        if (!isIncluded(type))
            return false
        return true
    }

    private fun isIncluded(type: EventType<*>): Boolean {
        if (this.includedTypes != null)
            return this.includedTypes.any { it.isAssignableFrom(type.eventClass) }
        if (this.excludedTypes != null)
            return this.excludedTypes.none { it.isAssignableFrom(type.eventClass) }
        return true
    }

    private fun isGenericTypeApplicable(type: EventType<*>): Boolean {
        if (!this.eventType.isGeneric || this.eventType.genericParameter == null)
            return true
        return TypeTokenHelper.isAssignable(type.genericParameter!!, this.eventType.genericParameter)
    }

    override fun toString(): String = ToStringHelper(this)
            .add("plugin", this.plugin.id)
            .add("eventType", this.eventType)
            .add("order", this.order)
            .add("includedTypes", if (this.includedTypes == null) null else this.includedTypes.contentToString())
            .add("excludedTypes", if (this.excludedTypes == null) null else this.excludedTypes.contentToString())
            .toString()
}
