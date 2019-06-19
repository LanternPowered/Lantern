/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.block

import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.world.WeakWorldReference
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.world.LocatableBlock
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.math.vector.Vector3i

import java.util.Optional
import java.util.UUID

class LanternLocatableBlockBuilder : AbstractDataBuilder<LocatableBlock>(LocatableBlock::class.java, 1), LocatableBlock.Builder {

    var blockState: BlockState? = null
    var position: Vector3i? = null
    var worldReference: WeakWorldReference? = null
    var location: Location? = null

    override fun state(blockState: BlockState) = apply {
        this.blockState = blockState
    }

    override fun location(location: Location) = apply {
        this.blockState = location.block
        this.position = location.blockPosition
        this.worldReference = WeakWorldReference(location.world)
        this.location = location
    }

    override fun position(position: Vector3i) = apply {
        this.position = position
        this.location = null
    }

    override fun position(x: Int, y: Int, z: Int) = apply {
        this.position = Vector3i(x, y, z)
        this.location = null
    }

    override fun world(world: World) = apply {
        this.worldReference = WeakWorldReference(world)
        this.location = null
    }

    override fun from(value: LocatableBlock) = apply {
        val block = value as LanternLocatableBlock
        this.blockState = block.getBlockState()
        val location = block.getLocation()
        this.position = block.getLocation().position.toInt()
        this.worldReference = location.worldIfAvailable.map { WeakWorldReference(it) }.orElse(null)
        this.location = null
    }

    override fun build(): LocatableBlock {
        val position = checkNotNull(this.position) { "position must be set" }
        val worldReference = checkNotNull(this.worldReference) { "world must be set" }
        var blockState: BlockState? = this.blockState
        if (blockState == null) {
            val world = worldReference.world.orElseThrow { IllegalStateException("World is unavailable.") }
            blockState = world.getBlock(position)!!
        }
        val location = this.location ?: worldReference.toLocation(position)
        return LanternLocatableBlock(location, blockState)
    }

    override fun reset() = apply {
        this.worldReference = null
        this.location = null
        this.position = null
        this.blockState = null
    }

    override fun buildContent(container: DataView): Optional<LocatableBlock> {
        val x = container.getInt(Queries.POSITION_X)
                .orElseThrow { InvalidDataException("Could not locate an \"x\" coordinate in the container!") }
        val y = container.getInt(Queries.POSITION_Y)
                .orElseThrow { InvalidDataException("Could not locate an \"y\" coordinate in the container!") }
        val z = container.getInt(Queries.POSITION_Z)
                .orElseThrow { InvalidDataException("Could not locate an \"z\" coordinate in the container!") }
        val blockState = container.getCatalogType(DataQueries.BLOCK_STATE, BlockState::class.java)
                .orElseThrow { InvalidDataException("Could not locate a BlockState") }
        val worldId = container.getObject(Queries.WORLD_ID, UUID::class.java)
                .orElseThrow { InvalidDataException("Could not locate a UUID") }
        return Sponge.getServer().worldManager.getWorld(worldId)
                .map { world ->
                    LanternLocatableBlockBuilder()
                            .position(x, y, z)
                            .world(world)
                            .state(blockState)
                            .build()
                }
    }
}
