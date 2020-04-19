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
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.advancement.LanternAdvancementProgress;
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
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
