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
package org.lanternpowered.server.timings

import co.aikar.timings.Timing
import co.aikar.timings.TimingsFactory
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.channel.MessageChannel

object DummyTimingsFactory : TimingsFactory {
    override fun of(plugin: PluginContainer, name: String, groupHandler: Timing?): Timing = DummyTiming
    override fun isTimingsEnabled(): Boolean = false
    override fun setTimingsEnabled(enabled: Boolean) {}
    override fun isVerboseTimingsEnabled(): Boolean = false
    override fun setVerboseTimingsEnabled(enabled: Boolean) {}
    override fun getHistoryInterval(): Int = 0
    override fun setHistoryInterval(interval: Int) {}
    override fun getHistoryLength(): Int = 0
    override fun setHistoryLength(length: Int) {}
    override fun reset() {}
    override fun generateReport(channel: MessageChannel) {}
}
