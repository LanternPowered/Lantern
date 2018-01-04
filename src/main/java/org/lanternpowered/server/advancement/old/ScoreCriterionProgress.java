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

import static com.google.common.base.Preconditions.checkArgument;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.util.OptionalLong;

public class ScoreCriterionProgress extends AbstractCriterionProgress {

    private int score = 0;
    private int dirtyIndex = 0;

    ScoreCriterionProgress(AdvancementProgress progress, ScoreAdvancementCriterion criterion) {
        super(progress, criterion);
    }

    @Override
    public long set() {
        if (this.score == 0) {
            this.score = getCriterion().getGoal();
        }
        return super.set();
    }

    @Override
    public OptionalLong revoke() {
        this.score = 0;
        return super.revoke();
    }

    @Override
    void resetDirtyState() {
        this.dirtyIndex = this.score;
    }

    @Override
    void fillDirtyProgress(Object2LongMap<String> progress) {
        if (this.dirtyIndex != this.score) {
            if (this.dirtyIndex < this.score) {
                for (int i = this.dirtyIndex; i < this.score; i++) {
                    progress.put(getCriterion().ids[i],
                            i == getGoal() - 1 ? this.achievingTime : System.currentTimeMillis());
                }
            } else {
                for (int i = Math.max(0, this.score - 1); i < this.dirtyIndex; i++) {
                    progress.put(getCriterion().ids[i], INVALID_TIME);
                }
            }
        }
    }

    @Override
    void fillProgress(Object2LongMap<String> progress) {
        for (int i = 0; i < this.score; i++) {
            progress.put(getCriterion().ids[i],
                    i == getGoal() - 1 ? this.achievingTime : System.currentTimeMillis());
        }
    }

    @Override
    public ScoreAdvancementCriterion getCriterion() {
        return (ScoreAdvancementCriterion) super.getCriterion();
    }

    /**
     * Gets the goal value.
     *
     * @return The goal value
     */
    public int getGoal() {
        return getCriterion().getGoal();
    }

    /**
     * Gets the score value.
     *
     * @return The score value
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Adds the target score value, the score cannot exceed
     * the goal value ({@link #getGoal()}) or be under zero.
     * The achieved time will be returned if the goal is met.
     *
     * @param score The score to set
     * @return The achieving time, if achieved
     */
    public OptionalLong set(int score) {
        checkArgument(score >= 0, "score to add may not be negative");
        final int goal = getGoal();
        this.score = Math.min(score, goal);
        if (score == goal) {
            return OptionalLong.of(super.set());
        } else {
            return super.revoke();
        }
    }

    /**
     * Adds the target score value, the score cannot exceed
     * the goal value ({@link #getGoal()}). The achieved time
     * will be returned if the goal is met.
     *
     * @param score The score to add
     * @return The achieving time, if achieved
     */
    public OptionalLong add(int score) {
        checkArgument(score > 0, "score to add must be greater then zero");
        final int goal = getGoal();
        if (this.score == goal) {
            return super.get();
        }
        this.score = Math.min(this.score + score, goal);
        if (this.score == goal) {
            return OptionalLong.of(super.set());
        }
        return OptionalLong.empty();
    }

    /**
     * Removes the target score value, the score cannot go under zero.
     * The achieved time will be returned if the goal is met before.
     *
     * @param score The score to remove
     * @return The old achieving time, if achieved before
     */
    public OptionalLong remove(int score) {
        checkArgument(score > 0, "score to remove must be greater then zero");
        this.score = Math.max(0, this.score - score);
        return super.revoke();
    }
}
