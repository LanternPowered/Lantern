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
package org.lanternpowered.server.util

/**
 * A thread type that is used for the main lantern threads, this means all the
 * world update threads, the initialization and main thread.
 */
class SyncLanternThread : LanternThread {
    constructor(target: Runnable, name: String) : super(target, name)
    constructor(target: Runnable) : super(target)
}
