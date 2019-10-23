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

import org.lanternpowered.api.catalog.CatalogType
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.scoreboard.criteria.Criterion
import org.spongepowered.api.statistic.Statistic
import org.spongepowered.api.statistic.StatisticCategory
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.util.NamedCatalogBuilder

interface StatisticBuilder : NamedCatalogBuilder<XStatistic, StatisticBuilder> {

    /**
     * Sets the translation for the [Statistic].
     *
     * @param translation The translation for the statistic
     * @return This builder, for chaining
     */
    fun translation(translation: Translation): StatisticBuilder

    /**
     * Sets the criterion of the [Statistic].
     *
     * @param criterion The criterion
     * @return This builder, for chaining
     */
    fun criterion(criterion: Criterion?): StatisticBuilder

    /**
     * Sets the [StatisticCategory].
     *
     * @param category The statistic category
     * @return This builder, for chaining
     */
    fun category(category: StatisticCategory): StatisticBuilder

    /**
     * Sets the [StatisticCategory].
     *
     * @param category The statistic category
     * @return This builder, for chaining
     */
    fun <C : CatalogType> category(category: StatisticCategory.ForCatalogType<C>): ForCatalogType<C>

    interface ForCatalogType<C : CatalogType> : StatisticBuilder {

        override fun category(category: StatisticCategory): ForCatalogType<C>
        override fun criterion(criterion: Criterion?): ForCatalogType<C>
        override fun translation(translation: Translation): ForCatalogType<C>
        override fun key(key: CatalogKey): ForCatalogType<C>
        override fun name(name: String): ForCatalogType<C>
        override fun name(translation: Translation): ForCatalogType<C>
        override fun reset(): ForCatalogType<C>

        /**
         * Sets the [CatalogType].
         *
         * @param catalogType The catalog type
         * @return This builder, for chaining
         */
        fun catalogType(catalogType: C)

        override fun build(): XStatistic.ForCatalog<C>
    }
}
