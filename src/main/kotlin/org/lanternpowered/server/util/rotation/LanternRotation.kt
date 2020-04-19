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
package org.lanternpowered.server.util.rotation

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.util.rotation.Rotation

class LanternRotation(key: CatalogKey, private val angle: Int) : DefaultCatalogType(key), Rotation {

    override fun getAngle() = this.angle
    override fun toStringHelper() = super.toStringHelper()
            .add("angle", this.angle)
}
