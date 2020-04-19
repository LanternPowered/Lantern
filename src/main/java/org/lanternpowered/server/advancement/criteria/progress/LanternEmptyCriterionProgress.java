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
import org.lanternpowered.server.advancement.criteria.EmptyCriterion;

import java.time.Instant;
import java.util.Optional;

public class LanternEmptyCriterionProgress extends AbstractCriterionProgress<EmptyCriterion> {

    private final Instant now = Instant.now();

    public LanternEmptyCriterionProgress(EmptyCriterion criterion, LanternAdvancementProgress progress) {
        super(criterion, progress);
    }

    @Override
    public boolean achieved() {
        return true;
    }

    @Override
    Optional<Instant> grant(Runnable invalidator) {
        return Optional.of(this.now);
    }

    @Override
    Optional<Instant> revoke(Runnable invalidator) {
        return Optional.empty();
    }

    @Override
    public Optional<Instant> get() {
        return Optional.of(this.now);
    }

    @Override
    public void fillProgress(Object2LongMap<String> progress) {
        progress.put(getCriterion().getName(), this.now.toEpochMilli());
    }
}
