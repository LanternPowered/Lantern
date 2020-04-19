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
import org.spongepowered.api.state.StateProperty

/**
 * Represents a state of something.
 */
interface IState<S : State<S>> : State<S> {

    /**
     * The container this state is part of.
     */
    val stateContainer: StateContainer<S>

    /**
     * The internal id (index) of the state.
     */
    val internalId: Int

    /**
     * Gets whether the given [StateProperty] is supported by this state.
     */
    fun supportsStateProperty(stateProperty: StateProperty<*>): Boolean
}
