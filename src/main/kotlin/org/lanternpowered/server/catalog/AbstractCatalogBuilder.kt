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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.catalog

import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.util.ResettableBuilder

abstract class AbstractCatalogBuilder<C : CatalogType, B : ResettableBuilder<C, B>> : CatalogBuilderBase<C, B>() {

    override fun build(): C {
        val key = checkNotNull(this.key) { "The key must be set." }
        return build(key)
    }

    protected abstract fun build(key: NamespacedKey): C
}
