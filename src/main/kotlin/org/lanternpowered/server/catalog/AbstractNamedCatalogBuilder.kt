/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
