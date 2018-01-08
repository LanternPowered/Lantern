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
