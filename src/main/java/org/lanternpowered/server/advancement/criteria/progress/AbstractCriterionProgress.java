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
package org.lanternpowered.server.advancement.criteria.progress;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.spongepowered.api.advancement.criteria.CriterionProgress;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractCriterionProgress<T extends AbstractCriterion> implements CriterionProgress {

    static final int INVALID_TIME = -1;

    final T criterion;
    final LanternAdvancementProgress progress;

    AbstractCriterionProgress(T criterion, LanternAdvancementProgress progress) {
        this.criterion = criterion;
        this.progress = progress;
    }

    public LanternAdvancementProgress getAdvancementProgress() {
        return this.progress;
    }

    @Override
    public T getCriterion() {
        return this.criterion;
    }

    @Override
    public Instant grant() { // TODO: Return optional
        return grant(this.progress::invalidateAchievedState).orElse(Instant.MIN);
    }

    abstract Optional<Instant> grant(Runnable invalidator);

    @Override
    public Optional<Instant> revoke() {
        return revoke(this.progress::invalidateAchievedState);
    }

    abstract Optional<Instant> revoke(Runnable invalidator);

    public void attachTrigger() {
    }

    public void detachTrigger() {
    }

    public void saveProgress(Map<String, Instant> progress) {
    }

    public void loadProgress(Map<String, Instant> progress) {
    }

    public void fillProgress(Object2LongMap<String> progress) {
    }

    public void invalidateAchievedState() {
    }
}
