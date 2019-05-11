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
package org.lanternpowered.api.util

import kotlin.reflect.KClass

class ToStringHelper @JvmOverloads constructor(
        private val className: String,
        private var brackets: Brackets = Brackets.ROUND,
        private var omitNullValues: Boolean = false,
        private var nameValueSeparator: String = "=",
        private var entrySeparator: String = ", ") {

    private var first: Entry? = null
    private var last: Entry? = null

    constructor(clazz: Class<*>): this(clazz.simpleName)
    constructor(clazz: KClass<*>): this(clazz.simpleName ?: clazz.run { java.simpleName })
    constructor(self: Any): this(self::class)
    constructor(): this("")

    operator fun invoke(function: ToStringHelper.() -> Unit): ToStringHelper = apply { function.invoke(this) }

    fun addFirst(name: String, value: Any?): ToStringHelper = addFirstEntry(name, value)
    fun addFirstValue(value: Any?): ToStringHelper = addFirstEntry(null, value)
    fun add(name: String, value: Any?): ToStringHelper = addEntry(name, value)
    fun addValue(value: Any?): ToStringHelper = addEntry(null, value)

    fun omitNullValues(): ToStringHelper = apply { this.omitNullValues = true }
    fun brackets(brackets: Brackets) = apply { this.brackets = brackets }
    fun entrySeparator(entrySeparator: String) = apply { this.entrySeparator = entrySeparator }
    fun nameValueSeparator(nameValueSeparator: String) = apply { this.nameValueSeparator = nameValueSeparator }

    private fun addFirstEntry(name: String?, value: Any?): ToStringHelper = apply {
        val entry = Entry(name, value)
        if (this.first == null) {
            this.first = entry
            this.last = entry
        } else {
            entry.next = this.first
            this.first = entry
        }
    }

    private fun addEntry(name: String?, value: Any?): ToStringHelper = apply {
        val entry = Entry(name, value)
        if (this.first == null) {
            this.first = entry
            this.last = entry
        } else {
            this.last!!.next = entry
            this.last = entry
        }
    }

    override fun toString(): String {
        val builder = StringBuilder(this.className).append(this.brackets.open)
        var entry = this.first
        while (entry != null) {
            if (!this.omitNullValues || entry.value != null) {
                if (entry.name != null) {
                    builder.append(entry.name).append(this.nameValueSeparator)
                }
                builder.append(entry.value)
                if (entry.next != null) {
                    builder.append(this.entrySeparator)
                }
            }
            entry = entry.next
        }
        return builder.append(this.brackets.close).toString()
    }

    enum class Brackets(
            internal val open: Char,
            internal val close: Char) {
        ROUND   ('(', ')'),
        CURLY   ('{', '}'),
        SQUARE  ('[', ']'),
    }

    private data class Entry(val name: String?, val value: Any?, var next: Entry? = null)
}
