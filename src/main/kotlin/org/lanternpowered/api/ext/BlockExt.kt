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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.block.BlockSnapshotBuilder
import org.lanternpowered.api.block.BlockState
import org.lanternpowered.api.block.entity.BlockEntityArchetype
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.World
import org.lanternpowered.api.x.block.XBlockSnapshotBuilder
import java.util.UUID

/**
 * The location.
 */
inline var BlockSnapshotBuilder.location: Location<World>?
    get() = (this as XBlockSnapshotBuilder).location
    set(value) { (this as XBlockSnapshotBuilder).location = value }

/**
 * The block state.
 */
inline var BlockSnapshotBuilder.blockState: BlockState
    get() = (this as XBlockSnapshotBuilder).blockState
    set(value) { (this as XBlockSnapshotBuilder).blockState = value }

/**
 * The [BlockEntityArchetype] that holds extra data. Cannot
 * be modified directly, will be available based
 * on the [blockState].
 */
inline val BlockSnapshotBuilder.blockEntity: BlockEntityArchetype?
    get() = (this as XBlockSnapshotBuilder).blockEntity

/**
 * The creator of the block.
 */
inline var BlockSnapshotBuilder.creator: UUID?
    get() = (this as XBlockSnapshotBuilder).creator
    set(value) { (this as XBlockSnapshotBuilder).creator = value }

/**
 * The notifier of the block.
 */
inline var BlockSnapshotBuilder.notifier: UUID?
    get() = (this as XBlockSnapshotBuilder).notifier
    set(value) { (this as XBlockSnapshotBuilder).notifier = value }

inline fun BlockSnapshotBuilder.location(location: Location<World>) = (this as XBlockSnapshotBuilder).location(location)
