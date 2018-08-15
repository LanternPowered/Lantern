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
package org.lanternpowered.server.text

import com.google.common.collect.ImmutableList
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.format.TextFormat
import java.util.Objects

class LanternLiteralText : LanternText, LiteralText {

    private val content: String

    internal constructor(content: String) {
        this.content = content
    }

    internal constructor(format: TextFormat, children: ImmutableList<Text>, clickAction: ClickAction<*>?,
                         hoverAction: HoverAction<*>?, shiftClickAction: ShiftClickAction<*>?, content: String)
            : super(format, children, clickAction, hoverAction, shiftClickAction) {
        this.content = content
    }

    override fun getContent(): String = this.content
    override fun toBuilder(): Builder =  Builder(this)

    override fun equals(other: Any?): Boolean =
            other === this || (other is LanternLiteralText && super.equals(other) && other.content == this.content)

    override fun hashCode(): Int = Objects.hash(super.hashCode(), this.content)
    override fun toStringHelper() = super.toStringHelper()
            .addFirstValue(this.content)

    class Builder : LanternText.AbstractBuilder<Builder>, LiteralText.Builder {

        private var content: String

        constructor() : this("")

        internal constructor(content: String) { this.content = content }
        internal constructor(text: Text) : super(text) { this.content = "" }
        internal constructor(text: LiteralText) : super(text) { this.content = text.content }

        override fun getContent(): String = this.content
        override fun content(content: String) = apply { this.content = content }

        override fun build(): LiteralText {
            // Special case for empty builder
            if (this.format.isEmpty
                    && this.children.isEmpty()
                    && this.clickAction == null
                    && this.hoverAction == null
                    && this.shiftClickAction == null) {
                if (this.content.isEmpty()) {
                    return EMPTY
                } else if (this.content == NEW_LINE_STRING) {
                    return NEW_LINE
                }
            }

            return LanternLiteralText(this.format, ImmutableList.copyOf(this.children),
                    this.clickAction, this.hoverAction, this.shiftClickAction, this.content)
        }

        override fun equals(other: Any?) = other === this || (other is Builder && super.equals(other) && other.content == this.content)
        override fun hashCode() = Objects.hash(super.hashCode(), this.content)

        override fun toStringHelper() = super.toStringHelper()
                .addFirstValue(this.content)

        override fun from(value: Text): Builder = Builder(value)
        override fun reset() = Builder()
    }

    companion object {

        const val NEW_LINE_CHAR = '\n'
        const val NEW_LINE_STRING = "\n"

        /**
         * The empty, unformatted [Text] instance.
         */
        val EMPTY = LanternLiteralText("")

        /**
         * An unformatted [Text] that will start a new line (if supported).
         */
        val NEW_LINE = LanternLiteralText(NEW_LINE_STRING)
    }
}
