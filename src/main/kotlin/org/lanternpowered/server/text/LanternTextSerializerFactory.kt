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
import org.lanternpowered.server.game.registry.type.text.TextSerializerRegistryModule
import org.spongepowered.api.text.serializer.FormattingCodeTextSerializer
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.concurrent.ConcurrentHashMap

object LanternTextSerializerFactory : TextSerializers.Factory {

    private val formattingCodeSerializers = ConcurrentHashMap<Char, FormattingCodeTextSerializer>()

    @Suppress("DEPRECATION")
    override fun createFormattingCodeSerializer(legacyChar: Char): FormattingCodeTextSerializer {
        return when (legacyChar) {
            TextConstants.LEGACY_CHAR -> TextSerializers.LEGACY_FORMATTING_CODE.get()
            TextSerializers.FORMATTING_CODE.get().character -> TextSerializers.FORMATTING_CODE.get()
            else -> this.formattingCodeSerializers.computeIfAbsent(legacyChar) {
                val serializer = LanternFormattingCodeTextSerializer(CatalogKey.minecraft("formatting_code_$it"), it)
                TextSerializerRegistryModule.register(serializer)
                serializer
            }
        }
    }
}
