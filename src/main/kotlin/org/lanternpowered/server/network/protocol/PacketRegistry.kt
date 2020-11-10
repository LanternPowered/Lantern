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

/**
 * Represents a registry of inbound packet types.
 */
interface InboundPacketRegistry {

    /**
     * Gets the [OpcodeRegistration] for the given opcode.
     */
    fun <P : Packet> byOpcode(opcode: Int): OpcodeRegistration<P>?

    /**
     * Gets the [TypeRegistration] for the given packet type.
     */
    fun <P : Packet> byType(type: Class<P>): TypeRegistration<P>?

    interface OpcodeRegistration<P : Packet> {

        /**
         * The opcode.
         */
        val opcode: Int

        /**
         * The decoder.
         */
        val decoder: PacketDecoder<out P>?
    }

    interface TypeRegistration<P : Packet> {

        /**
         * The packet type.
         */
        val type: Class<P>

        /**
         * The packet handler.
         */
        val handler: PacketHandler<in P>?

        /**
         * The packet processors.
         */
        val processors: Collection<PacketProcessor<in P>>
    }
}

/**
 * Represents a registry of outbound packet types.
 */
interface OutboundPacketRegistry {

    /**
     * Gets the [OpcodeRegistration] for the given opcode.
     */
    fun <P : Packet> opcodeByType(type: Class<P>): OpcodeRegistration<P>?

    /**
     * Gets the [TypeRegistration] for the given packet type.
     */
    fun <P : Packet> typeByType(type: Class<P>): TypeRegistration<P>?

    interface OpcodeRegistration<P : Packet> {

        /**
         * The opcode.
         */
        val opcode: Int

        /**
         * All the packet types that are accepted by the encoder
         * of this opcode registration.
         */
        val acceptedTypes: Set<Class<out P>>

        /**
         * The encoder.
         */
        val encoder: PacketEncoder<in P>?
    }

    interface TypeRegistration<P : Packet> {

        /**
         * The opcode registration the type is bound to.
         */
        val opcodeRegistration: OpcodeRegistration<P>?

        /**
         * The packet type.
         */
        val type: Class<P>

        /**
         * The packet processors.
         */
        val processors: Collection<PacketProcessor<in P>>
    }
}
