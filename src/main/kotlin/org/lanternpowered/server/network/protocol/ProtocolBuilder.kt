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
package org.lanternpowered.server.network.protocol

fun protocol(block: ProtocolBuilder.() -> Unit): Protocol {
    TODO()
}

interface ProtocolBuilder {

    fun inbound(): PacketRegistryBuilder

    fun inbound(block: PacketRegistryBuilder.() -> Unit) = this.inbound().run(block)

    fun outbound(): PacketRegistryBuilder

    fun outbound(block: PacketRegistryBuilder.() -> Unit) = this.outbound().run(block)
}
