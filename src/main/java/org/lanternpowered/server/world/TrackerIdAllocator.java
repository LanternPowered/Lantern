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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class TrackerIdAllocator {

    public static final int INVALID_ID = -1;

    private final List<UUID> uniqueIdsByIndex = new ArrayList<>();
    private final Object2IntMap<UUID> uniqueIds = new Object2IntOpenHashMap<>();
    private final StampedLock lock = new StampedLock();

    {
        this.uniqueIds.defaultReturnValue(INVALID_ID);
    }

    public Optional<UUID> get(int trackingId) {
        if (trackingId < 0) {
            return Optional.empty();
        }
        long stamp = this.lock.tryOptimisticRead();
        UUID uniqueId = null;
        if (stamp != 0L) {
            uniqueId = getUniqueIdFromIndex(trackingId);
        }
        if (stamp == 0L || !this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            try {
                uniqueId = getUniqueIdFromIndex(trackingId);
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
        int index = stamp == 0L ? 0 : this.uniqueIds.getInt(uniqueId);
        // Optimistic read failed, now just
        // acquire a read lock.
        if (stamp == 0L || !this.lock.validate(stamp)) {
            stamp = this.lock.readLock();
            index = this.uniqueIds.getInt(uniqueId);
            // Check if the index is valid
            if (index == INVALID_ID) {
                // Try to convert the read lock to a write lock
                long stamp1 = this.lock.tryConvertToWriteLock(stamp);
                // Could not convert the lock, release the old
                // lock and acquire the write lock
                if (stamp1 == 0L) {
                    this.lock.unlockRead(stamp);
                    stamp1 = this.lock.writeLock();
                    index = this.uniqueIds.getInt(uniqueId);
                    if (index != INVALID_ID) {
                        // Release the write lock
                        this.lock.unlockWrite(stamp1);
                        return index;
                    }
                }
                try {
                    // Get the next free index
                    return getNewIndex(uniqueId);
                } finally {
                    // Release the write lock
                    this.lock.unlockWrite(stamp1);
                }
            } else {
                // Release the read lock
                this.lock.unlockRead(stamp);
            }
        // Check if the index is valid
        } else if (index == INVALID_ID) {
            // Acquire a write lock and get the next free index
            stamp = this.lock.writeLock();
            try {
                return getNewIndex(uniqueId);
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
