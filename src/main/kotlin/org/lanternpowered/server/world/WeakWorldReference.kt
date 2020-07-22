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
package org.lanternpowered.server.world

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.api.world.fix
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i
import java.lang.ref.WeakReference
import org.spongepowered.api.world.World as SpongeWorld

/**
 * Represents a weak reference to a [World].
 */
class WeakWorldReference {

    private var reference: WeakReference<World>? = null

    /**
     * The unique id of the world of this reference.
     */
    val key: NamespacedKey

    /**
     * Creates a new weak world reference.
     *
     * @param world The world
     */
    constructor(world: SpongeWorld<*>) {
        world.fix()
        this.reference = WeakReference(world)
        this.key = world.key
    }

    /**
     * Creates a new weak world reference with the key of the world.
     *
     * @param key The key
     */
    constructor(key: NamespacedKey) {
        this.key = key
    }

    /**
     * Gets the world of this reference, this world may be
     * `null` if it couldn't be found.
     *
     * @return The world if present, otherwise `null`
     */
    val world: World? get() {
        val reference = this.reference
        var world = reference?.get()
        if (world != null)
            return world
        world = WorldManager.getWorld(this.key).orNull()
        if (world != null) {
            this.reference = WeakReference(world)
            return world
        }
        return null
    }

    fun toLocation(position: Vector3i): Location =
            this.world?.let { world -> Location.of(world, position) } ?: Location.of(this.key, position)

    fun toLocation(position: Vector3d): Location =
            this.world?.let { world -> Location.of(world, position) } ?: Location.of(this.key, position)

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("key", this.key)
            .toString()

    override fun equals(other: Any?): Boolean {
        if (other !is WeakWorldReference)
            return false
        return other.key == this.key
    }

    override fun hashCode(): Int = this.key.hashCode()
}
