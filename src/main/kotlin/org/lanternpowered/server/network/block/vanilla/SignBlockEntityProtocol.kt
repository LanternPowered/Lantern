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
package org.lanternpowered.server.network.block.vanilla

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.server.network.block.VanillaBlockEntityProtocol
import org.spongepowered.api.block.entity.BlockEntity
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView

class SignBlockEntityProtocol<T : BlockEntity>(tile: T) : VanillaBlockEntityProtocol<T>(tile) {

    private var lastLines: List<Text>? = null

    override val type: String
        get() = "minecraft:sign"

    override fun populateInitData(dataView: DataView) {
        addLines(dataView, this.blockEntity.get(Keys.SIGN_LINES).orElse(emptyList()))
    }

    override fun populateUpdateData(dataViewSupplier: () -> DataView) {
        val signLines = this.blockEntity.get(Keys.SIGN_LINES).orElse(emptyList())
        if (this.lastLines != signLines)
            addLines(dataViewSupplier(), signLines)
        this.lastLines = signLines.toList()
    }

    companion object {

        private val lineQueries = arrayOf(
                DataQuery.of("Text1"),
                DataQuery.of("Text2"),
                DataQuery.of("Text3"),
                DataQuery.of("Text4"))

        private fun addLines(view: DataView, lines: List<Text>) {
            // TODO: Make localizable per player
            for (i in lineQueries.indices)
                view[lineQueries[i]] = JsonTextSerializer.serialize(if (i < lines.size) lines[i] else emptyText())
        }
    }
}
