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
package org.lanternpowered.server.statistic

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.statistic.StatisticCategory
import org.spongepowered.api.text.translation.Translation
import java.util.Collections

class LanternStatisticCategoryForCatalogType<T : CatalogType>(
        key: CatalogKey, translation: Translation, private val catalogType: TypeToken<T>
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
