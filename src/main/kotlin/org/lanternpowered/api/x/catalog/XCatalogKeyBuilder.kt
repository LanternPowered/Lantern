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
package org.lanternpowered.api.x.catalog

import org.lanternpowered.api.catalog.CatalogKeyBuilder
import org.spongepowered.api.plugin.PluginContainer

interface XCatalogKeyBuilder : CatalogKeyBuilder {

    /**
     * Sets the name, this is the readable version
     * of the catalog key value.
     * <p>Setting the value will reset the name, this means
     * that the name must be set after the value.
     *
     * @param name The name
     * @return This builder, for chaining
     */
    fun name(name: String): XCatalogKeyBuilder

    override fun namespace(container: PluginContainer): XCatalogKeyBuilder
    override fun namespace(namespace: String): XCatalogKeyBuilder
    override fun value(value: String): XCatalogKeyBuilder
    override fun build(): XCatalogKey
}
