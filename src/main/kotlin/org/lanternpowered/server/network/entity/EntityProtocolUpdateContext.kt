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
package org.lanternpowered.server.network.entity

import org.lanternpowered.api.entity.Entity
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.packet.Packet
import java.util.Optional
import java.util.OptionalInt
import java.util.function.Supplier

interface EntityProtocolUpdateContext {

    /**
     * Gets the [LanternEntity] that is assigned to
     * the entity id if present.
     *
     * @param entityId The entity id
     * @return The entity id present
     */
    fun getById(entityId: Int): Optional<LanternEntity>

    /**
     * Gets the entity id that is assigned to the [Entity].
     *
     * @param entity The entity
     * @return The entity id
     */
    fun getId(entity: Entity): OptionalInt

    /**
     * Sends the [Packet] to the owner, will only do something
     * if the owner is a [Player].
     *
     * @param packet The message
     */
    fun sendToSelf(packet: Packet)

    /**
     * Sends the [Packet] to the owner, will only do something
     * if the owner is a [Player].
     *
     * @param messageSupplier The message supplier
     */
    fun sendToSelf(messageSupplier: Supplier<Packet>)

    /**
     * Sends the [Packet] to the owner, will only do something
     * if the owner is a [Player].
     *
     * @param messageSupplier The message supplier
     */
    fun sendToSelf(messageSupplier: () -> Packet)

    /**
     * Sends the [Packet] to all the trackers.
     *
     * @param packet The message
     */
    fun sendToAll(packet: Packet)

    /**
     * Sends the [Packet] to all the trackers.
     *
     * @param message The message
     */
    fun sendToAll(message: Supplier<Packet>)

    /**
     * Sends the [Packet] to all the trackers.
     *
     * @param message The message
     */
    fun sendToAll(message: () -> Packet)

    /**
     * Sends the [Packet] to all the trackers except the owner.
     *
     * @param packet The message
     */
    fun sendToAllExceptSelf(packet: Packet)

    /**
     * Sends the [Packet] to all the trackers except the owner.
     *
     * @param messageSupplier The message supplier
     */
    fun sendToAllExceptSelf(messageSupplier: Supplier<Packet>)

    /**
     * Sends the [Packet] to all the trackers except the owner.
     *
     * @param messageSupplier The message supplier
     */
    fun sendToAllExceptSelf(messageSupplier: () -> Packet)
}
