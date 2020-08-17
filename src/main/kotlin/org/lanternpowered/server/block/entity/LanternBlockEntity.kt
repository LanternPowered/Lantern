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
package org.lanternpowered.server.block.entity

import org.lanternpowered.api.world.Location
import org.lanternpowered.server.data.DataHelper
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.LocalKeyRegistry.Companion.of
import org.lanternpowered.server.data.LocalMutableDataHolder
import org.lanternpowered.server.network.block.BlockEntityProtocol
import org.lanternpowered.server.network.block.BlockEntityProtocolType
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.block.entity.BlockEntityArchetype
import org.spongepowered.api.block.entity.BlockEntityType
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.world.LocatableBlock
import kotlin.time.Duration

abstract class LanternBlockEntity protected constructor(
        creationData: BlockEntityCreationData
) : BlockEntity, LocalMutableDataHolder {

    private var blockEntityType: BlockEntityType = creationData.type

    override val keyRegistry: LocalKeyRegistry<out LanternBlockEntity> = of()

    override fun getType(): BlockEntityType = this.blockEntityType
    override fun getLocation(): Location = this._location ?: error("The location isn't available.")
    override fun getBlock(): BlockState = this._block ?: error("The block state isn't available.")
    override fun getLocatableBlock(): LocatableBlock = LocatableBlock.builder().location(this.location).build()
    override fun isValid(): Boolean = this._valid

    @Volatile private var _location: Location? = null
    @Volatile private var _block: BlockState? = null
    @Volatile private var _valid = false

    init {
        this.registerKeys()
    }

    override fun setValid(valid: Boolean) {
        this._valid = valid
    }

    /**
     * Sets the [Location] of this block entity.
     *
     * @param location The location
     */
    fun setLocation(location: Location?) {
        this._location = location
    }

    /**
     * Sets the [BlockState] of this block entity.
     *
     * @param blockState The block state
     */
    fun setBlock(blockState: BlockState) {
        this._block = blockState
    }

    /**
     * The [BlockEntityProtocolType] of this [BlockEntity].
     */
    var protocolType: BlockEntityProtocolType<*>? = null
        set(protocolType) {
            field = protocolType
            if (protocolType != null) {
                @Suppress("UNCHECKED_CAST")
                this.protocol = (protocolType as BlockEntityProtocolType<BlockEntity>).supplier(this)
            } else {
                this.protocol = null
            }
        }

    /**
     * The [BlockEntityProtocol] instance of this [BlockEntity].
     */
    var protocol: BlockEntityProtocol<*>? = null
        private set

    protected open fun registerKeys() {}

    /**
     * Pulses this [LanternBlockEntity].
     */
    open fun update(deltaTime: Duration) {}

    override fun getContentVersion(): Int = 1

    override fun validateRawData(dataView: DataView): Boolean = true // TODO

    override fun setRawData(dataView: DataView) {
        DataHelper.deserializeRawData(dataView, this)
    }

    override fun createArchetype(): BlockEntityArchetype =
            LanternBlockEntityArchetype(LanternBlockEntityArchetype.copy(this))

    override fun toContainer(): DataContainer {
        val dataContainer = DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, this.contentVersion)
                .set(DataQueries.TILE_ENTITY_TYPE, this.type)
                .set(DataQueries.POSITION, this.location.blockPosition)
        DataHelper.serializeRawData(dataContainer, this)
        return dataContainer
    }

    override fun copy(): BlockEntity {
        TODO()
    }
}
