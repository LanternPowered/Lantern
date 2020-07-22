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
package org.lanternpowered.server.block

import org.lanternpowered.api.namespace.NamespacedKey
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
        private val key: NamespacedKey,
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
