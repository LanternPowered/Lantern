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

import org.lanternpowered.api.catalog.CatalogKey
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.text.translation.Translation
import java.util.Collections

class LanternStatisticCategory(
        key: CatalogKey, translation: Translation
) : AbstractStatisticCategory<Statistic>(key, translation) {

    private val statistics = arrayListOf<Statistic>()
    private val unmodifiableStatistics = Collections.unmodifiableList(this.statistics)

    override fun addStatistic(statistic: Statistic) {
        this.statistics.add(statistic)
    }

    override fun getStatistics(): MutableCollection<Statistic> = this.unmodifiableStatistics
}
