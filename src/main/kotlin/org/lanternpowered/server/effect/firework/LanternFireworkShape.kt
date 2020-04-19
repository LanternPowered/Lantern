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
package org.lanternpowered.server.effect.firework

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.effect.firework.FireworkShape
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType

class LanternFireworkShape(key: CatalogKey, override val internalId: Int) :
        DefaultCatalogType(key), FireworkShape, InternalCatalogType
