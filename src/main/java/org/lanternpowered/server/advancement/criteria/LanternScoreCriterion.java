package org.lanternpowered.server.advancement.criteria;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import javax.annotation.Nullable;

public class LanternScoreCriterion extends LanternCriterion implements ScoreAdvancementCriterion {

    private final int goal;

    LanternScoreCriterion(String name, @Nullable FilteredTrigger<?> trigger, int goal) {
        super(name, trigger);
        this.goal = goal;
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
}
