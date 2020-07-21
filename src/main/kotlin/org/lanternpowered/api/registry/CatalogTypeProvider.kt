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
package org.lanternpowered.api.registry

import org.lanternpowered.api.catalog.CatalogType
import java.util.function.Supplier
import kotlin.reflect.KProperty

/**
 * Represents the provider of a single catalog type.
 */
interface CatalogTypeProvider<T : CatalogType> : Supplier<T> {

    /**
     * Gets the catalog type.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
}
