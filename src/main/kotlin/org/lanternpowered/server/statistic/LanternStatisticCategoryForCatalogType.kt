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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.catalog.CatalogType
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.statistic.StatisticCategory
import org.spongepowered.api.text.translation.Translation
import java.util.Collections

class LanternStatisticCategoryForCatalogType<T : CatalogType>(
        key: NamespacedKey, translation: Translation, private val catalogType: TypeToken<T>
) : AbstractStatisticCategory<Statistic.ForCatalog<T>>(key, translation), StatisticCategory.ForCatalogType<T> {

    private val statistics = hashMapOf<T, Statistic.ForCatalog<T>>()
    private val unmodifiableStatistics = Collections.unmodifiableCollection(this.statistics.values)

    override fun getCatalogType() = this.catalogType
    override fun getStatistics(): MutableCollection<Statistic.ForCatalog<T>> = this.unmodifiableStatistics

    override fun addStatistic(statistic: Statistic.ForCatalog<T>) {
        this.statistics[statistic.catalogType] = statistic
    }

    override fun getStatistic(catalogType: T): Statistic.ForCatalog<T> {
        return this.statistics[catalogType] ?: throw IllegalStateException("Unable to find statistic for ${catalogType.key}")
    }

    override fun toStringHelper() = super.toStringHelper()
            .add("catalogType", this.catalogType.rawType.name)
}
