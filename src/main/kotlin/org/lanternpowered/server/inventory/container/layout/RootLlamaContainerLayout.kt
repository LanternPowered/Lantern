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

import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.LlamaContainerLayout
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
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

class RootLlamaContainerLayout(val chestColumns: Int) : LanternTopBottomContainerLayout<LlamaContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS[chestColumns]
) {

    companion object {

        private val TITLE = translatableTextOf("entity.minecraft.llama")

        private val EQUIPMENT_INVENTORY_FLAGS = intArrayOf(
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION // Carpet slot
        )

        private const val MIN_CHEST_WIDTH = 0
        private const val MAX_CHEST_WIDTH = 5

        const val CHEST_HEIGHT = 3

        private val TOP_INVENTORY_FLAGS = Array(MAX_CHEST_WIDTH - MIN_CHEST_WIDTH + 1) { index ->
            EQUIPMENT_INVENTORY_FLAGS + IntArray(index * CHEST_HEIGHT) { Flags.REVERSE_SHIFT_INSERTION }
        }

        private val ALL_INVENTORY_FLAGS = Array(TOP_INVENTORY_FLAGS.size) { index ->
            TOP_INVENTORY_FLAGS[index] + MAIN_INVENTORY_FLAGS
        }

        private val LLAMA_NETWORK_TYPE = EntityNetworkIDs.REGISTRY.require(minecraftKey("llama"))

        private const val UPDATE_CHEST_SIZE = 0x1
    }

    var entity: Entity? = null

    override fun serverSlotIndexToClient(index: Int): Int {
        // Insert client saddle slot index
        return index + 1
    }

    override fun clientSlotIndexToServer(index: Int): Int {
        // Remove client saddle slot index
        return index - 1
    }

    // TODO: Changes to the entity must be tracked, because the entity
    //  may not even be visible to the given player at a given time,
    //  in that case we need to make it visible.

    override fun createOpenPackets(data: ContainerData): List<Packet> {
        val entity = this.entity
        if (entity != null) {
            entity as LanternEntity
            val protocolType = entity.protocolType
            if (protocolType == EntityProtocolTypes.LLAMA) {
                val entityId = entity.world.entityProtocolManager.getProtocolId(entity)
                return listOf(OpenHorseWindowPacket(data.containerId, TOP_INVENTORY_FLAGS.size + 1, entityId))
            }
        }

        val entityId = Int.MAX_VALUE

        // No entity was found, so create a fake one
        val parameters = DefaultParameterList()
        parameters.add(EntityParameters.Base.FLAGS, EntityParameters.Base.Flags.IS_INVISIBLE.toByte())
        parameters.add(EntityParameters.Llama.STRENGTH, this.chestColumns)

        val packets = mutableListOf<Packet>()
        packets += SpawnMobPacket(entityId, UUID.randomUUID(), LLAMA_NETWORK_TYPE,
                Vector3d.ZERO, 0, 0, 0, Vector3d.ZERO)
        packets += EntityMetadataPacket(entityId, parameters)
        packets += OpenHorseWindowPacket(data.containerId, TOP_INVENTORY_FLAGS.size + 1, entityId)
        packets += DestroyEntitiesPacket(entityId)
        return packets
    }

    override val top: LlamaContainerLayout = SubLlamaContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)
}

private class SubLlamaContainerLayout(
        offset: Int, size: Int, private val root: RootLlamaContainerLayout
) : SubContainerLayout(offset, size, root), LlamaContainerLayout {

    override var entity: Entity?
        get() = this.root.entity
        set(value) { this.root.entity = value }

    override val carpet: ContainerSlot get() = this[0]

    override val chest: GridContainerLayout =
            if (this.root.chestColumns != 0) {
                SubGridContainerLayout(1, this.root.chestColumns, RootDonkeyContainerLayout.CHEST_HEIGHT, this.root)
            } else {
                SubGridContainerLayout(1, 0, 0, this.root)
            }
}
