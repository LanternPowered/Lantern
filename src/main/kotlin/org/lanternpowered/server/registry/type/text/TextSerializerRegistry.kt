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
package org.lanternpowered.server.registry.type.text

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.text.serializer.FormattingCodeTextSerializer
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.text.serializer.PlainTextSerializer
import org.lanternpowered.api.text.serializer.TextSerializer

val TextSerializerRegistry = catalogTypeRegistry<TextSerializer> {
    register(PlainTextSerializer)
    register(JsonTextSerializer)
    register(LegacyTextSerializer)
    register(FormattingCodeTextSerializer['&'])
}
