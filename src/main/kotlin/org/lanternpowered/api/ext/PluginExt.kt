/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.api.ext

import org.lanternpowered.api.Sponge
import org.lanternpowered.api.plugin.PluginContainer

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
        !Sponge.getPluginManager().getPlugin(pluginInstance)
    } else {
        !Sponge.getPluginManager().fromInstance(pluginInstance)
    }
    return checkNotNull(container) {
        if (message == nullMessage) message() else "invalid plugin: $pluginInstance"
    }
}
