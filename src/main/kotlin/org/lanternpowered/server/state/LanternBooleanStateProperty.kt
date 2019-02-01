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
package org.lanternpowered.server.state

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.ImmutableSet
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.value.BaseValue
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.state.BooleanStateProperty
import org.spongepowered.api.util.OptBool
import java.util.Optional

class LanternBooleanStateProperty private constructor(key: CatalogKey, valueKey: Key<out BaseValue<Boolean>>) :
        AbstractStateProperty<Boolean, Boolean>(key, Boolean::class.java, STATES, valueKey), BooleanStateProperty {

    override fun parseValue(value: String): Optional<Boolean> {
        return when (value.toLowerCase()) {
            "true" -> OptBool.TRUE
            "false" -> OptBool.FALSE
            else -> Optional.empty()
        }
    }

    companion object {

        private val STATES = ImmutableSet.of(true, false)

        /**
         * Creates a new boolean trait with the specified name.
         *
         * @param key The catalog key
         * @param valueKey The key that should be attached to the trait
         * @return The boolean trait
         */
        @JvmStatic
        fun of(key: CatalogKey, valueKey: Key<out Value<Boolean>>): BooleanStateProperty {
            return LanternBooleanStateProperty(key, checkNotNull(valueKey, "valueKey"))
        }

        @JvmStatic
        fun minecraft(id: String, valueKey: Key<out Value<Boolean>>): BooleanStateProperty {
            return of(CatalogKey.minecraft(id), valueKey)
        }
    }
}
