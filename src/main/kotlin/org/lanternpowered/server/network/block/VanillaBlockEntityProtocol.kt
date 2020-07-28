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
package org.lanternpowered.server.network.block

import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutBlockEntity
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView

abstract class VanillaBlockEntityProtocol<T : BlockEntity>(blockEntity: T) : BlockEntityProtocol<T>(blockEntity) {

    /**
     * Gets the block entity type that should be used to
     * update the block entity on the client.
     *
     * @return The block entity type
     */
    protected abstract val type: String

    override fun init(context: BlockEntityProtocolUpdateContext) {
        val dataView: DataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
        populateInitData(dataView)
        context.send(PacketPlayOutBlockEntity(this.type, this.blockEntity.serverLocation.blockPosition, dataView))
    }

    override fun update(context: BlockEntityProtocolUpdateContext) {
        val lazyDataView = LazyDataView()
        populateUpdateData(lazyDataView)
        if (lazyDataView.dataView != null)
            context.send(PacketPlayOutBlockEntity(this.type, this.blockEntity.serverLocation.blockPosition, lazyDataView.dataView))
    }

    internal class LazyDataView : () -> DataView {
        var dataView: DataView? = null

        override fun invoke(): DataView {
            if (dataView == null) {
                dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
            }
            return dataView!!
        }
    }

    override fun destroy(context: BlockEntityProtocolUpdateContext) {}

    /**
     * Populates the [DataView] to send to the client.
     *
     * @param dataView The data view
     */
    open fun populateInitData(dataView: DataView) {}

    /**
     * Populates the [DataView] to send to the client.
     *
     * @param dataViewSupplier The data view supplier
     */
    open fun populateUpdateData(dataViewSupplier: () -> DataView) {}
}
