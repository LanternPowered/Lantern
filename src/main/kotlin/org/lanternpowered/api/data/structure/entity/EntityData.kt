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
package org.lanternpowered.api.data.structure.entity

import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.data.persistence.dataQueryOf
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.entity.EntityType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataSerializable
import org.spongepowered.math.vector.Vector3d

/**
 * Represents the data of an [Entity].
 */
data class EntityData(
        val type: EntityType<*>,
        val position: Vector3d,
        val data: DataView
) : DataSerializable {

    override fun getContentVersion(): Int = 1

    override fun toContainer(): DataContainer = DataContainer.createNew()
            .set(Queries.Type, this.type.key.formatted)
            .set(Queries.Position, this.position.toArray())
            .set(Queries.Data, this.data)

    object Queries {

        val Type = dataQueryOf("Type")
        val Position = dataQueryOf("Position")
        val Data = dataQueryOf("Data")
    }
}
