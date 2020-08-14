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

import net.kyori.adventure.text.serializer.ComponentSerializer
import org.lanternpowered.api.Lantern
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.serializer.TextSerializer
import org.spongepowered.api.util.locale.Locales
import java.util.Locale

open class LanternLiteralTextSerializer(
        private val serializer: ComponentSerializer<Text, out Text, String>
) : TextSerializer {

    private val renderer = LiteralTextRenderer(TranslationRegistries.All)

    override fun serialize(text: Text): String =
            this.serialize(text, Locales.DEFAULT)

    override fun serialize(text: Text, locale: Locale): String {
        val rendered = this.renderer.render(text,
                FormattedTextRenderContext(locale, scoreboard = Lantern.server.serverScoreboard.get()))
        return this.serializer.serialize(rendered)
    }

    override fun deserialize(input: String): Text = this.serializer.deserialize(input)
}
