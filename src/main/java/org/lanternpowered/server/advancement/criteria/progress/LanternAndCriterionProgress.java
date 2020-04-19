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
package org.lanternpowered.server.advancement.criteria.progress;

import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.LanternAndCriterion;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;

import java.time.Instant;
import java.util.Optional;

public class LanternAndCriterionProgress extends AbstractOperatorCriterionProgress<LanternAndCriterion> {

    public LanternAndCriterionProgress(LanternAndCriterion criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public Optional<Instant> get0() {
        Optional<Instant> time = Optional.empty();
        for (AdvancementCriterion criterion : getCriterion().getCriteria()) {
            final Optional<Instant> time1 = this.progress.get(criterion).get().get();
            if (!time1.isPresent()) {
                return Optional.empty();
            } else if (!time.isPresent() || time1.get().isAfter(time.get())) {
                time = time1;
            }
        }
        return time;
    }
}
