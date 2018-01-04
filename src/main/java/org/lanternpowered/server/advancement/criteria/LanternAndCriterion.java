package org.lanternpowered.server.advancement.criteria;

import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.OrCriterion;

import java.util.Collection;

public final class LanternAndCriterion extends AbstractOperatorCriterion implements OrCriterion {

    LanternAndCriterion(Collection<AdvancementCriterion> criteria) {
        super("and", criteria);
    }
}
