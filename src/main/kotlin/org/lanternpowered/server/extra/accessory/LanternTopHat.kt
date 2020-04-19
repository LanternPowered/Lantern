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
package org.lanternpowered.server.extra.accessory

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.data.type.DyeColor
import java.util.Optional

class LanternTopHat @JvmOverloads constructor(key: CatalogKey, private val dyeColor: DyeColor? = null) :
        DefaultCatalogType(key), TopHat {

    override fun getDyeColor(): Optional<DyeColor> = Optional.ofNullable(this.dyeColor)
}
