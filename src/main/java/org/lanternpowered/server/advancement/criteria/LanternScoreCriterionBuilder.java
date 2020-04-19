/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
