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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.data.property.LocalPropertyHolder
import org.lanternpowered.server.data.property.PropertyRegistry
import org.lanternpowered.server.state.AbstractStateContainer
import org.spongepowered.api.block.BlockSoundGroup
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.state.StateProperty
import org.spongepowered.api.text.translation.Translation
import java.util.Optional

/**
 * @property blockEntityProvider The provider for block entities of this block type
 */
class LanternBlockType(
        private val key: CatalogKey,
        private val translation: Translation,
        val blockEntityProvider: (() -> BlockEntity)?,
        stateProperties: Iterable<StateProperty<*>>,
        override val propertyRegistry: PropertyRegistry<out LocalPropertyHolder>
) : AbstractStateContainer<BlockState>(key, stateProperties, ::LanternBlockState), BlockType, LocalPropertyHolder {

    private val defaultSoundGroup: BlockSoundGroup = getProperty(BlockProperties.BLOCK_SOUND_GROUP).orElse(BlockSoundGroups.STONE)

    private var updateRandomly: Boolean = false

    override fun getKey() = this.key
    override fun getTranslation() = this.translation

    override fun getItem(): Optional<ItemType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setUpdateRandomly(updateRandomly: Boolean) {
        this.updateRandomly = updateRandomly
    }

    override fun getSoundGroup() = this.defaultSoundGroup
    override fun doesUpdateRandomly() = this.updateRandomly
}
