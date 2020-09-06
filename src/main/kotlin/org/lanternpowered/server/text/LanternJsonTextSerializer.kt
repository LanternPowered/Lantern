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

import com.google.gson.JsonElement
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.server.util.gson.fromJson

object LanternJsonTextSerializer : JsonTextSerializer {

    private val serializer = GsonComponentSerializer.gson()

    override fun serializeToTree(text: Text): JsonElement =
            this.serializer.serializer().toJsonTree(text)

    override fun deserializeFromTree(element: JsonElement): Text =
            this.serializer.serializer().fromJson(element)

    override fun serialize(text: Text, locale: Locale): String =
            this.serializer.serialize(text)

    override fun deserialize(input: String): Text =
            this.serializer.deserialize(input)
}
