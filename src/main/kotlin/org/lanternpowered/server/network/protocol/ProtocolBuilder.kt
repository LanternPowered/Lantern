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

    fun inbound(): InboundPacketRegistryBuilder

    fun inbound(block: InboundPacketRegistryBuilder.() -> Unit) = this.inbound().run(block)

    fun outbound(): OutboundPacketRegistryBuilder

    fun outbound(block: OutboundPacketRegistryBuilder.() -> Unit) = this.outbound().run(block)
}
