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
package org.lanternpowered.server.data.key

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.catalog.CatalogKeys.sponge
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.Value

object ValueKeyRegistryModule : AdditionalPluginCatalogRegistryModule<Key<*>>(Keys::class) {

    override fun <A : Key<*>> register(catalogType: A): A {
        return super.register(catalogType).apply {
            if (catalogType is OptionalValueKey<*,*>) {
                register(catalogType.unwrappedKey)
            }
        }
    }

    override fun registerDefaults() {
        register(valueKeyOf<BoundedValue<Double>>(sponge("absorption")) { range(0..Double.MAX_VALUE) })

        val valueTypeParameter = Key::class.java.typeParameters[0]
        for (field in Keys::class.java.fields) {
            val catalogKey = sponge(field.name.toLowerCase())
            // Already registered manually
            if (get(catalogKey).isPresent) {
                continue
            }
            val valueToken = field.genericType.typeToken.resolveType(valueTypeParameter)
            register(valueKeyOf(catalogKey, valueToken.uncheckedCast<TypeToken<Value<Any>>>()))
        }

        for (field in LanternKeys::class.java.fields) {
            register(field.get(null).uncheckedCast())
        }
    }
}
