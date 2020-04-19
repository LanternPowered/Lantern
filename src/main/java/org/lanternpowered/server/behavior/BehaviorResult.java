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
package org.lanternpowered.server.behavior;

public enum BehaviorResult {
    /**
     * The block interaction was a success and the result
     * should be returned directly.
     */
    SUCCESS,
    /**
     * Continue to the next {@link Behavior} in the context and
     * keep all the stored changes.
     */
    CONTINUE,
    /**
     * The current {@link Behavior} failed failed. The pipeline
     * handling will be interrupted.
     */
    FAIL,
    /**
     * The current {@link Behavior} failed and silently
     * move to the next behavior, discarding all the current changes.
     */
    PASS,
    ;

    public boolean isSuccess() {
        return this == SUCCESS || this == CONTINUE;
    }
}
