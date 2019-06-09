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

/**
 * A helper class to build [String]s for
 * classes with properties, etc.
 *
 * @param className The name of the class
 * @param brackets The brackets that should be added around the joined entries
 * @param omitNullValues Whether null values should be omitted
 * @param nameValueSeparator The separator that is used to join a key with its value
 * @param entrySeparator The separator that is used to join multiple key-value pairs
 */
class ToStringHelper @JvmOverloads constructor(
        private val className: String = "",
        private var brackets: Brackets = Brackets.ROUND,
        private var omitNullValues: Boolean = false,
        private var nameValueSeparator: String = "=",
        private var entrySeparator: String = ", "
) {

    private var first: Entry? = null
    private var last: Entry? = null

    /**
     * Constructs a new [ToStringHelper] with the
     * simple name of the given [Class].
     */
    constructor(clazz: Class<*>): this(clazz.simpleName)

    /**
     * Constructs a new [ToStringHelper] with the
     * simple name of the given [KClass].
     */
    constructor(clazz: KClass<*>): this(clazz.simpleName ?: clazz.run { java.simpleName })

    /**
     * Constructs a new [ToStringHelper] with the
     * simple name of the given object.
     */
    constructor(self: Any): this(self::class)

    /**
     * Applies changes to this [ToStringHelper].
     */
    operator fun invoke(function: ToStringHelper.() -> Unit) = apply { function.invoke(this) }

    /**
     * Adds a key-value pair at the first position (first is earlier).
     *
     * @param key The key to add
     * @param value The value to add
     * @return This helper, for chaining
     */
    fun addFirst(key: String, value: Any?): ToStringHelper = addFirstEntry(key, value)

    /**
     * Adds a value without a key at the first position (first is earlier).
     *
     * @param value The value to add
     * @return This helper, for chaining
     */
    fun addFirstValue(value: Any?): ToStringHelper = addFirstEntry(null, value)

    /**
     * Adds a key-value pair.
     *
     * @param key The key to add
     * @param value The value to add
     * @return This helper, for chaining
     */
    fun add(key: String, value: Any?): ToStringHelper = addEntry(key, value)

    /**
     * Adds a value without a key.
     *
     * @param value The value to add
     * @return This helper, for chaining
     */
    fun addValue(value: Any?) = addEntry(null, value)

    /**
     * Sets whether null values should be omitted.
     *
     * @return This helper, for chaining
     */
    fun omitNullValues() = apply { this.omitNullValues = true }

    /**
     * Sets the brackets that should be added around the joined entries.
     *
     * @param brackets The brackets
     * @return This helper, for chaining
     */
    fun brackets(brackets: Brackets) = apply { this.brackets = brackets }

    /**
     * Sets the separator that is used to join multiple key-value pairs
     *
     * @param entrySeparator The entry separator
     * @return This helper, for chaining
     */
    fun entrySeparator(entrySeparator: String) = apply { this.entrySeparator = entrySeparator }

    /**
     * Sets the separator that is used to join a key with its value.
     *
     * @param nameValueSeparator The name-value separator
     * @return This helper, for chaining
     */
    fun nameValueSeparator(nameValueSeparator: String) = apply { this.nameValueSeparator = nameValueSeparator }

    private fun addFirstEntry(key: String?, value: Any?) = apply {
        val entry = Entry(key, value)
        if (this.first == null) {
            this.first = entry
            this.last = entry
        } else {
            entry.next = this.first
            this.first = entry
        }
    }

    private fun addEntry(key: String?, value: Any?) = apply {
        val entry = Entry(key, value)
        if (this.first == null) {
            this.first = entry
            this.last = entry
        } else {
            this.last!!.next = entry
            this.last = entry
        }
    }

    /**
     * Builds the [String].
     *
     * @return The built string
     */
    override fun toString(): String {
        val builder = StringBuilder(this.className).append(this.brackets.open)
        var entry = this.first
        while (entry != null) {
            if (!this.omitNullValues || entry.value != null) {
                if (entry.key != null) {
                    builder.append(entry.key).append(this.nameValueSeparator)
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

    /**
     * The different kind of brackets.
     */
    enum class Brackets(
            internal val open: Char,
            internal val close: Char) {
        ROUND   ('(', ')'),
        CURLY   ('{', '}'),
        SQUARE  ('[', ']'),
    }

    private data class Entry(val key: String?, val value: Any?, var next: Entry? = null)
}
