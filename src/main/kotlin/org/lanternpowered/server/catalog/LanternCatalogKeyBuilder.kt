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
package org.lanternpowered.server.catalog

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogKeyBuilder
import org.lanternpowered.api.plugin.PluginContainer

class LanternCatalogKeyBuilder : CatalogKeyBuilder {

    private var namespace: String? = null
    private var value: String? = null

    override fun namespace(namespace: String): CatalogKeyBuilder = apply { this.namespace = namespace }
    override fun namespace(container: PluginContainer): CatalogKeyBuilder = namespace(container.id)
    override fun value(value: String): CatalogKeyBuilder = apply { this.value = value }

    override fun build(): CatalogKey {
        val namespace = checkNotNull(this.namespace) { "The namespace must be set" }
        val value = checkNotNull(this.value) { "The value must be set" }
        return LanternCatalogKey(namespace, value)
    }

    override fun reset(): CatalogKeyBuilder = apply {
        this.namespace = null
        this.value = null
    }
}
