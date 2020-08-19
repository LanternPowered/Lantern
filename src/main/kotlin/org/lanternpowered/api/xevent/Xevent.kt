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
package org.lanternpowered.api.xevent

import org.lanternpowered.api.cause.Cause

/**
 * Represents a event that can be posted on a [XeventBus].
 *
 * These events should be fast to update internal component
 * states, the creation of unnecessary objects like
 * [Cause] in most cases should be avoided.
 */
interface Xevent
