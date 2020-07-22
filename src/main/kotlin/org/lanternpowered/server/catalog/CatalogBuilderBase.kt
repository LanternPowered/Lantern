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
import org.spongepowered.api.util.CatalogBuilder
import org.spongepowered.api.util.ResettableBuilder

abstract class CatalogBuilderBase<C : CatalogType, B : ResettableBuilder<C, B>> : CatalogBuilder<C, B> {

    protected var key: NamespacedKey? = null

    override fun key(key: NamespacedKey) = apply {
        check(key.namespace.isNotBlank()) { "The key namespace may not be blank." }
        check(key.value.isNotBlank()) { "The key value may not be blank." }
        this.key = key
    } as B

    override fun reset() = apply {
        this.key = null
    } as B
}
