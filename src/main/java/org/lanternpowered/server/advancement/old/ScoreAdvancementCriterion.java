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

import static com.google.common.base.Preconditions.checkState;

public class ScoreAdvancementCriterion extends AdvancementCriterion {

    private final int goal;
    final String[] ids;

    /**
     * Creates a new {@link ScoreAdvancementCriterion} with the target goal value
     * that should be achieved to trigger this criterion.
     *
     * @param goal The goal value
     */
    public ScoreAdvancementCriterion(String name, int goal) {
        super("score", String.format("%s{goal=%s}", name, goal));
        checkState(goal > 0, "The goal must be greater then 0");
        this.goal = goal;
        this.ids = new String[goal];
        for (int i = 0; i < goal; i++) {
            this.ids[i] = newIdentifier();
        }
    }

    /**
     * Gets the goal value.
     *
     * @return The goal value
     */
    public int getGoal() {
        return this.goal;
    }
}
