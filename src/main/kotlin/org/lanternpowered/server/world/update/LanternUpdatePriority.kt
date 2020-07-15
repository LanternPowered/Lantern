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
package org.lanternpowered.server.world.update

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.world.scheduler.UpdatePriority
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternUpdatePriority(key: ResourceKey, val value: Int) : DefaultCatalogType(key), UpdatePriority
