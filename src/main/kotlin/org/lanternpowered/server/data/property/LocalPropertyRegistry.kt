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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.server.data.property

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.data.LocalKeyRegistry
import org.spongepowered.api.data.property.PropertyHolder

abstract class LocalPropertyRegistry<H : PropertyHolder> : PropertyRegistry<H> {

    /**
     * Gets this [LocalPropertyRegistry] as a registry which targets the given [PropertyHolder] type.
     *
     * @param holderType The data holder type
     * @return This local property registry, for the given holder type
     */
    abstract fun <H : PropertyHolder> forHolder(holderType: Class<H>): LocalPropertyRegistry<H>

    /**
     * Gets this [LocalKeyRegistry] as a registry which targets the given [PropertyHolder] type [H].
     *
     * @return This local property, for the given holder type
     */
    inline fun <H : PropertyHolder> forHolderUnchecked() = uncheckedCast<LocalPropertyRegistry<H>>()

    /**
     * Gets this [LocalKeyRegistry] as a registry which targets the given [PropertyHolder] type [H].
     *
     * @return This local property registry, for the given holder type
     */
    inline fun <reified H : PropertyHolder> forHolder() = forHolder(H::class.java)

    /**
     * A convenient alternative for the [apply] function on this collection. Applied to the specified holder type.
     */
    inline fun <reified H : PropertyHolder> forHolder(fn: LocalPropertyRegistry<H>.() -> Unit) = forHolder<H>().apply(fn)
}
