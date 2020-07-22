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
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.statistic.StatisticCategory
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation

abstract class AbstractStatisticCategory<T : Statistic>(
        key: NamespacedKey, translation: Translation
) : DefaultCatalogType(key), StatisticCategory, Translatable by Translated(translation) {

    abstract fun addStatistic(statistic: T)
}
