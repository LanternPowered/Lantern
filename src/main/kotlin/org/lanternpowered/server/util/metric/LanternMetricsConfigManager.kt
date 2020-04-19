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

import com.google.inject.Singleton
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.server.game.Lantern
import org.spongepowered.api.util.Tristate
import org.spongepowered.api.util.metric.MetricsConfigManager

@Singleton
class LanternMetricsConfigManager : MetricsConfigManager {

    fun areMetricsEnabled(container: PluginContainer): Boolean {
        val pluginState = getCollectionState(container)
        if (pluginState == Tristate.TRUE)
            return true
        return this.globalCollectionState == Tristate.TRUE && pluginState == Tristate.UNDEFINED
    }

    override fun getGlobalCollectionState(): Tristate
            = Lantern.getGame().globalConfig.metricsCategory.globalState

    override fun getCollectionState(container: PluginContainer): Tristate
            = Lantern.getGame().globalConfig.metricsCategory.getCollectionState(container)
}
