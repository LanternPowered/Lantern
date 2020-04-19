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

import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.AndCriterion;

import java.util.Arrays;

public final class AndCriterionFactory implements AndCriterion.Factory {

    @Override
    public AdvancementCriterion of(AdvancementCriterion... criteria) {
        return AbstractCriterion.build(AndCriterion.class, LanternAndCriterion::new, Arrays.asList(criteria));
    }

    @Override
    public AdvancementCriterion of(Iterable<AdvancementCriterion> criteria) {
        return AbstractCriterion.build(AndCriterion.class, LanternAndCriterion::new, criteria);
    }
}
