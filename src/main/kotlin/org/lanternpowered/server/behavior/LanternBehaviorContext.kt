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
package org.lanternpowered.server.behavior

import org.lanternpowered.api.behavior.BehaviorCollection
import org.lanternpowered.api.behavior.BehaviorContext
import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.server.cause.SnapshotCauseStack

class LanternBehaviorContext(
        override val behaviorCollection: BehaviorCollection,
        private val causeStack: SnapshotCauseStack = CauseStack.current() as SnapshotCauseStack,
        private val finalizers: MutableList<() -> Unit> = ArrayList()
) : BehaviorContext, CauseStack by causeStack {

    private var lastCauseStackSnapshot: SnapshotCauseStack.Snapshot? = null

    private var cachedSnapshot: BehaviorContextSnapshot? = null
    private var cachedFinalizers: MutableList<() -> Unit>? = null

    override fun addFinalizer(fn: () -> Unit) {
        this.finalizers.add(fn)
        this.cachedFinalizers = null
        this.cachedSnapshot = null
    }

    override fun pushCause(obj: Any) = apply { this.causeStack.pushCause(obj) }
    override fun <T> addContext(key: CauseContextKey<T>, value: T) = apply { this.causeStack.addContext(key, value) }

    private class BehaviorContextSnapshot(
            val causeStackSnapshot: SnapshotCauseStack.Snapshot,
            val finalizers: MutableList<() -> Unit>
    ) : BehaviorContext.Snapshot

    override fun createSnapshot(): BehaviorContext.Snapshot {
        // No need to cache cause stack snapshots, that is handled by the underlying cause stack
        val causeStackSnapshot = this.causeStack.createSnapshot()
        // The cached snapshot doesn't get invalidated when the cause stack changes, so we do that here
        if (this.lastCauseStackSnapshot === causeStackSnapshot) {
            this.cachedSnapshot?.let { return it }
        } else {
            this.lastCauseStackSnapshot = causeStackSnapshot
        }
        val finalizers = this.cachedFinalizers ?: run { ArrayList(this.finalizers).also { this.cachedFinalizers = it } }
        return BehaviorContextSnapshot(causeStackSnapshot, finalizers).also { this.cachedSnapshot = it }
    }

    override fun restoreSnapshot(snapshot: BehaviorContext.Snapshot) {
        if (this.cachedSnapshot === snapshot) return
        snapshot as BehaviorContextSnapshot
        this.cachedSnapshot = snapshot
        this.lastCauseStackSnapshot = snapshot.causeStackSnapshot
        this.causeStack.restoreSnapshot(snapshot.causeStackSnapshot)
        if (this.cachedFinalizers !== snapshot.finalizers) {
            this.finalizers.clear()
            this.finalizers.addAll(snapshot.finalizers)
            this.cachedFinalizers = snapshot.finalizers
        }
    }
}
