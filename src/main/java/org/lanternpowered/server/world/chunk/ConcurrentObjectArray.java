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
package org.lanternpowered.server.world.chunk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConcurrentObjectArray<O> {

    // The locks we will use to lock each section
    private final StampedLock[] locks;

    // All the chunk sections in a column
    private final O[] objects;

    public ConcurrentObjectArray(O[] objects) {
        this.objects = checkNotNull(objects, "objects");
        this.locks = new StampedLock[objects.length];
        for (int i = 0; i < this.locks.length; i++) {
            this.locks[i] = new StampedLock();
        }
    }

    public O[] getRawObjects() {
        return this.objects;
    }

    /**
     * Sets the chunk section at the index.
     *
     * @param index The index in the column
     * @param object The object
     */
    public void set(int index, O object) {
        final StampedLock lock = this.locks[index];
        final long stamp = lock.writeLock();
        try {
            this.objects[index] = object;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void work(int index, Consumer<O> consumer, boolean write) {
        work(index, consumer, write, false);
    }

    /**
     * Performs work on the object.
     *
     * @param index The index of the object
     * @param consumer The consumer that will be executed
     * @param write Whether you will perform write functions
     * @param forceReadLock whether you want to use the lock without trying without first,
     *                      this should be used when you are almost sure that the normal
     *                      read will fail anyway
     */
    public void work(int index, Consumer<O> consumer, boolean write, boolean forceReadLock) {
        final StampedLock lock = this.locks[index];
        if (write) {
            final long stamp = lock.writeLock();
            try {
                consumer.accept(this.objects[index]);
            } finally {
                lock.unlockWrite(stamp);
            }
        } else {
            long stamp;
            if (!forceReadLock) {
                stamp = lock.tryOptimisticRead();
                if (stamp != 0L) {
                    consumer.accept(this.objects[index]);
                }
                if (lock.validate(stamp)) {
                    return;
                }
            }
            stamp = lock.readLock();
            try {
                consumer.accept(this.objects[index]);
            } finally {
                lock.unlock(stamp);
            }
        }
    }

    public <T> T work(int index, Function<O, T> function, boolean write) {
        return work(index, function, write, false);
    }

    public <T> T work(int index, Function<O, T> function, boolean write, boolean forceReadLock) {
        final StampedLock lock = this.locks[index];
        if (write) {
            final long stamp = lock.writeLock();
            try {
                return function.apply(this.objects[index]);
            } finally {
                lock.unlockWrite(stamp);
            }
        } else {
            long stamp;
            if (!forceReadLock) {
                stamp = lock.tryOptimisticRead();
                final T result = stamp == 0L ? null : function.apply(this.objects[index]);
                if (lock.validate(stamp)) {
                    //noinspection ConstantConditions
                    return result;
                }
            }
            stamp = lock.readLock();
            try {
                return function.apply(this.objects[index]);
            } finally {
                lock.unlock(stamp);
            }
        }
    }

    /**
     * Allows us to perform some work on the section, the function may return
     * a null, this can happen if the section was empty (all air), and may return
     * a new section to be set at the index.
     *
     * @param index the index
     * @param function the section function
     */
    public void work(int index, Function<O, O> function) {
        final StampedLock lock = this.locks[index];
        final long stamp = lock.writeLock();
        try {
            this.objects[index] = function.apply(this.objects[index]);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
