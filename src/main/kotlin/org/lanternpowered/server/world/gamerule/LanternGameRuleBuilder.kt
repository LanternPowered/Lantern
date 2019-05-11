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
package org.lanternpowered.server.world.gamerule

import com.google.common.reflect.TypeToken
import org.lanternpowered.server.catalog.AbstractNamedCatalogBuilder
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.translation.Translation
import org.spongepowered.api.world.gamerule.GameRule

@Suppress("UNCHECKED_CAST")
class LanternGameRuleBuilder<V : Any> : AbstractNamedCatalogBuilder<GameRule<V>, GameRule.Builder<V>>(), GameRule.Builder<V> {

    private var valueType: TypeToken<V>? = null
    private var defaultValue: V? = null

    override fun <NV : Any?> valueType(valueType: TypeToken<NV>) = apply {
        this.valueType = valueType as TypeToken<V>
    } as GameRule.Builder<NV>

    override fun defaultValue(defaultValue: V) = apply {
        check(this.valueType != null) { "The value type must be set before the default value" }
        this.defaultValue = checkNotNull(defaultValue) { "defaultValue" }
    }

    override fun build(key: CatalogKey, name: Translation): GameRule<V> {
        val valueType = checkNotNull(this.valueType) { "The value type must be set." }
        val defaultValue = checkNotNull(this.defaultValue) { "The default value must be set." }
        return LanternGameRule(key, name, valueType, defaultValue)
    }
}
