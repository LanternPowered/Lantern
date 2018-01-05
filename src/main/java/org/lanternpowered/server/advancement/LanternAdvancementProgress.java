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
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.lanternpowered.server.advancement.criteria.LanternAndCriterion;
import org.lanternpowered.server.advancement.criteria.LanternCriterion;
import org.lanternpowered.server.advancement.criteria.LanternOrCriterion;
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion;
import org.lanternpowered.server.advancement.criteria.progress.AbstractCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternAndCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternOrCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternScoreCriterionProgress;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.CriterionProgress;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LanternAdvancementProgress implements AdvancementProgress {

    private final LanternPlayerAdvancements playerAdvancements;
    private final LanternAdvancement advancement;

    private final Map<AdvancementCriterion, AbstractCriterionProgress> progress = new HashMap<>();

    private Optional<Instant> achievedState = Optional.empty();

    // Whether the progress of the advancement should be updated
    boolean dirtyProgress;

    // Whether the visibility of the advancement should be updated
    boolean dirtyVisibility;

    // Whether the advancement is currently visible
    boolean visible;

    LanternAdvancementProgress(LanternPlayerAdvancements playerAdvancements, LanternAdvancement advancement) {
        this.playerAdvancements = playerAdvancements;
        this.advancement = advancement;
        for (AdvancementCriterion criterion : AbstractCriterion.getRecursiveCriteria(this.advancement.getCriterion())) {
            final AbstractCriterionProgress<?> progress;
            if (criterion instanceof LanternAndCriterion) {
                progress = new LanternAndCriterionProgress((LanternAndCriterion) criterion, this);
            } else if (criterion instanceof LanternOrCriterion) {
                progress = new LanternOrCriterionProgress((LanternOrCriterion) criterion, this);
            } else if (criterion instanceof LanternScoreCriterion) {
                progress = new LanternScoreCriterionProgress((LanternScoreCriterion) criterion, this);
            } else if (criterion instanceof LanternCriterion) {
                progress = new LanternCriterionProgress((LanternCriterion) criterion, this);
            } else {
                throw new IllegalStateException("Unsupported criterion: " + criterion);
            }
            this.progress.put(criterion, progress);
        }
    }

    @Override
    public Advancement getAdvancement() {
        return this.advancement;
    }

    @Override
    public Optional<ScoreCriterionProgress> get(ScoreAdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        return Optional.ofNullable((ScoreCriterionProgress) this.progress.get(criterion));
    }

    @Override
    public Optional<CriterionProgress> get(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        return Optional.ofNullable(this.progress.get(criterion));
    }

    @Override
    public Optional<Instant> get() {
        return get(this.advancement.getCriterion()).get().get();
    }

    @Override
    public Instant grant() { // TODO: Make this a optional in the API
        return get(this.advancement.getCriterion()).get().grant();
    }

    @Override
    public Optional<Instant> revoke() {
        return get(this.advancement.getCriterion()).get().revoke();
    }

    public LanternPlayerAdvancements getPlayerAdvancements() {
        return this.playerAdvancements;
    }

    public LanternPlayer getPlayer() {
        return this.playerAdvancements.getPlayer();
    }

    public void invalidateAchievedState() {
        // Invalidate the achieved state of all the criteria progress
        this.progress.values().forEach(AbstractCriterionProgress::invalidateAchievedState);
        // Get the new achieved state
        final Optional<Instant> achievedState = get();
        if (!this.achievedState.isPresent() && achievedState.isPresent()) {
            // The advancement got granted
            this.dirtyVisibility = true;
        } else if (this.achievedState.isPresent() && !achievedState.isPresent()) {
            // The advancement got revoked
            this.dirtyVisibility = true;
        }
        this.achievedState = achievedState;
        // The progress should be updated
        this.dirtyProgress = true;
        this.playerAdvancements.dirtyProgress.add(this);
    }

    void resetDirtyState() {
        this.dirtyProgress = false;
        // Reset the dirty states of the progress
        this.advancement.clientCriteria.getFirst().forEach(criterion -> this.progress.get(criterion).resetDirtyState());
    }

    void fillDirtyProgress(Object2LongMap<String> progress) {
        this.advancement.clientCriteria.getFirst().forEach(criterion -> this.progress.get(criterion).fillDirtyProgress(progress));
    }

    void fillProgress(Object2LongMap<String> progress) {
        this.advancement.clientCriteria.getFirst().forEach(criterion -> this.progress.get(criterion).fillProgress(progress));
    }
}
