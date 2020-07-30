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

import java.util.concurrent.locks.StampedLock
import java.util.function.Consumer
import java.util.function.Function

/**
 * @property objects All the objects in the array
 */
class ConcurrentObjectArray<V>(
        val objects: Array<V>
) {

    // The locks we will use to lock each section
    private val locks: Array<StampedLock> = Array(this.objects.size) { StampedLock() }

    /**
     * Sets the chunk section at the index.
     *
     * @param index The index in the column
     * @param obj The object to set
     */
    operator fun set(index: Int, obj: V) {
        val lock = this.locks[index]
        val stamp = lock.writeLock()
        try {
            this.objects[index] = obj
        } finally {
            lock.unlockWrite(stamp)
        }
    }

    /**
     * Performs work on the object.
     *
     * @param index The index of the object
     * @param consumer The consumer that will be executed
     * @param write Whether you will perform write functions
     * @param forceReadLock Whether you want to use the lock without trying without first,
     *                      this should be used when you are almost sure that the normal
     *                      read will fail anyway
     */
    @JvmOverloads
    fun work(index: Int, consumer: Consumer<V>, write: Boolean, forceReadLock: Boolean = false) {
        val lock = locks[index]
        if (write) {
            val stamp = lock.writeLock()
            try {
                consumer.accept(this.objects[index])
            } finally {
                lock.unlockWrite(stamp)
            }
        } else {
            var stamp: Long
            if (!forceReadLock) {
                stamp = lock.tryOptimisticRead()
                if (stamp != 0L) {
                    consumer.accept(this.objects[index])
                }
                if (lock.validate(stamp)) {
                    return
                }
            }
            stamp = lock.readLock()
            try {
                consumer.accept(this.objects[index])
            } finally {
                lock.unlock(stamp)
            }
        }
    }

    fun <T> work(index: Int, function: Function<V, T>, write: Boolean): T? {
        return work(index, function, write, false)
    }

    fun <T> work(index: Int, function: Function<V, T>, write: Boolean, forceReadLock: Boolean): T? {
        val lock = this.locks[index]
        return if (write) {
            val stamp = lock.writeLock()
            try {
                function.apply(this.objects[index])
            } finally {
                lock.unlockWrite(stamp)
            }
        } else {
            var stamp: Long
            if (!forceReadLock) {
                stamp = lock.tryOptimisticRead()
                val result = if (stamp == 0L) null else function.apply(this.objects[index])
                if (lock.validate(stamp)) {
                    return result
                }
            }
            stamp = lock.readLock()
            try {
                function.apply(this.objects[index])
            } finally {
                lock.unlock(stamp)
            }
        }
    }

    /**
     * Allows us to perform some work on the section, the function may return
     * a null, this can happen if the section was empty (all air), and may return
     * a new section to be set at the index.
     *
     * @param index The index
     * @param function The section function
     */
    fun work(index: Int, function: Function<V, V>) {
        val lock = this.locks[index]
        val stamp = lock.writeLock()
        try {
            this.objects[index] = function.apply(this.objects[index])
        } finally {
            lock.unlockWrite(stamp)
        }
    }
}
