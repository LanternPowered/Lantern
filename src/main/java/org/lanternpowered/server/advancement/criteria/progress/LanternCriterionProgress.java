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

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.api.event.cause.Cause;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class LanternCriterionProgress extends LanternCriterionProgressBase<AbstractCriterion> {

    public LanternCriterionProgress(AbstractCriterion criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    Optional<Instant> grant(Runnable invalidator) {
        if (this.achievingTime != null) {
            return Optional.of(this.achievingTime);
        }
        final Cause cause = CauseStack.current().getCurrentCause();
        final Advancement advancement = this.progress.getAdvancement();
        final CriterionEvent.Grant event = SpongeEventFactory.createCriterionEventGrant(
                cause, advancement, this.criterion, this.progress.getPlayer(), Instant.now());
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        this.achievingTime = event.getTime();
        detachTrigger();
        invalidator.run();
        return Optional.of(this.achievingTime);
    }

    @Override
    Optional<Instant> revoke(Runnable invalidator) {
        if (this.achievingTime == null) {
            return Optional.empty();
        }
        final Cause cause = CauseStack.current().getCurrentCause();
        final Advancement advancement = this.progress.getAdvancement();
        final CriterionEvent.Revoke event = SpongeEventFactory.createCriterionEventRevoke(
                cause, advancement, this.criterion, this.progress.getPlayer());
        Sponge.getEventManager().post(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        final Instant achievingTime = this.achievingTime;
        this.achievingTime = null;
        attachTrigger();
        invalidator.run();
        return Optional.of(achievingTime);
    }

    @Override
    public void fillProgress(Object2LongMap<String> progress) {
        if (this.achievingTime != null) {
            progress.put(getCriterion().getName(), this.achievingTime.toEpochMilli());
        }
    }

    @Override
    public void saveProgress(Map<String, Instant> progress) {
        if (this.achievingTime != null) {
            progress.put(getCriterion().getName(), this.achievingTime);
        }
    }

    @Override
    public void loadProgress(Map<String, Instant> progress) {
        this.achievingTime = progress.get(getCriterion().getName());
    }
}
