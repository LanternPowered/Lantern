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
package org.lanternpowered.server.state

import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateContainer

/**
 * Represents the builder of a [State].
 */
interface StateBuilder<S : State<S>> {

    /**
     * Is called when all the states of the
     * [StateContainer] are initialized.
     */
    fun whenCompleted(fn: () -> Unit)
}
