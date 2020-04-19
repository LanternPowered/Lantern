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
@file:Suppress("DEPRECATION")

package org.lanternpowered.server.text

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

object LanternTexts {

    @JvmStatic
    fun toLegacy(text: Text): String = TextSerializers.LEGACY_FORMATTING_CODE.get().serialize(text)

    @JvmStatic
    fun fromLegacy(text: String): Text = TextSerializers.LEGACY_FORMATTING_CODE.get().deserialize(text)
}
