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
package org.lanternpowered.server.text

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializer
import java.util.Locale

interface LanternTextSerializer : TextSerializer {

    /**
     * Serializes the [Text] using the given [Locale].
     *
     * @param text The text
     * @param locale The locale
     * @return The serialized string
     */
    fun serialize(text: Text, locale: Locale): String
}
