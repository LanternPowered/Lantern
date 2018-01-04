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

import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.LanternAndCriterion;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;

import java.time.Instant;
import java.util.Optional;

public class LanternAndCriterionProgress extends AbstractOperatorCriterionProgress<LanternAndCriterion> {

    public LanternAndCriterionProgress(LanternAndCriterion criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public Optional<Instant> get0() {
        Optional<Instant> time = Optional.empty();
        for (AdvancementCriterion criterion : getCriterion().getCriteria()) {
            final Optional<Instant> time1 = this.progress.get(criterion).get().get();
            if (!time1.isPresent()) {
                return Optional.empty();
            } else if (!time.isPresent() || time1.get().isAfter(time.get())) {
                time = time1;
            }
        }
        return time;
    }
}
