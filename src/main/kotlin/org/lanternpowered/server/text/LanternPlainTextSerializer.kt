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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.serializer.PlainTextSerializer
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.TranslationContext
import java.util.Locale

object LanternPlainTextSerializer : DefaultCatalogType(CatalogKey.minecraft("plain")), PlainTextSerializer {
    override fun serialize(text: Text): String = serialize(text, TranslationContext.current().locale)
    override fun serialize(text: Text, locale: Locale): String = LegacyTexts.toLegacy(locale, text, 0.toChar())
    override fun deserialize(input: String): Text = Text.of(input)
}
