/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.api.text.translation

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.toPlain
import org.spongepowered.api.text.translation.locale.Locales
import java.util.Locale

/**
 * Constructs a new [ConstantTextTranslation] from the given [Text].
 */
fun textTranslationOf(text: Text): ConstantTextTranslation = ConstantTextTranslation(text)

/**
 * Represents a [Translation] that can be translated into a
 * [Text] object, which means that it can contain formatting.
 */
interface TextTranslation : Translation {

    /**
     * Gets the default translation [Text] without extra parameters.
     *
     * @return The default translation text
     */
    @JvmDefault
    fun getText(): Text = getText(Locales.DEFAULT)

    /**
     * Gets the translation text without any parameters replaced.
     *
     * @param locale The language to get the translated format string for
     * @return The translation text without any parameters
     */
    fun getText(locale: Locale): Text

    @JvmDefault
    override fun get(): String = getText().toPlain()

    @JvmDefault
    override fun get(locale: Locale): String  = getText(locale).toPlain()
}

/**
 * Represents a [TextTranslation] that holds a constant [Text] object.
 */
data class ConstantTextTranslation(private val text: Text) : TextTranslation {
    override fun get(locale: Locale): String = this.text.toPlain(locale)
    override fun getText(): Text = this.text
    override fun getText(locale: Locale): Text = this.text
    override fun getId(): String = get()
}
