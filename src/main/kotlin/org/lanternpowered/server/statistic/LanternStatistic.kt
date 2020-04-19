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
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.scoreboard.criteria.Criterion
import org.spongepowered.api.statistic.StatisticCategory
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation
import java.text.NumberFormat

open class LanternStatistic(
        key: CatalogKey, translation: Translation,
        val internalId: String,
        private val format: NumberFormat,
        private val type: StatisticCategory,
        private val criterion: Criterion?
) : DefaultCatalogType(key), XStatistic, Translatable by Translated(translation) {

    override fun toStringHelper() = super.toStringHelper()
            .omitNullValues()
            .add("category", this.type.key)
            .add("criterion", this.criterion?.key)

    override fun getCriterion() = this.criterion.optional()
    override fun getFormat() = this.format
    override fun getType() = this.type
}
