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

import com.google.gson.JsonElement
import org.lanternpowered.api.registry.factoryOf
import org.lanternpowered.api.text.Text

/**
 * A text serializer for the json format.
 */
interface JsonTextSerializer : TextSerializer {

    /**
     * Serializes a [Text] object into a [JsonElement].
     */
    fun serializeToTree(text: Text): JsonElement

    /**
     * Deserializes a [Text] object from the given [JsonElement].
     */
    fun deserializeFromTree(element: JsonElement): Text

    /**
     * The singleton instance of [JsonTextSerializer].
     */
    companion object : JsonTextSerializer by factoryOf<TextSerializerFactory>().json
}
