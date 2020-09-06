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

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.contentToString
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.profile.property.ProfileProperty
import java.util.ArrayList
import java.util.Objects
import java.util.Optional
import java.util.UUID

class LanternGameProfile private constructor(
        private val uniqueId: UUID,
        private val properties: Multimap<String, ProfileProperty>,
        private var name: String?
) : GameProfile {

    constructor(uniqueId: UUID, name: String?, properties: Multimap<String, ProfileProperty>) :
            this(uniqueId, HashMultimap.create(properties), name)

    constructor(uniqueId: UUID, name: String?) :
            this(uniqueId, HashMultimap.create(), name)

    fun setName(name: String) {
        this.name = name
    }

    override fun getName(): Optional<String> = this.name.asOptional()
    override fun getPropertyMap(): Multimap<String, ProfileProperty> = this.properties
    override fun getUniqueId(): UUID = this.uniqueId
    override fun isFilled(): Boolean = this.name != null
    override fun getContentVersion(): Int = 1

    override fun toContainer(): DataContainer {
        val container = DataContainer.createNew()
                .set(UNIQUE_ID, this.uniqueId.toString())
        if (this.name != null)
            container[NAME] = this.name
        if (this.properties.isEmpty)
            return container
        val propertiesView = container.createView(PROPERTIES)
        for (key in this.properties.keySet()) {
            val entries = ArrayList<DataContainer>()
            for (property in this.properties[key]) {
                val entry = DataContainer.createNew()
                        .set(VALUE, property.value)
                val signature = property.signature.orNull()
                if (signature != null)
                    entry[SIGNATURE] = signature
                entries.add(entry)
            }
            propertiesView[DataQuery.of(key)] = entries
        }
        return container
    }

    override fun hashCode(): Int = Objects.hash(this.uniqueId, this.name)

    override fun equals(other: Any?): Boolean = other is LanternGameProfile &&
            other.uniqueId == this.uniqueId && other.name == this.name

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("uniqueId", this.uniqueId)
            .add("name", this.name)
            .add("properties", if (this.properties.isEmpty) null else this.properties.values().contentToString())
            .toString()

    companion object {

        val NAME: DataQuery = DataQuery.of("Name")
        val UNIQUE_ID: DataQuery = DataQuery.of("UniqueId")
        val PROPERTIES: DataQuery = DataQuery.of("Properties")
        val VALUE: DataQuery = DataQuery.of("Value")
        val SIGNATURE: DataQuery = DataQuery.of("Signature")
    }
}
