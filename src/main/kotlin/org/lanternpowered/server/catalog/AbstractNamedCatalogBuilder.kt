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

import org.spongepowered.api.ResourceKey
import org.spongepowered.api.NamedCatalogType
import org.spongepowered.api.util.NamedCatalogBuilder
import org.spongepowered.api.util.ResettableBuilder

abstract class AbstractNamedCatalogBuilder<C : NamedCatalogType, B : ResettableBuilder<C, B>> :
        CatalogBuilderBase<C, B>(), NamedCatalogBuilder<C, B> {

    protected var name: String? = null

    override fun name(name: String) = apply {
        check(name.isNotBlank()) { "The name may not be blank." }
        this.name = name
    } as B

    protected open fun getFinalName(): String = this.name ?: this.key!!.value

    override fun build(): C {
        val key = checkNotNull(this.key) { "The key must be set." }
        val name = getFinalName()
        return build(key, name)
    }

    protected abstract fun build(key: ResourceKey, name: String): C

    override fun reset() = super.reset().apply {
        name = null
    }
}
