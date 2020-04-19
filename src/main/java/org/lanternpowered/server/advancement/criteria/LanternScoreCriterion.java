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
