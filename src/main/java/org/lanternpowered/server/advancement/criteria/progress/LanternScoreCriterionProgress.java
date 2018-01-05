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
package org.lanternpowered.server.advancement.criteria.progress;

import static com.google.common.base.Preconditions.checkArgument;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.cause.Cause;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class LanternScoreCriterionProgress extends LanternCriterionProgressBase<LanternScoreCriterion> implements ScoreCriterionProgress {

    private int score = 0;
    private int dirtyIndex = 0;

    public LanternScoreCriterionProgress(LanternScoreCriterion criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    Optional<Instant> grant(Runnable invalidator) {
        return set(getGoal(), invalidator);
    }

    @Override
    Optional<Instant> revoke(Runnable invalidator) {
        return set(0, invalidator);
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public Optional<Instant> set(int score) {
        return set(score, this.progress::invalidateAchievedState);
    }

    public Optional<Instant> set(int score, Runnable invalidator) {
        checkArgument(score >= 0, "Score to set may not be negative");
        checkArgument(score <= getGoal(), "Score to set may not be greater than the goal");
        if (score == this.score) {
            return get();
        }
        final Cause cause = CauseStack.current().getCurrentCause();
        final Advancement advancement = this.progress.getAdvancement();
        final LanternPlayer player = this.progress.getPlayer();
        CriterionEvent.Score.Change event;
        if (score == getGoal()) {
            event = SpongeEventFactory.createCriterionEventScoreGrant(
                    cause, advancement, this.criterion, player, Instant.now(), score, this.score);
        } else if (this.score == getGoal()) {
            event = SpongeEventFactory.createCriterionEventScoreRevoke(
                    cause, advancement, this.criterion, player, score, this.score);
        } else {
            event = SpongeEventFactory.createCriterionEventScoreChange(
                    cause, advancement, this.criterion, player, score, this.score);
        }
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            return get();
        }
        this.score = score;
        if (event instanceof CriterionEvent.Grant) {
            this.achievingTime = ((CriterionEvent.Grant) event).getTime();
            invalidator.run();
        } else if (event instanceof CriterionEvent.Revoke) {
            this.achievingTime = null;
            invalidator.run();
        }
        return Optional.ofNullable(this.achievingTime);
    }

    @Override
    public Optional<Instant> add(int score) {
        checkArgument(score >= 0, "Score to add may not be negative");
        return set(Math.min(getScore() + score, getGoal()));
    }

    @Override
    public Optional<Instant> remove(int score) {
        checkArgument(score >= 0, "Score to remove may not be negative");
        return set(Math.max(getScore() - score, getGoal()));
    }

    @Override
    public void resetDirtyState() {
        this.dirtyIndex = this.score;
    }

    @Override
    public void fillDirtyProgress(Object2LongMap<String> progress) {
        if (this.dirtyIndex != this.score) {
            if (this.dirtyIndex < this.score) {
                long now = -1L;
                for (int i = this.dirtyIndex; i < this.score; i++) {
                    progress.put(getCriterion().getIds()[i], this.achievingTime != null ? this.achievingTime.toEpochMilli() :
                            now == -1L ? (now = System.currentTimeMillis()) : now);
                }
            } else {
                for (int i = Math.max(0, this.score - 1); i < this.dirtyIndex; i++) {
                    progress.put(getCriterion().getIds()[i], INVALID_TIME);
                }
            }
        }
    }

    @Override
    public void fillProgress(Object2LongMap<String> progress) {
        long now = -1L;
        for (int i = 0; i < this.score; i++) {
            progress.put(getCriterion().getIds()[i], this.achievingTime != null ? this.achievingTime.toEpochMilli() :
                    now == -1L ? (now = System.currentTimeMillis()) : now);
        }
    }

    @Override
    public void saveProgress(Map<String, Instant> progress) {
        Instant now = null;
        for (int i = 0; i < this.score; i++) {
            progress.put(getCriterion().getIds()[i], this.achievingTime != null ? this.achievingTime :
                    now == null ? (now = Instant.now()) : now);
        }
        if (this.achievingTime != null) {
            progress.put(getCriterion().getName(), this.achievingTime);
        }
    }

    @Override
    public void loadProgress(Map<String, Instant> progress) {
        this.achievingTime = progress.get(getCriterion().getName());
        if (this.achievingTime == null) {
            this.score = 0;
            Instant lastTime = null;
            for (int i = 0; i < getGoal(); i++) {
                final Instant time = progress.get(getCriterion().getIds()[i]);
                if (time != null) {
                    this.score++;
                    if (lastTime == null || time.isAfter(lastTime)) {
                        lastTime = time;
                    }
                }
            }
            if (this.score == getGoal()) {
                this.achievingTime = lastTime;
            }
        } else {
            this.score = getGoal();
        }
    }
}
