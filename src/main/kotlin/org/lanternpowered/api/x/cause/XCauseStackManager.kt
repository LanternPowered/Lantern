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
package org.lanternpowered.api.x.cause

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.CauseStackManager

/**
 * An extended [CauseStackManager].
 */
interface XCauseStackManager : CauseStackManager {

    /**
     * Get the current active cause stack, or null if none.
     */
    fun currentStackOrNull(): CauseStack?

    /**
     * Get the current active cause stack, or empty if none.
     */
    fun currentStackOrEmpty(): CauseStack

    /**
     * Gets the current active cause stack, or throws
     * a [IllegalStateException] if none.
     */
    @Throws(IllegalStateException::class)
    fun currentStack(): CauseStack
}
