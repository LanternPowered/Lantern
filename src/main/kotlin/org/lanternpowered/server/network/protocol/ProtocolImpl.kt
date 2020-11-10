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

import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.packet.PacketProcessor
import kotlin.reflect.KClass

class ProtocolBuilderImpl : ProtocolBuilder {

    private val inbound = InboundPacketRegistryBuilderImpl()
    private val outbound = OutboundPacketRegistryBuilderImpl()

    override fun inbound(): InboundPacketRegistryBuilder = this.inbound
    override fun outbound(): OutboundPacketRegistryBuilder = this.outbound

    fun build(): Protocol {
        val inbound = this.inbound.build()
        val outbound = this.outbound.build()
        return ProtocolImpl(inbound, outbound)
    }
}

private class ProtocolImpl(
        override val inbound: InboundPacketRegistry,
        override val outbound: OutboundPacketRegistry
) : Protocol

private class InboundPacketRegistryImpl(
        private val byOpcode: Array<InboundPacketRegistry.OpcodeRegistration<*>>,
        private val byType: Map<Class<*>, InboundPacketRegistry.TypeRegistration<*>>
) : InboundPacketRegistry {

    override fun <P : Packet> byOpcode(opcode: Int): InboundPacketRegistry.OpcodeRegistration<P>? =
            if (opcode in this.byOpcode.indices) this.byOpcode[opcode].uncheckedCast() else null

    override fun <P : Packet> byType(type: Class<P>): InboundPacketRegistry.TypeRegistration<P>? =
            this.byType[type].uncheckedCast()
}

private class InboundOpcodeRegistrationImpl<P : Packet>(
        override val opcode: Int,
        override val decoder: PacketDecoder<out P>?
) : InboundPacketRegistry.OpcodeRegistration<P>

private class InboundTypeRegistrationImpl<P : Packet>(
        override val type: Class<P>,
        override val handler: PacketHandler<in P>?,
        override val processors: Collection<PacketProcessor<in P>>
) : InboundPacketRegistry.TypeRegistration<P>

private class InboundPacketRegistryBuilderImpl : InboundPacketRegistryBuilder {

    private val tempTypeRegistrations = HashMap<Class<*>, TempTypeRegistration<*>>()
    private val tempOpcodeRegistrations = ArrayList<TempOpcodeRegistration>()

    override fun bind(): InboundOpcodeBindingBuilder {
        val opcode = this.tempOpcodeRegistrations.size
        val registration = TempOpcodeRegistration(opcode)
        this.tempOpcodeRegistrations.add(registration)
        return object : InboundOpcodeBindingBuilder {
            override fun decoder(decoder: PacketDecoder<*>) {
                registration.decoder = decoder.uncheckedCast()
            }
        }
    }

    override fun <P : Packet> types(types: Iterable<KClass<out P>>): InboundPacketBindingBuilder<P> {
        val classes = types.map { it.java }
        return object : InboundPacketBindingBuilder<P> {
            override fun processor(processor: PacketProcessor<in P>): InboundPacketBindingBuilder<P> = this.apply {
                for (type in classes)
                    addProcessor(type, processor)
            }

            override fun handler(handler: PacketHandler<in P>): InboundPacketBindingBuilder<P> = this.apply {
                for (type in classes)
                    setHandler(type, handler)
            }
        }
    }

    override fun <P : Packet> type(type: KClass<out P>): InboundPacketBindingBuilder<P> = this.types(setOf(type))

    private fun <P : Packet> tempTypeRegistration(type: Class<P>) =
            this.tempTypeRegistrations.computeIfAbsent(type) { TempTypeRegistration(type) }.uncheckedCast<TempTypeRegistration<P>>()

    fun <P : Packet> setHandler(type: Class<P>, handler: PacketHandler<in P>) {
        this.tempTypeRegistration(type).handler = handler
    }

    fun <P : Packet> addProcessor(type: Class<P>, processor: PacketProcessor<in P>) {
        this.tempTypeRegistration(type).processors += processor
    }

    private class TempTypeRegistration<P : Packet>(
            val type: Class<P>,
            var handler: PacketHandler<in P>? = null,
            val processors: MutableCollection<PacketProcessor<in P>> = ArrayList()
    )

    private class TempOpcodeRegistration(
            val opcode: Int,
            var decoder: PacketDecoder<Packet>? = null
    )

    fun build(): InboundPacketRegistry {
        val opcodeRegistrations: Array<InboundPacketRegistry.OpcodeRegistration<*>> = this.tempOpcodeRegistrations
                .map { temp -> InboundOpcodeRegistrationImpl(temp.opcode, temp.decoder) }
                .toTypedArray()
        val typeRegistrations = this.tempTypeRegistrations.values
                .map { temp ->
                    InboundTypeRegistrationImpl<Packet>(temp.type.uncheckedCast(),
                            temp.handler.uncheckedCast(), temp.processors.uncheckedCast())
                }
                .associateBy { it.type }
                .toImmutableMap()
        return InboundPacketRegistryImpl(opcodeRegistrations, typeRegistrations.uncheckedCast())
    }
}

