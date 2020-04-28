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
package org.lanternpowered.api.world

import org.spongepowered.api.world.World
import org.spongepowered.api.world.server.ServerWorld
import kotlin.contracts.contract

typealias BoundedProtoWorldView<P> = org.spongepowered.api.world.BoundedWorldView<P>

/**
 * Gets the bounded sponge world view as a lantern bounded world view.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun BoundedProtoWorldView<out World<*>>.fix(): BoundedWorldView {
    contract {
        returns() implies (this@fix is BoundedWorldView)
    }
    return this as BoundedWorldView
}

/**
 * A bounded world view.
 */
interface BoundedWorldView : BoundedProtoWorldView<ServerWorld>
