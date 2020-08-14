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

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.lanternpowered.api.text.serializer.FormattingCodeTextSerializer
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.text.serializer.PlainTextSerializer
import org.lanternpowered.api.text.serializer.TextSerializerFactory
import java.util.concurrent.ConcurrentHashMap

object LanternTextSerializerFactory : TextSerializerFactory {

    private val formattingCodeSerializers = ConcurrentHashMap<Char, FormattingCodeTextSerializer>()

    override val json: JsonTextSerializer
        get() = LanternJsonTextSerializer

    override val plain: PlainTextSerializer
        get() = LanternPlainTextSerializer

    override val legacy: LegacyTextSerializer
        get() = LanternLegacyTextSerializer

    override fun formatting(code: Char): FormattingCodeTextSerializer {
        return when (code) {
            LegacyComponentSerializer.SECTION_CHAR -> LanternLegacyTextSerializer
            else -> this.formattingCodeSerializers.computeIfAbsent(code, ::LanternFormattingCodeTextSerializer)
        }
    }
}
