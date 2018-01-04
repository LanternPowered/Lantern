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
import org.lanternpowered.server.advancement.criteria.EmptyCriterion;

import java.time.Instant;
import java.util.Optional;

public class LanternEmptyCriterionProgress extends AbstractCriterionProgress<EmptyCriterion> {

    private final Instant now = Instant.now();

    public LanternEmptyCriterionProgress(EmptyCriterion criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public boolean achieved() {
        return true;
    }

    @Override
    Optional<Instant> grant(Runnable invalidator) {
        return Optional.of(this.now);
    }

    @Override
    Optional<Instant> revoke(Runnable invalidator) {
        return Optional.empty();
    }

    @Override
    public Optional<Instant> get() {
        return Optional.of(this.now);
    }

    @Override
    public void fillProgress(Object2LongMap<String> progress) {
        progress.put(getCriterion().getName(), this.now.toEpochMilli());
    }
}
