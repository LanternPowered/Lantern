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
package org.lanternpowered.server.advancement.criteria;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternScoreCriterion extends LanternCriterion implements ScoreAdvancementCriterion {

    private final int goal;
    private final String[] ids;

    LanternScoreCriterion(String name, @Nullable FilteredTrigger<?> trigger, int goal) {
        super(name, trigger);
        this.goal = goal;
        this.ids = new String[goal];
        for (int i = 0; i < goal; i++) {
            this.ids[i] = name + "&score_index=" + i;
        }
    }

    @Override
    public int getGoal() {
        return this.goal;
    }

    @Override
    MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("goal", this.goal);
    }

    /**
     * Gets the internal ids of this {@link LanternScoreCriterion}.
     *
     * @return The ids
     */
    public String[] getIds() {
        return this.ids;
    }
}
