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
package org.lanternpowered.server.network.buffer.contextual

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.text.translation.TranslationContext

internal class TextContextualValueType : ContextualValueType<Text> {

    override fun write(ctx: CodecContext, text: Text, buf: ByteBuffer) {
        TranslationContext.enter()
                .locale(ctx.session.locale)
                .enableForcedTranslations().use {
                    buf.writeString(fixJson(JsonTextSerializer.serialize(text)))
                }
    }

    override fun read(ctx: CodecContext, buf: ByteBuffer): Text {
        TranslationContext.enter()
                .locale(ctx.session.locale)
                .enableForcedTranslations().use {
                    return JsonTextSerializer.deserialize(buf.readString())
                }
    }

    // We need to fix the json format yay, the minecraft client
    // can't handle primitives or arrays as root, just expect
    // things to break, so fix it...
    private fun fixJson(json: String): String {
        var fixedJson = json
        val start = fixedJson.first()
        return if (start == '{') {
            fixedJson
        } else {
            if (start != '[') {
                fixedJson = "[$fixedJson]"
            }
            "{\"text\":\"\",\"extra\":$fixedJson}"
        }
    }
}
