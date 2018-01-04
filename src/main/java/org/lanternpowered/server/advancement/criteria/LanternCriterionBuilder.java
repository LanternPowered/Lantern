package org.lanternpowered.server.advancement.criteria;

import org.spongepowered.api.advancement.criteria.AdvancementCriterion;

public class LanternCriterionBuilder extends AbstractCriterionBuilder<AdvancementCriterion, AdvancementCriterion.Builder>
        implements AdvancementCriterion.Builder {

    @Override
    AdvancementCriterion build0() {
        return new LanternCriterion(this.name, this.trigger);
    }
}
