/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.concurrent.locks.StampedLock;

public class IdAllocator {

    public static final int INVALID_ID = -1;

    private int idCounter = 0;

    private final IntSet reusableIds = new IntOpenHashSet();
    private final IntIterator reusableIdsIterator = this.reusableIds.iterator();

    private final StampedLock lock = new StampedLock();

    /**
     * Gets whether the specified id is allocated.
     *
     * @param id The id to check
     * @return Whether the id is allocated
     */
    public boolean isAllocated(int id) {
        long stamp = this.lock.tryOptimisticRead();
        boolean allocated = stamp != 0L && this.isAllocated0(id);
        if (stamp == 0L || !this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            try {
                allocated = this.isAllocated0(id);
            } finally {
                this.lock.unlockRead(stamp);
            }
        }
        return allocated;
    }

    private boolean isAllocated0(int id) {
        return id < this.idCounter && !this.reusableIds.contains(id);
    }

    /**
     * Acquires the next free id.
     *
     * @return The id
     */
    public int acquire() {
        final long stamp = this.lock.writeLock();
        try {
            return this.acquire0();
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public int[] acquire(int count) {
        return this.acquire(new int[count]);
    }

    public int[] acquire(int[] array) {
        checkNotNull(array, "array");
        final long stamp = this.lock.writeLock();
        try {
            for (int i = 0; i < array.length; i++) {
                array[i] = this.acquire0();
            }
        } finally {
            this.lock.unlockWrite(stamp);
        }
        return array;
    }

    private int acquire0() {
        if (this.reusableIdsIterator.hasNext()) {
            try {
                return this.reusableIdsIterator.nextInt();
            } finally {
                this.reusableIdsIterator.remove();
            }
        }
        return this.idCounter++;
    }

    /**
     * Releases the id so that it can be reused.
     *
     * @param id The id
     */
    public void release(int id) {
        if (id != INVALID_ID) {
            final long stamp = this.lock.writeLock();
            try {
                this.reusableIds.add(id);
            } finally {
                this.lock.unlockWrite(stamp);
            }
        }
    }
}
