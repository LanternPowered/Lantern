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
package org.lanternpowered.api.text.serializer

typealias SafeTextSerializer = org.spongepowered.api.text.serializer.SafeTextSerializer
typealias TextParseException = org.spongepowered.api.text.serializer.TextParseException
typealias TextSerializer = org.spongepowered.api.text.serializer.TextSerializer
typealias TextSerializers = org.spongepowered.api.text.serializer.TextSerializers

interface TextSerializerFactory {

    val json: JsonTextSerializer

    val plain: PlainTextSerializer
}
