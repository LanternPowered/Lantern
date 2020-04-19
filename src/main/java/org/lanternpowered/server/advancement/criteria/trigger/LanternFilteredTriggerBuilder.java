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
package org.lanternpowered.server.advancement.criteria.trigger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger;
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration;
import org.spongepowered.api.advancement.criteria.trigger.Trigger;

@SuppressWarnings({"unchecked", "ConstantConditions", "NullableProblems"})
public class LanternFilteredTriggerBuilder<C extends FilteredTriggerConfiguration> implements FilteredTrigger.Builder<C> {

    private C config;
    private Trigger<C> type;

    @Override
    public <T extends FilteredTriggerConfiguration> FilteredTrigger.Builder<T> type(Trigger<T> type) {
        checkNotNull(type, "type");
        this.type = (Trigger<C>) type;
        return (FilteredTrigger.Builder<T>) this;
    }

    @Override
    public FilteredTrigger.Builder<C> config(C config) {
        checkNotNull(config, "config");
        this.config = config;
        return this;
    }

    @Override
    public FilteredTrigger<C> build() {
        checkState(this.type != null, "The type must be set");
        checkState(this.config != null, "The config must be set");
        return new LanternFilteredTrigger<>(this.type, this.config);
    }

    @Override
    public FilteredTrigger.Builder<C> from(FilteredTrigger<C> value) {
        this.config = value.getConfiguration();
        this.type = value.getType();
        return this;
    }

    @Override
    public FilteredTrigger.Builder<C> reset() {
        this.config = null;
        this.type = null;
        return this;
    }
}
