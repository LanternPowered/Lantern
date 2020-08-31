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
package org.lanternpowered.server.inventory.container.layout

import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.HorseContainerLayout
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.lanternpowered.server.network.entity.parameter.DefaultParameterList
import org.lanternpowered.server.network.entity.vanilla.EntityNetworkIDs
import org.lanternpowered.server.network.entity.vanilla.EntityParameters
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityMetadataPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenHorseWindowPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket
import org.spongepowered.api.entity.Entity
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

class RootHorseContainerLayout : LanternTopBottomContainerLayout<HorseContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("entity.minecraft.horse")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION, // Saddle slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION  // Armor slot
        )

        private val ALL_INVENTORY_FLAGS = MAIN_INVENTORY_FLAGS + TOP_INVENTORY_FLAGS

        private val HORSE_NETWORK_TYPE = EntityNetworkIDs.REGISTRY.require(minecraftKey("horse"))
    }

    // TODO: Changes to the entity must be tracked, because the entity
    //  may not even be visible to the given player at a given time,
    //  in that case we need to make it visible.

    var entity: Entity? = null

    override fun createOpenPackets(data: ContainerData): List<Packet> {
        val entity = this.entity
        if (entity != null) {
            entity as LanternEntity
            val protocolType = entity.protocolType
            if (protocolType == EntityProtocolTypes.HORSE) {
                val entityId = entity.world.entityProtocolManager.getProtocolId(entity)
                return listOf(OpenHorseWindowPacket(data.containerId, TOP_INVENTORY_FLAGS.size, entityId))
            }
        }

        val packets = mutableListOf<Packet>()
        val entityId = Int.MAX_VALUE

        // No entity was found, so create a fake one
        val parameters = DefaultParameterList()
        parameters.add(EntityParameters.Base.FLAGS, EntityParameters.Base.Flags.IS_INVISIBLE.toByte())

        packets += SpawnMobPacket(entityId, UUID.randomUUID(), HORSE_NETWORK_TYPE,
                Vector3d.ZERO, 0, 0, 0, Vector3d.ZERO)
        packets += EntityMetadataPacket(entityId, parameters)
        packets += OpenHorseWindowPacket(data.containerId, TOP_INVENTORY_FLAGS.size, entityId)
        packets += DestroyEntitiesPacket(entityId)

        return packets
    }

    override val top: HorseContainerLayout = SubHorseContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)
}

private class SubHorseContainerLayout(
        offset: Int, size: Int, private val root: RootHorseContainerLayout
) : SubContainerLayout(offset, size, root), HorseContainerLayout {

    override var entity: Entity?
        get() = this.root.entity
        set(value) { this.root.entity = value }

    override val saddle: ContainerSlot get() = this[0]
    override val armor: ContainerSlot get() = this[1]
}
