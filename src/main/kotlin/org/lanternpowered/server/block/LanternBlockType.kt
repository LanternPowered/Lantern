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

import net.kyori.adventure.text.Component
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.data.LocalImmutableDataHolder
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.state.AbstractStateContainer
import org.spongepowered.api.block.BlockSoundGroup
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.block.entity.BlockEntityType
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.state.StateProperty
import java.util.Optional

/**
 * @property blockEntityType The type for block entities of this block type, or null if there's no block entity
 */
class LanternBlockType(
        private val key: NamespacedKey,
        private val name: Text,
        val blockEntityType: BlockEntityType?,
        stateProperties: Iterable<StateProperty<*>>,
        override val keyRegistry: LocalKeyRegistry<BlockType>
) : AbstractStateContainer<BlockState>(key, stateProperties, ::LanternBlockState), BlockType, LocalImmutableDataHolder<BlockType> {

    val isAir: Boolean = this.key.formatted.contains("air")

    private val defaultSoundGroup: BlockSoundGroup = this.get(LanternKeys.BLOCK_SOUND_GROUP).orElse(BlockSoundGroups.STONE)

    private var updateRandomly: Boolean = false

    override fun getKey() = this.key
    override fun asComponent(): Component = this.name

    override fun setUpdateRandomly(updateRandomly: Boolean) {
        this.updateRandomly = updateRandomly
    }

    override fun getSoundGroup() = this.defaultSoundGroup
    override fun doesUpdateRandomly() = this.updateRandomly

    override fun getItem(): Optional<ItemType> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val pipeline: BehaviorPipeline<Behavior>
        get() = TODO()
}
