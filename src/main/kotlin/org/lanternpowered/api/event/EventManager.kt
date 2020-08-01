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
package org.lanternpowered.api.event

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.util.type.TypeToken
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.Order
import org.spongepowered.plugin.PluginContainer
import kotlin.reflect.KClass

/**
 * The event manager.
 */
interface EventManager : org.spongepowered.api.event.EventManager {

    /**
     * Registers an event listener for a specific event class.
     *
     * Normally, the annotation-based way in [registerListeners] should be preferred
     * over this way. This method exists primarily to support dynamic event registration
     * like needed in scripting plugins.
     *
     * @param plugin The plugin instance
     * @param eventClass The event to listen to
     * @param listener The listener to receive the events
     * @param T The type of the event
     */
    fun <T : Event> registerListener(plugin: PluginContainer, eventClass: KClass<T>, listener: EventListener<in T>)

    @Deprecated(
            message = "There are no mods on Lantern. This makes beforeModifications redundant.",
            replaceWith = ReplaceWith("registerListener(plugin, eventType, order, listener)"),
            level = DeprecationLevel.HIDDEN
    )
    override fun <T : Event> registerListener(
            plugin: PluginContainer, eventType: TypeToken<T>, order: Order, beforeModifications: Boolean, listener: EventListener<in T>) {
        this.registerListener(plugin, eventType, order, listener)
    }

    @Deprecated(
            message = "There are no mods on Lantern. This makes beforeModifications redundant.",
            replaceWith = ReplaceWith("registerListener(plugin, eventClass, order, listener)"),
            level = DeprecationLevel.HIDDEN
    )
    override fun <T : Event> registerListener(
            plugin: PluginContainer, eventClass: Class<T>, order: Order, beforeModifications: Boolean, listener: EventListener<in T>) {
        this.registerListener(plugin, eventClass, order, listener)
    }

    /**
     * The singleton instance of the event manager.
     */
    companion object : EventManager by Lantern.eventManager
}
