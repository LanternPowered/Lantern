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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"unchecked", "NullableProblems", "ConstantConditions"})
public abstract class AbstractCriterionBuilder<T extends AdvancementCriterion, B extends AdvancementCriterion.BaseBuilder<T, B>>
        implements AdvancementCriterion.BaseBuilder<T, B> {

    @Nullable FilteredTrigger trigger;
    String name;

    @Override
    public B trigger(FilteredTrigger<?> trigger) {
        checkNotNull(trigger, "trigger");
        this.trigger = trigger;
        return (B) this;
    }

    @Override
    public B name(String name) {
        checkNotNull(name, "name");
        this.name = name;
        return (B) this;
    }

    @Override
    public T build() {
        checkState(this.name != null, "The name must be set");
        return build0();
    }

    abstract T build0();

    @Override
    public B from(T value) {
        this.trigger = value.getTrigger().orElse(null);
        this.name = value.getName();
        return (B) this;
    }

    @Override
    public B reset() {
        this.trigger = null;
        this.name = null;
        return (B) this;
    }
}
