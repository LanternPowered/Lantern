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
package org.lanternpowered.server.data.type

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.asString

enum class RedstoneConnectionType(id: String) : CatalogType by DefaultCatalogType.minecraft(id) {

    NONE        ("none"),
    SIDE        ("side"),
    UP          ("up"),
    ;

    override fun toString(): String = asString()
}
