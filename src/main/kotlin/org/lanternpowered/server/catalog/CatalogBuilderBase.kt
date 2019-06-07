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

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.ext.*
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.util.CatalogBuilder
import org.spongepowered.api.util.ResettableBuilder

abstract class CatalogBuilderBase<C : CatalogType, B : ResettableBuilder<C, B>> : CatalogBuilder<C, B> {

    protected var key: CatalogKey? = null

    override fun key(key: CatalogKey) = apply {
        check(key.namespace.isNotBlank()) { "The key namespace may not be blank." }
        check(key.value.isNotBlank()) { "The key value may not be blank." }
        this.key = key
    } as B

    override fun id(id: String): B {
        check(id.isNotBlank()) { "The id may not be blank." }
        val pluginContainer = CauseStack.current().first<PluginContainer>()
                ?: throw IllegalStateException("Unable to find PluginContainer in the cause stack.")
        return key(CatalogKey.of(pluginContainer.id, id))
    }

    override fun reset() = apply {
        this.key = null
    } as B
}
