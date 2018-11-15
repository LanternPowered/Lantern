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
package org.lanternpowered.server.cause

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.CauseContextKey
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.CauseStackManagerFrame
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.event.cause.EventContextKey
import java.util.Optional

internal object EmptyCauseStack : SnapshotCauseStack {

    private val obj = Any()
    private val cause by lazy { Cause.of(EventContext.empty(), Lantern.game) }
    private val snapshot = object : SnapshotCauseStack.Snapshot {}
    private val frame = object : CauseStack.Frame {

        override fun close() {}
        override fun getCurrentCause(): Cause = cause
        override fun getCurrentContext() = EventContext.empty()
        override fun pushCause(obj: Any) = this
        override fun popCause(): Any = obj

        override fun <T> addContext(key: EventContextKey<T>, value: T) = this
        override fun <T> removeContext(key: EventContextKey<T>) = Optional.empty<T>()
    }

    override fun restoreSnapshot(snapshot: SnapshotCauseStack.Snapshot) {}
    override fun createSnapshot() = this.snapshot

    override fun getCurrentCause(): Cause = cause
    override fun getCurrentContext(): EventContext = EventContext.empty()

    override fun pushCause(obj: Any): CauseStack = this
    override fun popCause(): Any = obj
    override fun popCauses(n: Int) {}
    override fun peekCause(): Any = obj
    override fun pushCauseFrame() = frame
    override fun popCauseFrame(handle: CauseStackManagerFrame) {}

    override fun <T> first(target: Class<T>) = Optional.empty<T>()
    override fun <T> last(target: Class<T>) = Optional.empty<T>()

    override fun containsType(target: Class<*>) = false
    override fun contains(any: Any) = false

    override fun <T> addContext(key: EventContextKey<T>, value: T) = this
    override fun <T> addContextIfAbsent(key: CauseContextKey<T>, valueProvider: () -> T) = valueProvider()

    override fun <T> getContext(key: EventContextKey<T>) = Optional.empty<T>()
    override fun <T> removeContext(key: EventContextKey<T>) = Optional.empty<T>()
}
