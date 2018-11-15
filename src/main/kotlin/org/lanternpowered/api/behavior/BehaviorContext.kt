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
@file:Suppress("DeprecatedCallableAddReplaceWith")

package org.lanternpowered.api.behavior

import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack

interface BehaviorContext : CauseStack {

    /**
     * The [BehaviorCollection] for which this [BehaviorContext]
     * was constructed to process [Behavior]s.
     *
     * This collection can be used to
     * trigger new behavior pipelines.
     */
    val behaviorCollection: BehaviorCollection

    /**
     * Adds a finalizer task that will be executed when
     * the whole pipeline is processed. Finalizers may
     * also be reverted by [Snapshot]s.
     */
    fun addFinalizer(fn: () -> Unit)

    // Snapshots

    /**
     * Represents a snapshot of the [BehaviorContext]. The snapshot
     * also contains the state of the [CauseStack].
     */
    interface Snapshot

    /**
     * Reverts this [CauseStack] to the given [Snapshot].
     *
     * @param snapshot The snapshot to revert to
     */
    fun restoreSnapshot(snapshot: Snapshot)

    /**
     * Creates a [Snapshot] of the current state of the [BehaviorContext].
     *
     * @return The snapshot
     */
    fun createSnapshot(): Snapshot

    // Cause related

    override fun pushCause(obj: Any): BehaviorContext
    override fun <T> addContext(key: CauseContextKey<T>, value: T): BehaviorContext

    /*
    /**
     * Gets all the [BlockSnapshot]s.
     *
     * @return The block snapshots
     */
    val blockSnapshots: Collection<BlockSnapshot>

    /**
     * Gets a [ImmutableList] with all the [SlotTransaction]s.
     *
     * @return The slot transactions
     */
    val slotChanges: List<SlotTransaction>

    fun addEntity(entitySnapshot: EntitySnapshot)

    fun addEntity(entity: Entity)

    /**
     * Adds a [SlotTransaction] to the context.
     *
     * @param slotTransaction The slot transaction
     */
    fun addSlotChange(slotTransaction: SlotTransaction)

    /**
     * Adds a [SlotTransaction]s to the context.
     *
     * @param slotTransactions The slot transactions
     */
    fun addSlotChanges(slotTransactions: Iterable<SlotTransaction>)

    /**
     * Adds a [BlockSnapshot] to the context.
     *
     * @param blockSnapshot The block snapshot
     * @param force Force the block change, this overrides the current entry at the position without an exception
     * @throws IllegalArgumentException If the location of the block change is in use and force is `false`
     */
    @Throws(IllegalArgumentException::class)
    fun addBlockChange(blockSnapshot: BlockSnapshot, force: Boolean)

    /**
     * Adds a [BlockSnapshot] to the context.
     *
     * @param blockSnapshot The block snapshot
     * @throws IllegalArgumentException If the location of the block change is in use and force is `false`
     */
    @Throws(IllegalArgumentException::class)
    fun addBlockChange(blockSnapshot: BlockSnapshot) {
        addBlockChange(blockSnapshot, false)
    }

    fun transformBlockChanges(snapshotTransformer: BiConsumer<BlockSnapshot, BlockSnapshotBuilder>)

    fun transformBlockChangesWithFunction(snapshotTransformer: Function<BlockSnapshot, BlockSnapshot>)

    /**
     * Populates the [BlockSnapshot.Builder] with information
     * that triggered the change. For example the notifier.
     *
     * @param builder The builder to populate
     */
    fun populateBlockSnapshot(builder: BlockSnapshot.Builder, populationFlags: Int)*/
}
