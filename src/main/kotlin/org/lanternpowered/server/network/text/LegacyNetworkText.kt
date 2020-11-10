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
package org.lanternpowered.server.network.text

import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.value.ContextualValueCodec
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.text.DefaultTextRenderer
import org.lanternpowered.server.text.LanternTextRenderer
import org.lanternpowered.server.text.TranslationRegistries
import org.lanternpowered.server.text.Translators

/**
 * A object codec for to serialize [Text] as legacy text.
 */
object LegacyNetworkText : ContextualValueCodec<Text> {

    /**
     * The renderer used to render [Text] components.
     */
    val renderer: LanternTextRenderer<Locale> = DefaultTextRenderer(Translators.GlobalAndMinecraft)

    override fun write(ctx: CodecContext, buf: ByteBuffer, value: Text) {
        val locale = ctx.session.locale
        val legacy = LegacyTextSerializer.serialize(value, locale)
        buf.writeString(legacy)
    }

    override fun read(ctx: CodecContext, buf: ByteBuffer): Text {
        val legacy = buf.readString()
        return LegacyTextSerializer.deserialize(legacy)
    }
}
