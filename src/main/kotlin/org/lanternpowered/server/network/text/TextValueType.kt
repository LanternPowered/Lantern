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
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.text.DefaultTextRenderer
import org.lanternpowered.server.text.LanternTextRenderer
import org.lanternpowered.server.text.TranslationRegistries

object TextValueType : ContextualValueType<Text> {

    /**
     * The renderer used to render [Text] components.
     */
    val renderer: LanternTextRenderer<Locale> = DefaultTextRenderer(TranslationRegistries.Default)

    override fun write(ctx: CodecContext, value: Text, buf: ByteBuffer) {
        val locale = ctx.session.locale
        val rendered = this.renderer.render(value, locale)
        val json = serialize(rendered)
        buf.writeString(json)
    }

    override fun read(ctx: CodecContext, buf: ByteBuffer): Text {
        val json = buf.readString()
        return JsonTextSerializer.deserialize(json)
    }

    fun serialize(text: Text): String =
            this.fixJson(JsonTextSerializer.serialize(text))

    fun deserialize(json: String): Text =
            JsonTextSerializer.deserialize(json)

    // We need to fix the json format yay, the minecraft client
    // can't handle primitives or arrays as root, just expect
    // things to break, so fix it...
    private fun fixJson(json: String): String {
        var fixedJson = json
        val start = fixedJson.first()
        return if (start == '{') {
            fixedJson
        } else {
            if (start != '[')
                fixedJson = "[$fixedJson]"
            "{\"text\":\"\",\"extra\":$fixedJson}"
        }
    }
}
