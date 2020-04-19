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
package org.lanternpowered.server.statistic;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class StatisticEntry {

    private final Object lock = new Object();
    @Nullable private final LanternStatistic statistic;
    private long value = 0;
    private boolean dirty;

    StatisticEntry(LanternStatistic statistic) {
        this.statistic = statistic;
    }

    public long get() {
        synchronized (this.lock) {
            return this.value;
        }
    }

    public void set(long value) {
        synchronized (this.lock) {
            if (this.value != value) {
                this.value = value;
                this.dirty = true;
            }
        }
    }

    public long add(long value) {
        synchronized (this.lock) {
            value = this.value + value;
            this.value = value;
            return value;
        }
    }

    boolean isDirty(boolean reset) {
        synchronized (this.lock) {
            if (this.dirty) {
                if (reset) {
                    this.dirty = false;
                }
                return true;
            }
            return false;
        }
    }

    @Nullable
    public LanternStatistic getStatistic() {
        return this.statistic;
    }
}
