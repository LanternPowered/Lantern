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
import org.lanternpowered.api.item.inventory.container.layout.DonkeyContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.lanternpowered.server.network.entity.parameter.MutableParameterList
import org.lanternpowered.server.network.entity.vanilla.EntityNetworkIDs
import org.lanternpowered.server.network.entity.vanilla.EntityParameters
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.DestroyEntitiesPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityMetadataPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenHorseWindowPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

class RootDonkeyContainerLayout(val hasChest: Boolean) : LanternTopBottomContainerLayout<DonkeyContainerLayout>(
        title = TITLE, slotFlags = if (hasChest) ALL_INVENTORY_FLAGS_WITH_CHEST else ALL_INVENTORY_FLAGS
) {

    companion object {

        private val TITLE = translatableTextOf("entity.minecraft.donkey")

        private val EQUIPMENT_INVENTORY_FLAGS = intArrayOf(
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION // Saddle slot
        )

        const val CHEST_WIDTH = 5
        const val CHEST_HEIGHT = 3

        private val CHEST_INVENTORY_FLAGS = IntArray(CHEST_WIDTH * CHEST_HEIGHT) { Flags.REVERSE_SHIFT_INSERTION }

        private val TOP_INVENTORY_FLAGS = EQUIPMENT_INVENTORY_FLAGS + CHEST_INVENTORY_FLAGS

        private val ALL_INVENTORY_FLAGS_WITH_CHEST = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS

        private val ALL_INVENTORY_FLAGS = EQUIPMENT_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS

        private val DONKEY_NETWORK_TYPE = EntityNetworkIDs.REGISTRY.require(minecraftKey("donkey"))
    }

    var entity: Entity? = null

    override fun serverSlotIndexToClient(index: Int): Int {
        if (index == 0)
            return 0
        // Insert client armor slot index
        return index + 1
    }

    override fun clientSlotIndexToServer(index: Int): Int {
        if (index == 0)
            return 0
        // Remove client armor slot index
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
            if (protocolType == EntityProtocolTypes.DONKEY) {
                val entityId = entity.world.entityProtocolManager.getProtocolId(entity)
                return listOf(OpenHorseWindowPacket(data.containerId, TOP_INVENTORY_FLAGS.size + 1, entityId))
            }
        }

        val entityId = Int.MAX_VALUE

        // No entity was found, so create a fake one
        val parameters = MutableParameterList()
        parameters.add(EntityParameters.Base.FLAGS, EntityParameters.Base.Flags.IS_INVISIBLE.toByte())
        parameters.add(EntityParameters.ChestedHorse.HAS_CHEST, this.hasChest)

        val packets = mutableListOf<Packet>()
        packets += SpawnMobPacket(entityId, UUID.randomUUID(), DONKEY_NETWORK_TYPE,
                Vector3d.ZERO, PackedAngle.Zero, PackedAngle.Zero, PackedAngle.Zero, Vector3d.ZERO)
        packets += EntityMetadataPacket(entityId, parameters)
        packets += OpenHorseWindowPacket(data.containerId, TOP_INVENTORY_FLAGS.size + 1, entityId)
        packets += DestroyEntitiesPacket(entityId)
        return packets
    }

    override val top: DonkeyContainerLayout = SubDonkeyContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)
}

private class SubDonkeyContainerLayout(
        offset: Int, size: Int, private val root: RootDonkeyContainerLayout
) : SubContainerLayout(offset, size, root), DonkeyContainerLayout {

    override var entity: Entity?
        get() = this.root.entity
        set(value) { this.root.entity = value }

    override val saddle: ContainerSlot get() = this[0]

    override val chest: GridContainerLayout =
            if (this.root.hasChest) {
                SubGridContainerLayout(1, RootDonkeyContainerLayout.CHEST_WIDTH, RootDonkeyContainerLayout.CHEST_HEIGHT, this.root)
            } else {
                SubGridContainerLayout(1, 0, 0, this.root)
            }
}
