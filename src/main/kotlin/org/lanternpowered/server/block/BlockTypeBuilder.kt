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

import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.BlockType
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.namespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.AABB
import org.lanternpowered.api.util.math.times
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.block.property.FlammableInfo
import org.lanternpowered.server.block.state.BlockStateProperties
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.item.ItemTypeBuilder
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.block.entity.BlockEntityType
import org.spongepowered.api.block.entity.BlockEntityTypes
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.state.StateProperty
import org.spongepowered.math.vector.Vector3d
import java.util.function.Supplier

val testBlockType = blockTypeOf(namespacedKey("namespace", "value")) {
    name("Test Block")
    stateProperty(BlockStateProperties.IS_WET, false)
    keys {
        register(Keys.BLOCK_SOUND_GROUP, BlockSoundGroups.GLASS)
        register(Keys.BLAST_RESISTANCE, 10.2)
    }
    stateKeys {
        registerProvider(LanternKeys.FLAMMABLE_INFO) {
            get {
                if (this.getStateProperty(BlockStateProperties.IS_WET).orElse(false)) {
                    null
                } else {
                    FlammableInfo(1, 1)
                }
            }
            getDirectional {
                FlammableInfo(1, 1)
            }
        }
    }
    blockEntity(BlockEntityTypes.BANNER)
    collisionBox {
        if (getStateProperty(BlockStateProperties.IS_WET).orElse(false)) {
            AABB(Vector3d.ZERO, Vector3d.ONE * 2)
        } else {
            AABB(Vector3d.ZERO, Vector3d.ONE)
        }
    }
    itemType()
    itemType {
        name("Something else")
    }
    behaviors {

    }
}

fun blockTypeOf(key: NamespacedKey, fn: BlockTypeBuilder.() -> Unit): BlockType {
    TODO()
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class BlockTypeBuilderDsl

@BlockTypeBuilderDsl
interface BlockTypeBuilder {

    fun name(name: String)
    fun name(name: Text)

    /**
     * Adds a [StateProperty] with the given default value.
     */
    fun <V : Comparable<V>> stateProperty(stateProperty: StateProperty<V>, defaultValue: V)

    /**
     * Applies a [BlockEntity] to the block type.
     */
    fun blockEntity(blockEntityType: BlockEntityType)

    /**
     * Applies a [BlockEntity] to the block type.
     */
    fun blockEntity(blockEntityType: Supplier<out BlockEntityType>) = this.blockEntity(blockEntityType.get())

    /**
     * Applies the selection bounding box.
     */
    fun selectionBox(selectionBox: AABB?)

    /**
     * Applies the selection bounding box based on the [BlockState].
     */
    fun selectionBox(fn: @BlockTypeBuilderDsl BlockState.() -> AABB)

    /**
     * Applies the collision bounding box.
     */
    fun collisionBox(collisionBox: AABB?)

    /**
     * Applies the collision bounding box based on the [BlockState].
     */
    fun collisionBox(fn: @BlockTypeBuilderDsl BlockState.() -> AABB)

    /**
     * Applies the collision bounding boxes.
     */
    fun collisionBoxes(collisionBox: Collection<AABB>)

    /**
     * Applies the collision bounding boxes based on the [BlockState].
     */
    fun collisionBoxes(fn: @BlockTypeBuilderDsl BlockState.() -> Collection<AABB>)

    /**
     * Applies keys to the [BlockType].
     */
    fun keys(fn: @BlockTypeBuilderDsl LocalKeyRegistry<BlockType>.() -> Unit)

    /**
     * Applies keys to the [BlockState]. Overrides keys provided through [keys].
     */
    fun stateKeys(fn: @BlockTypeBuilderDsl LocalKeyRegistry<BlockState>.() -> Unit)

    /**
     * Applies behaviors to the [BlockType].
     */
    fun behaviors(fn: @BlockTypeBuilderDsl MutableBehaviorPipeline<Behavior>.() -> Unit)

    /**
     * Enables a item type for the block type and allows the item type to be modified.
     */
    fun itemType(fn: @BlockTypeBuilderDsl ItemTypeBuilder.() -> Unit = {})

}
