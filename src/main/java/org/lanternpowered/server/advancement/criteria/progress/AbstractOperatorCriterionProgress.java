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

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.AbstractOperatorCriterion;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;

import java.time.Instant;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("OptionalAssignedToNull")
public abstract class AbstractOperatorCriterionProgress<T extends AbstractOperatorCriterion> extends AbstractCriterionProgress<T> {

    @Nullable private Optional<Instant> cachedAchievedState;
    @Nullable private Instant lastAchievingTime;

    AbstractOperatorCriterionProgress(T criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public Optional<Instant> get() {
        if (this.cachedAchievedState == null) {
            this.cachedAchievedState = get0();
        }
        return this.cachedAchievedState;
    }

    abstract Optional<Instant> get0();

    @Override
    Optional<Instant> grant(Runnable invalidator) {
        Instant time = null;
        final boolean[] change = new boolean[1];
        for (AdvancementCriterion criterion : this.criterion.getCriteria()) {
            final Optional<Instant> time1 = ((AbstractCriterionProgress<?>) this.progress.get(criterion).get())
                    .grant(() -> change[0] = true);
            if (!time1.isPresent()) {
                time = Instant.MAX;
            } else if (time == null || time1.get().isAfter(time)) {
                time = time1.get();
            }
        }
        if (change[0]) {
            invalidator.run();
        }
        checkNotNull(time); // Should be impossible
        if (time == Instant.MAX) {
            // Somebody prevented a criterion to be granted
            return Optional.empty();
        }
        return Optional.of(time);
    }

    @Override
    Optional<Instant> revoke(Runnable invalidator) {
        final Optional<Instant> previousState = get();
        final boolean[] change = new boolean[1];
        for (AdvancementCriterion criterion : this.criterion.getCriteria()) {
            ((AbstractCriterionProgress<?>) this.progress.get(criterion).get()).revoke(() -> change[0] = true);
        }
        if (change[0]) {
            invalidator.run();
        }
        return previousState;
    }

    @Override
    public void invalidateAchievedState() {
        this.cachedAchievedState = null;
    }

    @Override
    public void fillProgress(Object2LongMap<String> progress) {
        final Instant achievingTime = get().orElse(null);
        progress.put(getCriterion().getName(), achievingTime == null ? INVALID_TIME : achievingTime.toEpochMilli());
    }
}
