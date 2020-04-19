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
package org.lanternpowered.server.catalog

interface InternalCatalogType {

    /**
     * The internal id of this catalog type.
     */
    val internalId: Int

    /**
     * Returns a internal id based on the ordinal of the enum value.
     */
    interface EnumOrdinal : InternalCatalogType {

        override val internalId: Int
            get() = (this as Enum<*>).ordinal
    }
}
