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
package org.lanternpowered.server.statistic

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.asUnmodifiableCollection
import org.spongepowered.api.statistic.Statistic

class LanternStatisticCategory(
        key: NamespacedKey, text: Text
) : AbstractStatisticCategory<Statistic>(key, text) {

    private val statistics = arrayListOf<Statistic>()
    private val unmodifiableStatistics = this.statistics.asUnmodifiableCollection()

    override fun addStatistic(statistic: Statistic) {
        this.statistics.add(statistic)
    }

    override fun getStatistics(): Collection<Statistic> = this.unmodifiableStatistics
}
