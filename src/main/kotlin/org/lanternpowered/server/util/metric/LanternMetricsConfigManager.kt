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
package org.lanternpowered.server.util.metric

import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.id
import org.lanternpowered.api.util.Tristate
import org.lanternpowered.server.config.GlobalConfigObject
import org.spongepowered.api.util.metric.MetricsConfigManager

class LanternMetricsConfigManager(private val config: GlobalConfigObject) : MetricsConfigManager {

    fun areMetricsEnabled(container: PluginContainer): Boolean {
        val pluginState = getCollectionState(container)
        if (pluginState == Tristate.TRUE)
            return true
        return this.globalCollectionState == Tristate.TRUE && pluginState == Tristate.UNDEFINED
    }

    override fun getGlobalCollectionState(): Tristate =
            this.config.metrics.globalState

    override fun getCollectionState(container: PluginContainer): Tristate =
            this.config.metrics.pluginStates[container.id] ?: Tristate.UNDEFINED
}
