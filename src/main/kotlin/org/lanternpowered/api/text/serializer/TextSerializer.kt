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
package org.lanternpowered.api.text.serializer

import net.kyori.adventure.text.serializer.ComponentSerializer
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.locale.Locales
import org.lanternpowered.api.text.Text

interface TextSerializer : ComponentSerializer<Text, Text, String> {

    override fun serialize(text: Text): String = this.serialize(text, Locales.DEFAULT)

    /**
     * Returns a string representation of the provided [Text] in a format
     * that will be accepted by this [TextSerializer]'s [deserialize] method using
     * the given [Locale].
     *
     * @param text The text to serialize
     * @param locale The locale to use
     * @return The string representation of this text
     */
    fun serialize(text: Text, locale: Locale): String
}
