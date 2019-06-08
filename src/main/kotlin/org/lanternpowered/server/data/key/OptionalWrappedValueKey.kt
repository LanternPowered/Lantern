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

import com.google.common.reflect.TypeParameter
import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ext.*
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.value.OptionalValue
import org.spongepowered.api.data.value.Value
import java.util.Optional

class OptionalWrappedValueKey<V : OptionalValue<E>, E : Any>(
        key: CatalogKey, valueToken: TypeToken<V>, elementToken: TypeToken<Optional<E>>
) : ValueKey<V, Optional<E>>(key, valueToken, elementToken) {

    val unwrappedKey = run {
        val unwrappedElementToken = elementToken.resolveType(optionalElementParameter).uncheckedCast<TypeToken<E>>()
        val unwrappedValueToken = createValueToken(unwrappedElementToken)
        val unwrappedKey = CatalogKey.of(key.namespace, key.value + "_non_optional")

        OptionalUnwrappedValueKey(unwrappedKey, unwrappedValueToken, unwrappedElementToken, this)
    }

    companion object {

        private val optionalElementParameter = Optional::class.java.typeParameters[0]

        private fun <E : Any> createValueToken(elementToken: TypeToken<E>) =
                object : TypeToken<Value<E>>() {}.where(object : TypeParameter<E>() {}, elementToken)
    }
}
