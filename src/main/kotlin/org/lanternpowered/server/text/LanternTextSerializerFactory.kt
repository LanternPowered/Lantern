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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.text.serializer.FormattingCodeTextSerializer
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.text.serializer.PlainTextSerializer
import org.lanternpowered.api.text.serializer.TextSerializerFactory
import java.util.concurrent.ConcurrentHashMap

object LanternTextSerializerFactory : TextSerializerFactory {

    private val formattingCodeSerializers = ConcurrentHashMap<Char, FormattingCodeTextSerializer>()

    private const val defaultFormattingCode = '&'
    private val defaultFormattingCodeTextSerializer = LanternFormattingCodeTextSerializer(
            ResourceKey.minecraft("formatting_code"), this.defaultFormattingCode)

    override val json: JsonTextSerializer
        get() = TODO("Not yet implemented")

    override val plain: PlainTextSerializer
        get() = LanternPlainTextSerializer

    override val legacy: LegacyTextSerializer
        get() = LanternLegacyTextSerializer

    override fun formatting(code: Char): FormattingCodeTextSerializer {
        return when (code) {
            LanternFormattingCodes.LEGACY_CODE -> LanternLegacyTextSerializer
            this.defaultFormattingCode -> this.defaultFormattingCodeTextSerializer
            else -> this.formattingCodeSerializers.computeIfAbsent(code) {
                LanternFormattingCodeTextSerializer(ResourceKey.minecraft("formatting_code_$it"), it)
            }
        }
    }
}
