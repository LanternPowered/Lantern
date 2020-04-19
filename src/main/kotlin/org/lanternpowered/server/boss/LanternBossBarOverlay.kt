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
package org.lanternpowered.server.boss

import org.lanternpowered.api.boss.BossBarOverlay
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType

class LanternBossBarOverlay(key: CatalogKey, override val internalId: Int) :
        DefaultCatalogType(key), BossBarOverlay, InternalCatalogType
