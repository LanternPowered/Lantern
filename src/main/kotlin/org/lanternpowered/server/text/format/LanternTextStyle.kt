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
package org.lanternpowered.server.text.format

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.TextBuilder
import org.lanternpowered.api.text.format.TextStyle
import org.lanternpowered.api.text.format.TextStyleBase
import org.lanternpowered.api.x.text.format.XTextStyle
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.util.ToStringHelper
import java.util.Objects

open class LanternTextStyle(
        private val bold: Boolean? = null,
        private val italic: Boolean? = null,
        private val underline: Boolean? = null,
        private val strikethrough: Boolean? = null,
        private val obfuscated: Boolean? = null
) : XTextStyle {
    // Return true by default as the TextStyle class is composite by default
    override fun isComposite() = true

    override fun isEmpty() =
            this.bold == null && this.italic == null && this.underline == null && this.strikethrough == null && this.obfuscated == null

    override fun bold(bold: Boolean?) = LanternTextStyle(bold, this.italic, this.underline, this.strikethrough, this.obfuscated)
    override fun italic(italic: Boolean?) = LanternTextStyle(this.bold, italic, this.underline, this.strikethrough, this.obfuscated)
    override fun underline(underline: Boolean?) = LanternTextStyle(this.bold, this.italic, underline, this.strikethrough, this.obfuscated)
    override fun strikethrough(strikethrough: Boolean?) = LanternTextStyle(this.bold, this.italic, this.underline, strikethrough, this.obfuscated)
    override fun obfuscated(obfuscated: Boolean?) = LanternTextStyle(this.bold, this.italic, this.underline, this.strikethrough, obfuscated)

    override fun isBold() = this.bold.optional()
    override fun isItalic() = this.italic.optional()
    override fun hasUnderline() = this.underline.optional()
    override fun hasStrikethrough() = this.strikethrough.optional()
    override fun isObfuscated() = this.obfuscated.optional()

    /**
     * Utility method to check if the given "super-property" contains the given "sub-property".
     *
     * @param superProperty The super property
     * @param subProperty The sub property
     * @return True if the property is contained, otherwise false
     */
    private fun containsProperty(superProperty: Boolean?, subProperty: Boolean?) = subProperty == null || superProperty == subProperty

    override fun contains(vararg styles: TextStyle): Boolean {
        for (style in styles) {
            style as LanternTextStyle
            if (!containsProperty(this.bold, style.bold)
                    || !containsProperty(this.italic, style.italic)
                    || !containsProperty(this.underline, style.underline)
                    || !containsProperty(this.strikethrough, style.strikethrough)
                    || !containsProperty(this.obfuscated, style.obfuscated)) {
                return false
            }
        }
        return true
    }

    /**
     * Utility method to negate a property if it is not null.
     *
     * @param prop The property to negate
     * @return The negated property, or null
     */
    private fun negateProperty(prop: Boolean?) = if (prop == null) null else !prop

    // Do a negation of each property
    override fun negate() = LanternTextStyle(
            negateProperty(this.bold),
            negateProperty(this.italic),
            negateProperty(this.underline),
            negateProperty(this.strikethrough),
            negateProperty(this.obfuscated))

    override fun and(vararg styles: TextStyle): XTextStyle = and(styles.asList())
    override fun and(styles: Collection<TextStyle>): XTextStyle = compose(styles, false) as XTextStyle

    override fun andNot(vararg styles: TextStyle): TextStyle = andNot(styles.asList())
    override fun andNot(styles: Collection<TextStyle>): XTextStyle = compose(styles, true) as XTextStyle

    /**
     * Utility method to perform a compose operation between two properties.
     *
     * @param first The first property
     * @param second The second property
     * @return The composition of the two properties
     */
    private fun composeProperty(first: Boolean?, second: Boolean?): Boolean? {
        return when {
            first == null -> second
            second == null -> first
            first != second -> null
            else -> first
        }
    }

    private fun compose(styles: Collection<TextStyle>, negate: Boolean): TextStyle {
        if (styles.isEmpty()) {
            return this
        } else if (this.isEmpty && styles.size == 1) {
            val style = styles.iterator().next()
            return if (negate) style.negate() else style
        }

        var bold = this.bold
        var italic = this.italic
        var underline = this.underline
        var strikethrough = this.strikethrough
        var obfuscated = this.obfuscated

        if (negate) {
            for (style in styles) {
                style as LanternTextStyle

                bold = composeProperty(bold, negateProperty(style.bold))
                italic = composeProperty(italic, negateProperty(style.italic))
                underline = composeProperty(underline, negateProperty(style.underline))
                strikethrough = composeProperty(strikethrough, negateProperty(style.strikethrough))
                obfuscated = composeProperty(obfuscated, negateProperty(style.obfuscated))
            }
        } else {
            for (style in styles) {
                style as LanternTextStyle

                bold = composeProperty(bold, style.bold)
                italic = composeProperty(italic, style.italic)
                underline = composeProperty(underline, style.underline)
                strikethrough = composeProperty(strikethrough, style.strikethrough)
                obfuscated = composeProperty(obfuscated, style.obfuscated)
            }
        }

        return LanternTextStyle(bold, italic, underline, strikethrough, obfuscated)
    }

    override fun applyTo(builder: TextBuilder) { builder.style(this) }

    override fun equals(other: Any?): Boolean {
        return other === this || (other is LanternTextStyle &&
                this.bold == other.bold &&
                this.italic == other.italic &&
                this.underline == other.underline &&
                this.strikethrough == other.strikethrough &&
                this.obfuscated == other.obfuscated)
    }

    override fun hashCode() = Objects.hash(this.bold, this.italic, this.underline, this.obfuscated, this.strikethrough)
    override fun toString() = ToStringHelper(this::class)
                .omitNullValues()
                .add("bold", this.bold)
                .add("italic", this.italic)
                .add("underline", this.underline)
                .add("strikethrough", this.strikethrough)
                .add("obfuscated", this.obfuscated)
                .toString()

    class Real(key: CatalogKey, override val code: Char, bold: Boolean? = null, italic: Boolean? = null, underline: Boolean? = null,
               strikethrough: Boolean? = null, obfuscated: Boolean? = null) :
            LanternTextStyle(bold, italic, underline, strikethrough, obfuscated), TextStyleBase,
            CatalogType by DefaultCatalogType(key), FormattingCodeHolder {

        override fun isComposite() = false
    }

    class None(key: CatalogKey) : LanternTextStyle(), TextStyleBase, CatalogType by DefaultCatalogType(key) {

        override fun isComposite() = false
    }
}
