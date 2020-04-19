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
package org.lanternpowered.server.advancement.criteria;

import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternCriterion extends AbstractCriterion {

    @Nullable private final FilteredTrigger<?> trigger;

    LanternCriterion(String name, @Nullable FilteredTrigger<?> trigger) {
        super(name);
        this.trigger = trigger;
    }

    @Override
    public Optional<FilteredTrigger<?>> getTrigger() {
        return Optional.ofNullable(this.trigger);
    }
}
