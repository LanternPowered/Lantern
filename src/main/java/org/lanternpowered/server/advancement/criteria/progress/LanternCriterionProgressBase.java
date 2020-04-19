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
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.lanternpowered.server.advancement.criteria.trigger.LanternTrigger;

import java.time.Instant;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

abstract class LanternCriterionProgressBase<T extends AbstractCriterion> extends AbstractCriterionProgress<T> {

    @Nullable Instant achievingTime;
    private boolean attached = false;

    LanternCriterionProgressBase(T criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public boolean achieved() {
        return this.achievingTime != null;
    }

    @Override
    public Optional<Instant> get() {
        return Optional.ofNullable(this.achievingTime);
    }

    @Override
    public void attachTrigger() {
        this.criterion.getTrigger().ifPresent(trigger -> {
            // Only attach once
            if (this.attached) {
                return;
            }
            this.attached = true;
            ((LanternTrigger) trigger.getType()).add(getAdvancementProgress().getPlayerAdvancements(), this);
        });
    }

    @Override
    public void detachTrigger() {
        this.criterion.getTrigger().ifPresent(trigger -> {
            // Only detach once
            if (!this.attached) {
                return;
            }
            this.attached = false;
            ((LanternTrigger) trigger.getType()).remove(getAdvancementProgress().getPlayerAdvancements(), this);
        });
    }
}
