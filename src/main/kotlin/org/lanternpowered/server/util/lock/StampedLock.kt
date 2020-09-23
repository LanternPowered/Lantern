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
package org.lanternpowered.server.util.lock

import org.lanternpowered.api.util.uncheckedCast
import java.util.concurrent.locks.StampedLock

/**
 * Performs a [read] function using the target [StampedLock].
 */
inline fun <R> StampedLock.read(read: () -> R): R {
    var stamp = this.tryOptimisticRead()
    var result: R? = null
    if (stamp != 0L)
        result = read()
    if (stamp == 0L || !this.validate(stamp)) {
        stamp = this.readLock()
        try {
            result = read()
        } finally {
            this.unlockRead(stamp)
        }
    }
    return result.uncheckedCast()
}

/**
 * Performs a [write] function using the target [StampedLock].
 */
inline fun <R> StampedLock.write(write: () -> R): R {
    val stamp = this.writeLock()
    try {
        return write()
    } finally {
        this.unlockWrite(stamp)
    }
}

/**
 * Performs a [read] function using the target [StampedLock]. If the result was
 * `null`, the [compute] function will be used to determine and write the value.
 */
inline fun <R : Any> StampedLock.computeIfNull(read: () -> R?, compute: () -> R): R =
        this.computeIf(read, { it == null}, compute).uncheckedCast()

/**
 * Performs a [read] function using the target [StampedLock]. If the result was
 * `null`, the [compute] function will be used to determine and write the value.
 */
inline fun <R> StampedLock.computeIf(read: () -> R, shouldCompute: (R) -> Boolean, compute: () -> R): R {
    var stamp = this.tryOptimisticRead()
    if (stamp != 0L) {
        val result = read()
        if (this.validate(stamp)) {
            if (!shouldCompute(result))
                return result
            stamp = this.writeLock()
            try {
                return compute()
            } finally {
                this.unlockWrite(stamp)
            }
        }
    }
    stamp = this.readLock()
    try {
        val result = read()
        if (!shouldCompute(result))
            return result
        stamp = this.tryConvertToWriteLock(stamp)
        if (stamp == 0L)
            stamp = this.writeLock()
        return compute()
    } finally {
        this.unlock(stamp)
    }
}
