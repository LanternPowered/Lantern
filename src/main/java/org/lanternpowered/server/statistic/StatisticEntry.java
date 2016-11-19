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
package org.lanternpowered.server.statistic;

import javax.annotation.Nullable;

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
