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

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.namespace.NamespacedKey
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
        override fun key(key: NamespacedKey): ForCatalogType<C>
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
