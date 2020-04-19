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

public final class EmptyCriterion extends AbstractCriterion {

    public static final EmptyCriterion INSTANCE = new EmptyCriterion();

    private EmptyCriterion() {
        super("empty");
    }

    @Override
    public Optional<FilteredTrigger<?>> getTrigger() {
        return Optional.empty();
    }
}
