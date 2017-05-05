/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2LongMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

public final class AdvancementProgress extends Achievable {

    private final Advancement advancement;
    private final Map<AdvancementCriterion, CriterionProgress> progress = new HashMap<>();

    private long achievingTime = INVALID_TIME;
    private boolean lock;

    public AdvancementProgress(Advancement advancement) {
        this.advancement = advancement;
        for (AdvancementCriterion criterion : advancement.getLeafCriteria()) {
            final CriterionProgress progress;
            if (criterion instanceof ScoreAdvancementCriterion) {
                progress = new ScoreCriterionProgress(this, (ScoreAdvancementCriterion) criterion);
            } else {
                progress = new SimpleCriterionProgress(this, criterion);
            }
            this.progress.put(criterion, progress);
        }
    }

    /**
     * Gets the {@link Advancement}.
     *
     * @return The advancement
     */
    public Advancement getAdvancement() {
        return this.advancement;
    }

    /**
     * Gets the {@link ScoreCriterionProgress} for the given {@link ScoreAdvancementCriterion}.
     *
     * @param criterion The score criterion
     * @return The score criterion progress
     */
    public ScoreCriterionProgress tryGet(ScoreAdvancementCriterion criterion) {
        return get(criterion).orElseThrow(() -> new IllegalStateException("The score criterion " + criterion.getName() +
                " isn't present on the advancement " + this.advancement.getName()));
    }

    /**
     * Gets the {@link ScoreCriterionProgress} for the given {@link ScoreAdvancementCriterion},
     * this will only be returned of the criterion is present on the {@link Advancement}.
     *
     * @param criterion The score criterion
     * @return The score criterion progress
     */
    public Optional<ScoreCriterionProgress> get(ScoreAdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        return Optional.ofNullable((ScoreCriterionProgress) this.progress.get(criterion));
    }

    /**
     * Gets the {@link CriterionProgress} for the given {@link AdvancementCriterion}.
     * <p>
     * For AND and OR criteria will wrapped {@link CriterionProgress} be provided that will
     * interact with the {@link CriterionProgress}s for every child {@link AdvancementCriterion}s.
     *
     * @param criterion The criterion
     * @return The criterion progress
     */
    public CriterionProgress tryGet(AdvancementCriterion criterion) {
        return get(criterion).orElseThrow(() -> new IllegalStateException("The criterion " + criterion.getName() +
                " isn't present on the advancement " + this.advancement.getName()));
    }

    /**
     * Gets the {@link CriterionProgress} for the given {@link AdvancementCriterion},
     * this will only be returned of the criterion is present on the {@link Advancement}.
     * <p>
     * For AND and OR criteria will wrapped {@link CriterionProgress} be provided that will
     * interact with the {@link CriterionProgress}s for every child {@link AdvancementCriterion}s.
     *
     * @param criterion The criterion
     * @return The criterion progress
     */
    public Optional<CriterionProgress> get(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        if (criterion == AdvancementCriterion.EMPTY || !this.advancement.getCriteria().contains(criterion)) {
            return Optional.empty();
        } else if (criterion instanceof AdvancementCriterion.And) {
            return Optional.of(new AndCriterionProgress(this, (AdvancementCriterion.And) criterion));
        } else if (criterion instanceof AdvancementCriterion.Or) {
            return Optional.of(new OrCriterionProgress(this, (AdvancementCriterion.Or) criterion));
        }
        return Optional.of(this.progress.get(criterion));
    }

    @Override
    public OptionalLong get() {
        return this.achievingTime == INVALID_TIME ? OptionalLong.empty() : OptionalLong.of(this.achievingTime);
    }

    @Override
    public long set() {
        this.lock = true;
        try {
            long time = -1L;
            for (CriterionProgress progress : this.progress.values()) {
                final long time1 = progress.set();
                if (time1 > time) {
                    time = time1;
                }
            }
            this.achievingTime = time;
            return time;
        } finally {
            this.lock = false;
        }
    }

    @Override
    public OptionalLong revoke() {
        this.lock = true;
        try {
            OptionalLong time = OptionalLong.empty();
            for (CriterionProgress progress : this.progress.values()) {
                final OptionalLong time1 = progress.revoke();
                if (time1.isPresent() && (!time.isPresent() || time1.getAsLong() > time.getAsLong())) {
                    time = time1;
                }
            }
            this.achievingTime = INVALID_TIME;
            return time;
        } finally {
            this.lock = false;
        }
    }

    @Override
    void resetDirtyState() {
        this.progress.values().forEach(Achievable::resetDirtyState);
    }

    @Override
    void fillDirtyProgress(Object2LongMap<String> progress) {
        this.progress.values().forEach(criterionProgress -> criterionProgress.fillDirtyProgress(progress));
    }

    @Override
    void fillProgress(Object2LongMap<String> progress) {
        this.progress.values().forEach(criterionProgress -> criterionProgress.fillProgress(progress));
    }

    void updateAchievedState(long time) {
        if (this.lock) {
            return;
        }
        final boolean achieved = testAchievedState(this.advancement.getCriterion());
        if (achieved) {
            this.achievingTime = time;
        } else {
            this.achievingTime = INVALID_TIME;
        }
    }

    private boolean testAchievedState(AdvancementCriterion criterion) {
        if (criterion == AdvancementCriterion.EMPTY) {
            return true;
        }
        if (criterion instanceof AdvancementCriterion.Or) {
            for (AdvancementCriterion criterion1 : ((AdvancementCriterion.Or) criterion).getCriteria()) {
                if (testAchievedState(criterion1)) {
                    return true;
                }
            }
            return false;
        } else if (criterion instanceof AdvancementCriterion.And) {
            for (AdvancementCriterion criterion1 : ((AdvancementCriterion.And) criterion).getCriteria()) {
                if (!testAchievedState(criterion1)) {
                    return false;
                }
            }
            return true;
        } else {
            return this.progress.get(criterion).achieved();
        }
    }
}
