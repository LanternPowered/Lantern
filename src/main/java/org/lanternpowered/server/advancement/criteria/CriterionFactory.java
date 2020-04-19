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

public class CriterionFactory implements AdvancementCriterion.Factory {

    private final AdvancementCriterion dummy = new LanternCriterionBuilder().name("dummy").build();

    @Override
    public AdvancementCriterion empty() {
        return EmptyCriterion.INSTANCE;
    }

    @Override
    public AdvancementCriterion dummy() {
        return this.dummy;
    }
}
