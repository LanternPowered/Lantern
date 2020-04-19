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
package org.lanternpowered.server.data.persistence

import org.lanternpowered.api.catalog.CatalogKey
import org.spongepowered.api.data.persistence.StringDataFormat

abstract class AbstractStringDataFormat(key: CatalogKey) : AbstractDataFormat(key), StringDataFormat
