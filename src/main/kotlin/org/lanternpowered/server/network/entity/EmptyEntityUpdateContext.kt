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

import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.entity.Entity
import java.util.Optional
import java.util.OptionalInt
import java.util.function.Supplier

object EmptyEntityUpdateContext : EntityProtocolUpdateContext {
    override fun getById(entityId: Int): Optional<LanternEntity> = Optional.empty()
    override fun getId(entity: Entity): OptionalInt = OptionalInt.empty()
    override fun sendToSelf(packet: Packet) {}
    override fun sendToSelf(messageSupplier: Supplier<Packet>) {}
    override fun sendToSelf(messageSupplier: () -> Packet) {}
    override fun sendToAll(packet: Packet) {}
    override fun sendToAll(message: Supplier<Packet>) {}
    override fun sendToAll(message: () -> Packet) {}
    override fun sendToAllExceptSelf(packet: Packet) {}
    override fun sendToAllExceptSelf(messageSupplier: Supplier<Packet>) {}
}
