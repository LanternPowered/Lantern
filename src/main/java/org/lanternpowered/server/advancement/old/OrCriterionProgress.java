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
package org.lanternpowered.server.advancement.old;

import java.util.OptionalLong;

final class OrCriterionProgress extends MultiCriterionProgress {

    OrCriterionProgress(AdvancementProgress progress, AdvancementCriterion.Or criterion) {
        super(progress, criterion);
    }

    @Override
    public AdvancementCriterion.Or getCriterion() {
        return (AdvancementCriterion.Or) super.getCriterion();
    }

    @Override
    public boolean achieved() {
        for (AdvancementCriterion criterion : getCriterion().getCriteria()) {
            final OptionalLong time1 = getProgress().get(criterion).get().get();
            if (time1.isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OptionalLong get() {
        OptionalLong time = OptionalLong.empty();
        for (AdvancementCriterion criterion : getCriterion().getCriteria()) {
            final OptionalLong time1 = getProgress().get(criterion).get().get();
            if (time1.isPresent() && (!time.isPresent() || time1.getAsLong() > time.getAsLong())) {
                time = time1;
            }
        }
        return time;
    }
}
