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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.List;
import java.util.OptionalLong;

public final class AdvancementProgress {

    public static final long INVALID_TIME = -1L;

    private final Advancement advancement;
    private final Object2LongMap<AdvancementCriterion> progress = new Object2LongOpenHashMap<>();

    private long achieveTime = INVALID_TIME;

    private boolean dirty;

    public AdvancementProgress(Advancement advancement) {
        this.advancement = advancement;
        this.progress.defaultReturnValue(INVALID_TIME);
    }

    boolean isDirty() {
        return this.dirty;
    }

    void clearDirty() {
        this.dirty = false;
    }

    /**
     * Gets whether the {@link Advancement} is achieved.
     *
     * @return Is achieved
     */
    public boolean isAchieved() {
        return this.achieveTime != INVALID_TIME;
    }

    /**
     * Gets the time when the {@link Advancement} was achieved, if present.
     *
     * @return The achieve time
     */
    public long getAchieveTime() {
        return this.achieveTime;
    }

    private void updateAchievedState(long time) {
        boolean achieved = false;
        // OR loop
        for (List<AdvancementCriterion> criteria : this.advancement.getCriteria()) {
            boolean fail = false;
            // AND loop
            for (AdvancementCriterion criterion : criteria) {
                if (this.progress.getLong(criterion) == INVALID_TIME) {
                    fail = true;
                    break;
                }
            }
            if (!fail) {
                achieved = true;
                break;
            }
        }
        if (this.advancement.getCriteria().isEmpty()) {
            achieved = true;
        }
        if (achieved != isAchieved()) {
            this.dirty = true;
        }
        if (achieved) {
            this.achieveTime = time;
        } else {
            this.achieveTime = INVALID_TIME;
        }
    }

    /**
     * Gets the {@link Advancement}.
     *
     * @return The advancement
     */
    public Advancement getAdvancement() {
        return this.advancement;
    }

    /**
     * Achieves the {@link Advancement} completely.
     *
     * @return The achieving time
     */
    public long achieve() {
        if (this.achieveTime != INVALID_TIME) {
            return this.achieveTime;
        }
        final long achieveTime = System.currentTimeMillis();
        for (List<AdvancementCriterion> criteria : this.advancement.getCriteria()) {
            for (AdvancementCriterion criterion : criteria) {
                this.progress.putIfAbsent(criterion, achieveTime);
            }
        }
        this.achieveTime = achieveTime;
        this.dirty = true;
        return achieveTime;
    }

    /**
     * Achieves the {@link AdvancementCriterion}, if achieved before
     * that time will be returned.
     *
     * @param criterion The criterion
     * @return The achieving time
     */
    public long achieve(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        if (!this.advancement.getAdvancementCriteria0().contains(criterion)) {
            return INVALID_TIME;
        }
        long time = this.progress.get(criterion);
        if (time == INVALID_TIME) {
            time = System.currentTimeMillis();
            this.progress.put(criterion, time);
            if (!isAchieved()) {
                updateAchievedState(time);
            }
        }
        return time;
    }

    /**
     * Revokes the {@link AdvancementCriterion}.
     *
     * @param criterion The criterion
     */
    public void revoke(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        final long time = this.progress.getLong(criterion);
        if (time != INVALID_TIME) {
            this.progress.remove(criterion);
            if (isAchieved()) {
                updateAchievedState(this.achieveTime);
            }
        }
    }

    /**
     * Gets the achieving time for the specified {@link AdvancementCriterion}
     * if achieved before, otherwise {@link OptionalLong#empty()}.
     *
     * @param criterion The criterion
     * @return The achieving time if present, otherwise {@link OptionalLong#empty()}
     */
    public OptionalLong get(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        final long time = this.progress.getLong(criterion);
        return time == INVALID_TIME ? OptionalLong.empty() : OptionalLong.of(time);
    }
}
