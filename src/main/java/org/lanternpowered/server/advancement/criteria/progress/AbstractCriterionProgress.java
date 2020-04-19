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

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.spongepowered.api.advancement.criteria.CriterionProgress;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractCriterionProgress<T extends AbstractCriterion> implements CriterionProgress {

    static final int INVALID_TIME = -1;

    final T criterion;
    final LanternAdvancementProgress progress;

    AbstractCriterionProgress(T criterion, LanternAdvancementProgress progress) {
        this.criterion = criterion;
        this.progress = progress;
    }

    public LanternAdvancementProgress getAdvancementProgress() {
        return this.progress;
    }

    @Override
    public T getCriterion() {
        return this.criterion;
    }

    @Override
    public Instant grant() { // TODO: Return optional
        return grant(this.progress::invalidateAchievedState).orElse(Instant.MIN);
    }

    abstract Optional<Instant> grant(Runnable invalidator);

    @Override
    public Optional<Instant> revoke() {
        return revoke(this.progress::invalidateAchievedState);
    }

    abstract Optional<Instant> revoke(Runnable invalidator);

    public void attachTrigger() {
    }

    public void detachTrigger() {
    }

    public void saveProgress(Map<String, Instant> progress) {
    }

    public void loadProgress(Map<String, Instant> progress) {
    }

    public void fillProgress(Object2LongMap<String> progress) {
    }

    public void invalidateAchievedState() {
    }
}
