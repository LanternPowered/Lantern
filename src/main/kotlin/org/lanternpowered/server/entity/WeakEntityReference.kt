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
package org.lanternpowered.server.entity

import org.lanternpowered.api.Server
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldManager
import java.lang.ref.WeakReference
import java.util.UUID

/**
 * Represents a weak reference to an [Entity].
 */
class WeakEntityReference {

    private var reference: WeakReference<Entity>? = null

    /**
     * The unique id of the entity of this reference.
     */
    val uniqueId: UUID

    /**
     * Whether this reference points to a player.
     */
    private var isPlayer: Boolean? = null

    /**
     * Creates a new weak world reference.
     *
     * @param entity The entity
     */
    constructor(entity: Entity) {
        this.reference = WeakReference(entity)
        this.uniqueId = entity.uniqueId
        this.isPlayer = entity is Player
    }

    /**
     * Creates a new weak entity reference with the unique id of the entity.
     *
     * @param uniqueId The unique id
     */
    constructor(uniqueId: UUID) {
        this.uniqueId = uniqueId
    }

    /**
     * Gets the entity of this reference, this entity may be
     * `null` if it couldn't be found.
     *
     * @return The entity if present, otherwise `null`
     */
    val entity: Entity? get() {
        val reference = this.reference
        var entity = reference?.get()
        if (entity != null)
            return entity
        val isPlayer = this.isPlayer
        if (isPlayer == true) {
            entity = Server.getPlayer(this.uniqueId).orNull()
        } else {
            if (isPlayer == null) {
                entity = Server.getPlayer(this.uniqueId).orNull()
                this.isPlayer = entity != null
            }
            if (entity == null) {
                entity = WorldManager.worlds.asSequence()
                        .map { world -> world.getEntity(this.uniqueId).orNull() }
                        .firstOrNull()
            }
        }
        if (entity != null) {
            this.reference = WeakReference(entity)
            return entity
        }
        return null
    }

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("uniqueId", this.uniqueId)
            .toString()

    override fun equals(other: Any?): Boolean {
        if (other !is WeakEntityReference)
            return false
        return other.uniqueId == this.uniqueId
    }

    override fun hashCode(): Int = this.uniqueId.hashCode()
}
