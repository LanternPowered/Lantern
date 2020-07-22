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
import org.spongepowered.api.NamedCatalogType
import org.spongepowered.api.statistic.Statistic

interface XStatistic : Statistic, NamedCatalogType {

    interface TypeInstance<T : CatalogType> : XStatistic, Statistic.TypeInstance<T>
}
