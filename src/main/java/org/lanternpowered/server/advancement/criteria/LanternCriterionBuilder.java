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

public class LanternCriterionBuilder extends AbstractCriterionBuilder<AdvancementCriterion, AdvancementCriterion.Builder>
        implements AdvancementCriterion.Builder {

    @Override
    AdvancementCriterion build0() {
        return new LanternCriterion(this.name, this.trigger);
    }
}
