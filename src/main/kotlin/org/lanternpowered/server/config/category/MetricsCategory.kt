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
package org.lanternpowered.server.config.category

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.id
import org.spongepowered.api.util.Tristate
import java.util.*

@ConfigSerializable
class MetricsCategory {

    @field:Setting(value = "global-state", comment = "The global collection state that should be respected " +
            "by all plugins that have no specified collection state. If undefined then it is treated as disabled.")
    var globalState = Tristate.UNDEFINED
        private set

    @Setting(value = "plugin-states", comment = "Plugin-specific collection states that override the global collection state.")
    private val pluginStates = HashMap<String, Tristate>()

    fun getCollectionState(container: PluginContainer): Tristate
            = this.pluginStates[container.id] ?: Tristate.UNDEFINED
}
