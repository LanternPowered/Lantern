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

import org.lanternpowered.api.locale.Locales
import org.lanternpowered.api.text.Text
import java.util.Locale

/**
 * Represents a translation that can be formatted by using parameters.
 */
interface ParameterizedTextTranslation : TextTranslation {

    @JvmDefault
    override fun getText(): Text = getText(emptyList())

    @JvmDefault
    override fun getText(locale: Locale): Text = getText(locale, emptyList())

    /**
     * Gets a formatted [Text] object using the given [parameters].
     *
     * @param parameters The parameters
     * @return The formatted text
     */
    @JvmDefault
    fun getText(parameters: List<Text>): Text = getText(Locales.DEFAULT, parameters)

    /**
     * Gets a formatted [Text] object using the given [Locale] and [parameters].
     *
     * @param locale The language to get the translated format string for
     * @param parameters The parameters
     * @return The formatted text
     */
    fun getText(locale: Locale, parameters: List<Text>): Text
}