private class OutboundPacketRegistryImpl(
        private val opcodeByType: Map<Class<*>, OutboundPacketRegistry.OpcodeRegistration<*>>,
        private val typeByType: Map<Class<*>, OutboundPacketRegistry.TypeRegistration<*>>
) : OutboundPacketRegistry {

    override fun <P : Packet> opcodeByType(type: Class<P>): OutboundPacketRegistry.OpcodeRegistration<P> =
            this.opcodeByType[type].uncheckedCast()

    override fun <P : Packet> typeByType(type: Class<P>): OutboundPacketRegistry.TypeRegistration<P> =
            this.typeByType[type].uncheckedCast()
}

private class OutboundOpcodeRegistrationImpl<P : Packet>(
        override val opcode: Int,
        override val acceptedTypes: Set<Class<out P>>,
        override val encoder: PacketEncoder<in P>?
) : OutboundPacketRegistry.OpcodeRegistration<P>

private class OutboundTypeRegistrationImpl<P : Packet>(
        override val opcodeRegistration: OutboundPacketRegistry.OpcodeRegistration<P>?,
        override val type: Class<P>,
        override val processors: Collection<PacketProcessor<in P>>
) : OutboundPacketRegistry.TypeRegistration<P>

private class OutboundPacketRegistryBuilderImpl : OutboundPacketRegistryBuilder {

    private val tempTypeRegistrations = HashMap<Class<*>, TempTypeRegistration<*>>()
    private val tempOpcodeRegistrations = ArrayList<TempOpcodeRegistration<*>>()

    override fun bind(): OutboundOpcodeBindingBuilder {
        val opcode = this.tempOpcodeRegistrations.size
        val registration = TempOpcodeRegistration<Packet>(opcode)
        this.tempOpcodeRegistrations.add(registration)
        return object : OutboundOpcodeBindingBuilder {
            override fun <P : Packet> encoder(encoder: PacketEncoder<P>): OutboundOpcodeBindingBuilder.Accepts<P> {
                registration.encoder = encoder.uncheckedCast()
                return object : OutboundOpcodeBindingBuilder.Accepts<P> {
                    override fun acceptAll(types: Iterable<KClass<out P>>): OutboundOpcodeBindingBuilder.Accepts<P> = this.apply {
                        val javaTypes = types.map { it.java }
                        registration.acceptedTypes.addAll(javaTypes)
                    }
                    override fun accept(type: KClass<out P>): OutboundOpcodeBindingBuilder.Accepts<P> = this.acceptAll(setOf(type))
                }
            }
        }
    }

    private fun <P : Packet> tempTypeRegistration(type: Class<P>) =
            this.tempTypeRegistrations.computeIfAbsent(type) { TempTypeRegistration(type) }.uncheckedCast<TempTypeRegistration<P>>()

    override fun <P : Packet> types(types: Iterable<KClass<out P>>): OutboundPacketBindingBuilder<P> {
        val classes = types.map { it.java }
        return object : OutboundPacketBindingBuilder<P> {
            override fun processor(processor: PacketProcessor<in P>): OutboundPacketBindingBuilder<P> = this.apply {
                for (type in classes)
                    tempTypeRegistration(type).processors.uncheckedCast<MutableCollection<PacketProcessor<*>>>() += processor
            }
        }
    }

    override fun <P : Packet> type(type: KClass<out P>): OutboundPacketBindingBuilder<P> = this.types(setOf(type))

    private class TempTypeRegistration<P : Packet>(
            val type: Class<P>,
            val processors: MutableCollection<PacketProcessor<in P>> = ArrayList()
    )

    private class TempOpcodeRegistration<P : Packet>(
            val opcode: Int,
            var encoder: PacketEncoder<in P>? = null,
            var acceptedTypes: MutableCollection<Class<out P>> = HashSet()
    )

    fun build(): OutboundPacketRegistry {
        val opcodeRegistrations = HashMap<Class<*>, OutboundPacketRegistry.OpcodeRegistration<*>>()
        for (temp in this.tempOpcodeRegistrations) {
            val registration = OutboundOpcodeRegistrationImpl(temp.opcode,
                temp.acceptedTypes.toImmutableSet(), temp.encoder.uncheckedCast())
            for (type in temp.acceptedTypes)
                opcodeRegistrations[type] = registration
        }
        val typeRegistrations = this.tempTypeRegistrations
                .mapValues { (_, temp) ->
                    val opcodeRegistration = opcodeRegistrations[temp.type]
                    OutboundTypeRegistrationImpl<Packet>(opcodeRegistration.uncheckedCast(),
                            temp.type.uncheckedCast(), temp.processors.uncheckedCast())
                }
                .toImmutableMap()
        return OutboundPacketRegistryImpl(opcodeRegistrations, typeRegistrations.uncheckedCast())
    }
}
