/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

import javax.annotation.Nullable;

public final class TrackerIdAllocator {

    private static final int INVALID_INDEX = -1;

    private final List<UUID> uniqueIdsByIndex = new ArrayList<>();
    private final Object2IntMap<UUID> uniqueIds = new Object2IntOpenHashMap<>();
    private final StampedLock lock = new StampedLock();

    {
        this.uniqueIds.defaultReturnValue(INVALID_INDEX);
    }

    public Optional<UUID> get(int trackingId) {
        if (trackingId < 0) {
            return Optional.empty();
        }
        long stamp = this.lock.tryOptimisticRead();
        UUID uniqueId = null;
        if (stamp != 0L) {
            uniqueId = this.getUniqueIdFromIndex(trackingId);
        }
        if (stamp == 0L || !this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            try {
                uniqueId = this.getUniqueIdFromIndex(trackingId);
            } finally {
                this.lock.unlockRead(stamp);
            }
        }
        return Optional.ofNullable(uniqueId);
    }

    @Nullable
    private UUID getUniqueIdFromIndex(int trackingId) {
        return trackingId > this.uniqueIdsByIndex.size() ? null : this.uniqueIdsByIndex.get(trackingId);
    }

    /**
     * Gets the index for the specified {@link UUID}.
     *
     * @param uniqueId The unique id
     * @return The tracking id
     */
    public int get(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        long stamp = this.lock.tryOptimisticRead();
        int index = stamp == 0L ? 0 : this.uniqueIds.get(uniqueId);
        // Optimistic read failed, now just
        // acquire a read lock.
        if (stamp == 0L || !this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            index = this.uniqueIds.get(uniqueId);
            // Check if the index is valid
            if (index == INVALID_INDEX) {
                // Try to convert the read lock to a write lock
                long stamp1 = this.lock.tryConvertToWriteLock(stamp);
                // Could not convert the lock, release the old
                // lock and acquire the write lock
                if (stamp1 == 0L) {
                    this.lock.unlockRead(stamp);
                    stamp1 = this.lock.writeLock();
                }
                try {
                    // Get the next free index
                    return this.getNewIndex(uniqueId);
                } finally {
                    // Release the write lock
                    this.lock.unlockWrite(stamp1);
                }
            } else {
                // Release the read lock
                this.lock.unlockRead(stamp);
            }
        // Check if the index is valid
        } else if (index == INVALID_INDEX) {
            // Acquire a write lock and get the next free index
            stamp = this.lock.writeLock();
            try {
                return this.getNewIndex(uniqueId);
            } finally {
                this.lock.unlockWrite(stamp);
            }
        }
        return index;
    }

    /**
     * Gets the next available index for the specified unique id and puts it
     * into the internal map and list.
     *
     * @param uniqueId The unique id
     * @return The index
     */
    private int getNewIndex(UUID uniqueId) {
        final int index = this.uniqueIdsByIndex.size();
        this.uniqueIdsByIndex.add(uniqueId);
        this.uniqueIds.put(uniqueId, index);
        return index;
    }

    Object2IntMap<UUID> getUniqueIds() {
        return this.uniqueIds;
    }

    List<UUID> getUniqueIdsByIndex() {
        return this.uniqueIdsByIndex;
    }
}
