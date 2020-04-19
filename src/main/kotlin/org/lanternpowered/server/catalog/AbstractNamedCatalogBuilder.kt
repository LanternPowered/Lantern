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

import org.spongepowered.api.CatalogKey
import org.spongepowered.api.NamedCatalogType
import org.spongepowered.api.text.translation.FixedTranslation
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.util.NamedCatalogBuilder
import org.spongepowered.api.util.ResettableBuilder

abstract class AbstractNamedCatalogBuilder<C : NamedCatalogType, B : ResettableBuilder<C, B>> :
        CatalogBuilderBase<C, B>(), NamedCatalogBuilder<C, B> {

    protected var name: Translation? = null

    override fun name(name: String) = apply {
        check(name.isNotBlank()) { "The name may not be blank." }
        this.name = FixedTranslation(name)
    } as B

    protected open fun getFinalName(): Translation = this.name ?: FixedTranslation(this.key!!.value)

    override fun name(name: Translation) = apply {
        check(name.id.isNotBlank()) { "The translation id may not be blank." }
        this.name = name
    } as B

    override fun build(): C {
        val key = checkNotNull(this.key) { "The key must be set." }
        val name = getFinalName()
        return build(key, name)
    }

    protected abstract fun build(key: CatalogKey, name: Translation): C

    override fun reset() = super.reset().apply {
        name = null
    }
}
