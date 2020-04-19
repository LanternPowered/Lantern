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
package org.lanternpowered.server.effect.entity

import org.lanternpowered.server.entity.LanternEntity
import org.spongepowered.api.entity.Entity

private val emptyEntityEffect = entityEffect {}

/**
 * Gets an empty entity effect.
 */
fun emptyEntityEffect(): EntityEffect = emptyEntityEffect

/**
 * Constructs a new [EntityEffect].
 */
inline fun entityEffect(crossinline block: (entity: LanternEntity) -> Unit): EntityEffect {
    return object : EntityEffect {
        override fun play(entity: LanternEntity) {
            block(entity)
        }
    }
}

/**
 * Plays this [EntityEffect] for the given [Entity].
 *
 * @param entity The entity
 */
fun EntityEffect.play(entity: Entity) = play(entity as LanternEntity)

/**
 * Represents an effect of an entity.
 */
interface EntityEffect {

    /**
     * Plays this [EntityEffect] for the given [LanternEntity].
     *
     * @param entity The entity
     */
    fun play(entity: LanternEntity)
}
