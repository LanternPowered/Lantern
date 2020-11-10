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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.data.Keys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.value.PackedAngle
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityTeleportPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPaintingPacket
import org.spongepowered.api.data.type.ArtType
import org.spongepowered.api.data.type.ArtTypes
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3i

class PaintingEntityProtocol<E : LanternEntity>(entity: E) : EntityProtocol<E>(entity) {

    private var lastArt: ArtType? = null
    private var lastDirection: Direction? = null
    private var lastBlockPos: Vector3i? = null

    // We can only support cardinal directions, up and down are also
    // not supported but they will also default to facing south
    private val direction: Direction
        get() {
            val direction = this.entity.get(Keys.DIRECTION).orElse(Direction.SOUTH)
            // We can only support cardinal directions, up and down are also
            // not supported but they will also default to facing south
            return if (!direction.isCardinal) {
                Direction.getClosest(direction.asOffset(), Direction.Division.CARDINAL)
            } else direction
        }

    private val art: ArtType
        get() = this.entity.get(Keys.ART_TYPE).orElseGet(ArtTypes.KEBAB)

    override fun spawn(context: EntityProtocolUpdateContext) {
        this.spawn(context, this.art, this.direction, this.entity.position.toInt())
    }

    private fun spawn(context: EntityProtocolUpdateContext, art: ArtType, direction: Direction, position: Vector3i) {
        context.sendToAll { SpawnPaintingPacket(this.rootEntityId, this.entity.uniqueId, art, position, direction) }
    }

    public override fun update(context: EntityProtocolUpdateContext) {
        val art: ArtType = this.art
        val direction = this.direction
        val pos = this.entity.position
        val blockPos = pos.toInt()
        if (art != this.lastArt || direction != this.lastDirection) {
            this.spawn(context, art, direction, blockPos)
            this.lastDirection = direction
            this.lastBlockPos = blockPos
            this.lastArt = art
        } else if (blockPos != this.lastBlockPos) {
            context.sendToAll { EntityTeleportPacket(this.rootEntityId, pos, PackedAngle.Zero, PackedAngle.Zero, true) }
            this.lastBlockPos = blockPos
        }
    }

    public override fun spawn(parameterList: ParameterList) {}
    public override fun update(parameterList: ParameterList) {}
}
