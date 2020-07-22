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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.statistic.StatisticCategory

abstract class AbstractStatisticCategory<T : Statistic>(
        key: NamespacedKey, text: Text
) : DefaultCatalogType(key), StatisticCategory, TextRepresentable by text {

    abstract fun addStatistic(statistic: T)
}
