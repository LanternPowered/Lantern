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

import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.util.OptionalLong;

abstract class MultiCriterionProgress extends CriterionProgress {

    MultiCriterionProgress(AdvancementProgress progress, AdvancementCriterion.Multi criterion) {
        super(progress, criterion);
    }

    @Override
    public AdvancementCriterion.Multi getCriterion() {
        return (AdvancementCriterion.Multi) super.getCriterion();
    }

    @Override
    public long set() {
        long time = -1L;
        for (AdvancementCriterion criterion : getCriterion().getCriteria()) {
            final long time1 = getProgress().get(criterion).get().set();
            if (time1 > time) {
                time = time1;
            }
        }
        return time;
    }

    @Override
    public OptionalLong revoke() {
        OptionalLong time = OptionalLong.empty();
        for (AdvancementCriterion criterion : getCriterion().getCriteria()) {
            final OptionalLong time1 = getProgress().get(criterion).get().revoke();
            if (time1.isPresent() && (!time.isPresent() || time1.getAsLong() > time.getAsLong())) {
                time = time1;
            }
        }
        return time;
    }

    @Override
    void resetDirtyState() {
    }

    @Override
    void fillDirtyProgress(Object2LongMap<String> progress) {
    }

    @Override
    void fillProgress(Object2LongMap<String> progress) {
    }
}
