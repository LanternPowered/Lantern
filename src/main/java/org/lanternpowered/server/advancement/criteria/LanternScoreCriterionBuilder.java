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

import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;

public class LanternScoreCriterionBuilder extends AbstractCriterionBuilder<ScoreAdvancementCriterion, ScoreAdvancementCriterion.Builder>
        implements ScoreAdvancementCriterion.Builder {

    private int goal;

    public LanternScoreCriterionBuilder() {
        reset();
    }

    @Override
    ScoreAdvancementCriterion build0() {
        return new LanternScoreCriterion(this.name, this.trigger, this.goal);
    }

    @Override
    public ScoreAdvancementCriterion.Builder from(ScoreAdvancementCriterion value) {
        this.goal = value.getGoal();
        return super.from(value);
    }

    @Override
    public ScoreAdvancementCriterion.Builder reset() {
        this.goal = 1;
        return super.reset();
    }

    @Override
    public ScoreAdvancementCriterion.Builder goal(int goal) {
        checkState(goal > 0, "The goal must be greater than zero.");
        this.goal = goal;
        return this;
    }
}
