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
import org.lanternpowered.server.network.packet.PacketProcessor
import org.lanternpowered.server.network.packet.PacketHandler
import kotlin.reflect.KClass

interface PacketRegistryBuilder {

    fun <P : Packet> bind(first: KClass<out P>, vararg more: KClass<out P>): PacketBindingBuilder<P> =
            this.bind(listOf(first) + more.asList())

    fun <P : Packet> bind(types: Iterable<KClass<out P>>): PacketBindingBuilder<P>

    fun <P : Packet> bind(types: KClass<out P>): PacketBindingBuilder<P>

    fun bind(): OpcodeBindingBuilder

    fun <P : Packet> type(first: KClass<out P>, vararg more: KClass<out P>): PacketBindingBuilder<P> =
            this.types(listOf(first) + more.asList())

    fun <P : Packet> types(types: Iterable<KClass<out P>>): PacketBindingBuilder<P>

    fun <P : Packet> types(types: KClass<out P>): PacketBindingBuilder<P>
}

interface OpcodeBindingBuilder : PacketBindingBuilder<Packet> {

    fun <P : Packet> types(first: KClass<out P>, vararg more: KClass<out P>): PacketBindingBuilder<P> =
            this.types(listOf(first) + more.asList())

    fun <P : Packet> types(types: Iterable<KClass<out P>>): PacketBindingBuilder<P>

    fun <P : Packet> type(types: KClass<out P>): PacketBindingBuilder<P>
}

interface PacketBindingBuilder<P : Packet> {

    fun encoder(encoder: PacketEncoder<in P>): PacketBindingBuilder<P>

    fun decoder(decoder: PacketDecoder<out P>): PacketBindingBuilder<P>

    fun processor(processor: PacketProcessor<in P>): PacketBindingBuilder<P>

    fun handler(handler: PacketHandler<in P>)
}
