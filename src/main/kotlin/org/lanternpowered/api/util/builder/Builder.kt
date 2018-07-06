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
package org.lanternpowered.api.util.builder

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.plugin.PluginCallerSensitive
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation

/**
 * The base class for all the builders.
 */
typealias BaseBuilder<T, B> = org.spongepowered.api.util.ResettableBuilder<T, B>

// TODO: Add the following to SpongeAPI to provide consistent builders across the API.

/**
 * A base builder to construct [CatalogType]s.
 */
interface CatalogBuilder<C : CatalogType, B : CatalogBuilder<C, B>> : BaseBuilder<C, B> {

    /**
     * Sets the name of the catalog type.
     *
     * @param name The name
     * @return This builder, for chaining
     */
    fun name(name: String): B

    /**
     * Sets the id of the catalog type. (without namespace)
     *
     * @param id The id
     * @return This builder, for chaining
     */
    fun id(id: String): B

    /**
     * Builds the [CatalogType] of type [C].
     * <p>The last [PluginContainer] in the cause stack will be used to
     * determine which plugin was used to construct the [C].
     *
     * @return The built catalog type
     */
    @PluginCallerSensitive
    fun build(): C
}

/**
 * A base builder to build catalog typ
 */
interface TranslatableCatalogBuilder<C, B : TranslatableCatalogBuilder<C, B>> : CatalogBuilder<C, B>
        where C : CatalogType, C : Translatable {

    /**
     * Sets the [Translatable] name of the catalog type.
     *
     * @param name The name
     * @return This builder, for chaining
     */
    fun name(name: Translatable): B

    /**
     * Sets the [Translation] name of the catalog type.
     *
     * @param name The name
     * @return This builder, for chaining
     */
    fun name(name: Translation): B
}
