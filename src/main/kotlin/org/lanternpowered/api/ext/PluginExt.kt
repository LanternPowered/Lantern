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
package org.lanternpowered.api.ext

import org.lanternpowered.api.Sponge
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.util.optional.orNull

// Represents a "null" message,
private val nullMessage: () -> Any = { "" }

/**
 * Ensures that the given plugin instance is an actual plugin
 * instance that is bound to a [PluginContainer].
 *
 * @param pluginInstance The plugin instance
 * @param message The exception message to use if the check fails
 * @return The bound plugin container
 */
fun checkPluginInstance(pluginInstance: Any, message: () -> Any = nullMessage): PluginContainer {
    if (pluginInstance is PluginContainer) {
        return pluginInstance
    }
    val container = if (pluginInstance is String) {
        Sponge.getPluginManager().getPlugin(pluginInstance)
    } else {
        Sponge.getPluginManager().fromInstance(pluginInstance)
    }.orNull()
    return checkNotNull(container) {
        if (message == nullMessage) message() else "invalid plugin: $pluginInstance"
    }
}
