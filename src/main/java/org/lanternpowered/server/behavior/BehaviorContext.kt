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
package org.lanternpowered.server.behavior

import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.block.BlockSnapshotBuilder
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntitySnapshot
import org.spongepowered.api.item.inventory.transaction.SlotTransaction
import java.util.function.BiConsumer
import java.util.function.Function

interface BehaviorContext : CauseStack {
    interface Snapshot

    override fun pushCause(obj: Any): BehaviorContext
    override fun <T : Any> addContext(key: CauseContextKey<T>, value: T): BehaviorContext

    /**
     * Gets the current [CauseStack].
     *
     * @return The cause stack
     */
    val causeStack: CauseStack?

    /**
     * Pushes a [Snapshot].
     *
     * @return The snapshot
     */
    fun pushSnapshot(): Snapshot?

    /**
     * Reverts all the changes made since the [Snapshot].
     */
    fun popSnapshot(snapshot: Snapshot?)

    /**
     * Gets all the [BlockSnapshot]s.
     *
     * @return The block snapshots
     */
    val blockSnapshots: Collection<BlockSnapshot?>?
    fun addEntity(entitySnapshot: EntitySnapshot?)
    fun addEntity(entity: Entity?)

    /**
     * Adds a [SlotTransaction] to the context.
     *
     * @param slotTransaction The slot transaction
     */
    fun addSlotChange(slotTransaction: SlotTransaction?)

    /**
     * Adds a [SlotTransaction]s to the context.
     *
     * @param slotTransactions The slot transactions
     */
    fun addSlotChanges(slotTransactions: Iterable<SlotTransaction?>?)

    /**
     * Gets a [ImmutableList] with all the [SlotTransaction]s.
     *
     * @return The slot transactions
     */
    val slotChanges: List<SlotTransaction?>?

    /**
     * Adds a [BlockSnapshot] to the context.
     *
     * @param blockSnapshot The block snapshot
     * @param force Force the block change, this overrides the current entry at the position without an exception
     * @throws IllegalArgumentException If the location of the block change is in use and force is `false`
     */
    @Throws(IllegalArgumentException::class)
    fun addBlockChange(blockSnapshot: BlockSnapshot?, force: Boolean)

    /**
     * Adds a [BlockSnapshot] to the context.
     *
     * @param blockSnapshot The block snapshot
     * @throws IllegalArgumentException If the location of the block change is in use and force is `false`
     */
    @Throws(IllegalArgumentException::class)
    fun addBlockChange(blockSnapshot: BlockSnapshot?) {
        addBlockChange(blockSnapshot, false)
    }

    fun transformBlockChanges(snapshotTransformer: BiConsumer<BlockSnapshot?, BlockSnapshotBuilder?>?)
    fun transformBlockChangesWithFunction(snapshotTransformer: Function<BlockSnapshot?, BlockSnapshot?>?)

    /**
     * Populates the [BlockSnapshot.Builder] with information
     * that triggered the change. For example the notifier.
     *
     * @param builder The builder to populate
     */
    fun populateBlockSnapshot(builder: BlockSnapshot.Builder, populationFlags: Int)

    fun <B : Behavior> process(pipeline: BehaviorPipeline<B>, function: BehaviorProcessFunction<B>): BehaviorResult

    object PopulationFlags {
        const val CREATOR = 0x01
        const val NOTIFIER = 0x02
        const val CREATOR_AND_NOTIFIER = CREATOR or NOTIFIER
    }
}