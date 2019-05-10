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

import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.x.catalog.XCatalogKey
import org.lanternpowered.api.x.catalog.XCatalogKeyBuilder

class LanternCatalogKeyBuilder : XCatalogKeyBuilder {

    private var namespace: String? = null
    private var value: String? = null
    private var name: String? = null

    override fun name(name: String): XCatalogKeyBuilder = apply { this.name = name }
    override fun namespace(namespace: String): XCatalogKeyBuilder = apply { this.namespace = namespace }
    override fun namespace(container: PluginContainer): XCatalogKeyBuilder = namespace(container.id)

    override fun value(value: String): XCatalogKeyBuilder = apply {
        this.value = value
        this.name = null
    }

    override fun build(): XCatalogKey {
        val namespace = checkNotNull(this.namespace) { "The namespace must be set" }
        val value = checkNotNull(this.value) { "The value must be set" }
        val name = this.name
        // There is no need to create a named key if the objects are equal
        if (name !== null && value !== name) {
            return NamedCatalogKey(namespace, value, name)
        }
        return LanternCatalogKey(namespace, value)
    }

    override fun reset(): XCatalogKeyBuilder = apply {
        this.namespace = null
        this.value = null
        this.name = null
    }
}
