package org.lanternpowered.server.advancement.criteria;

import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.OrCriterion;

import java.util.Collection;

public final class LanternOrCriterion extends AbstractOperatorCriterion implements OrCriterion {

    LanternOrCriterion(Collection<AdvancementCriterion> criteria) {
        super("or", criteria);
    }
}
