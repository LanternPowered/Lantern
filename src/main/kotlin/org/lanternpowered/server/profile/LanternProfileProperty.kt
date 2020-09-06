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
package org.lanternpowered.server.profile

import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.profile.property.ProfileProperty
import java.util.Optional

data class LanternProfileProperty(
        private val name: String,
        private val value: String,
        private val signature: String?
) : ProfileProperty {

    override fun getName(): String = this.name
    override fun getValue(): String = this.value
    override fun getSignature(): Optional<String> = this.signature.asOptional()
    override fun getContentVersion(): Int = 1

    override fun toContainer(): DataContainer {
        val dataContainer = DataContainer.createNew()
                .set(LanternGameProfile.NAME, this.name)
                .set(LanternGameProfile.VALUE, this.value)
        if (this.signature != null)
            dataContainer[LanternGameProfile.SIGNATURE] = this.signature
        return dataContainer
    }

    companion object {

        /**
         * Creates [LanternProfileProperty] from the specified [JsonObject].
         *
         * @param jsonObject The json object
         * @return The profile property
         */
        fun createFromJson(jsonObject: JsonObject): LanternProfileProperty {
            val name = jsonObject["name"].asString
            val value = jsonObject["value"].asString
            val signature = jsonObject["signature"]?.asString
            return LanternProfileProperty(name, value, signature)
        }

        /**
         * Creates a multimap with [LanternProfileProperty]s from the specified [JsonArray].
         *
         * @param jsonArray The json array
         * @return The multimap
         */
        fun createPropertiesMapFromJson(jsonArray: JsonArray): Multimap<String, ProfileProperty> {
            val properties = LinkedHashMultimap.create<String, ProfileProperty>()
            for (i in 0 until jsonArray.size()) {
                val profileProperty = this.createFromJson(jsonArray[i].asJsonObject)
                properties.put(profileProperty.name, profileProperty)
            }
            return properties
        }
    }
}
