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
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf

typealias FixedTranslation = org.spongepowered.api.text.translation.FixedTranslation
typealias Translation = org.spongepowered.api.text.translation.Translation
typealias Translatable = org.spongepowered.api.text.translation.Translatable

/**
 * Converts the translation to a [Text] with the given parameters.
 */
fun Translation.toText(vararg parameters: Any): Text {
    return when (this) {
        is ParameterizedTextTranslation -> getText(parameters.asList().map { parameter -> parameter.toText() })
        is TextTranslation -> getText()
        else -> translatableTextOf(this, *parameters)
    }
}

private fun Any.toText(): Text = if (this is TextRepresentable) toText() else Text.of(this)
