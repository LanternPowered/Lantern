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

import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.packet.PacketProcessor
import kotlin.reflect.KClass

interface InboundPacketRegistryBuilder {

    fun bind(): InboundOpcodeBindingBuilder

    fun <P : Packet> types(first: KClass<out P>, vararg more: KClass<out P>): InboundPacketBindingBuilder<P> =
            this.types(listOf(first) + more.asList())

    fun <P : Packet> types(types: Iterable<KClass<out P>>): InboundPacketBindingBuilder<P>

    fun <P : Packet> type(type: KClass<out P>): InboundPacketBindingBuilder<P>
}

interface InboundOpcodeBindingBuilder {

    fun decoder(decoder: PacketDecoder<*>)
}

interface InboundPacketBindingBuilder<P : Packet> {

    fun processor(processor: PacketProcessor<in P>): InboundPacketBindingBuilder<P>

    fun handler(handler: PacketHandler<in P>): InboundPacketBindingBuilder<P>
}

interface OutboundPacketRegistryBuilder {

    fun bind(): OutboundOpcodeBindingBuilder

    fun <P : Packet> types(first: KClass<out P>, vararg more: KClass<out P>): OutboundPacketBindingBuilder<P> =
            this.types(listOf(first) + more.asList())

    fun <P : Packet> types(types: Iterable<KClass<out P>>): OutboundPacketBindingBuilder<P>

    fun <P : Packet> type(type: KClass<out P>): OutboundPacketBindingBuilder<P>
}

interface OutboundOpcodeBindingBuilder {

    fun <P : Packet> encoder(encoder: PacketEncoder<P>): Accepts<P>

    interface Accepts<P : Packet> {

        fun acceptAll(first: KClass<out P>, vararg more: KClass<out P>): Accepts<P> =
                this.acceptAll(listOf(first) + more.asList())

        fun acceptAll(types: Iterable<KClass<out P>>): Accepts<P>

        fun accept(type: KClass<out P>): Accepts<P>
    }
}

interface OutboundPacketBindingBuilder<P : Packet> {

    fun processor(processor: PacketProcessor<in P>): OutboundPacketBindingBuilder<P>
}
