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
package org.lanternpowered.server.cause.entity.healing

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.cause.entity.health.HealingType
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternHealingType(key: CatalogKey) : DefaultCatalogType(key), HealingType
