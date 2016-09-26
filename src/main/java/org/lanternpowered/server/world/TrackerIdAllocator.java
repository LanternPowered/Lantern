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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class TrackerIdAllocator {

    private final List<UUID> uniqueIdsByIndex = new ArrayList<>();
    private final Object2IntMap<UUID> uniqueIds = new Object2IntOpenHashMap<>();

    /**
     * Gets the index for the specified {@link UUID}.
     *
     * @param uniqueId The unique id
     * @return The tracking id
     */
    public synchronized int get(UUID uniqueId) {
        return this.uniqueIds.computeIfAbsent(uniqueId, uniqueId0 -> {
            final int index = this.uniqueIdsByIndex.size();
            this.uniqueIdsByIndex.add(uniqueId0);
            return index;
        });
    }

    public synchronized Optional<UUID> get(int trackingId) {
        return trackingId < 0 || trackingId > this.uniqueIdsByIndex.size() ? Optional.empty() :
                Optional.ofNullable(this.uniqueIdsByIndex.get(trackingId));
    }

    Object2IntMap<UUID> getUniqueIds() {
        return this.uniqueIds;
    }

    List<UUID> getUniqueIdsByIndex() {
        return this.uniqueIdsByIndex;
    }
}
