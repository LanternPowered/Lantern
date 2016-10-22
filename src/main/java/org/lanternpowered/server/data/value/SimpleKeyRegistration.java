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
package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.value.processor.ValueProcessor;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public abstract class SimpleKeyRegistration<V extends BaseValue<E>, E> implements KeyRegistration<V, E> {

    private final List<ElementHolderChangeListener> elementHolderChangeListeners = new ArrayList<>();
    private final List<ElementHolderChangeListener> unmodifiableElementHolderChangeListeners =
            Collections.unmodifiableList(this.elementHolderChangeListeners);
    private final Key<? extends V> key;

    SimpleKeyRegistration(Key<? extends V> key) {
        this.key = checkNotNull(key, "key");
    }

    @Override
    public Key<? extends V> getKey() {
        return this.key;
    }

    @Override
    public List<ElementHolderChangeListener> getElementChangeListeners() {
        return this.unmodifiableElementHolderChangeListeners;
    }

    @SuppressWarnings("unchecked")
    @Override
    public KeyRegistration<V, E> addElementChangeListener(ElementHolderChangeListener listener) {
        this.elementHolderChangeListeners.add(checkNotNull(listener, "listener"));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public KeyRegistration<V, E> applyValueProcessor(Consumer<ValueProcessor.Builder<V, E>> builderConsumer) {
        ValueProcessor.Builder<V, E> builder = ValueProcessor.builder();
        checkNotNull(builderConsumer, "builderConsumer").accept(builder);
        return this.addValueProcessor(builder.build());
    }

    public static class SingleProcessor<V extends BaseValue<E>, E> extends SimpleKeyRegistration<V, E> {

        @Nullable private List<ValueProcessor<V, E>> valueProcessor;

        SingleProcessor(Key<? extends V> key) {
            super(key);
        }

        @Override
        public List<ValueProcessor<V, E>> getValueProcessors() {
            return this.valueProcessor == null ? Collections.emptyList() : this.valueProcessor;
        }

        @SuppressWarnings("unchecked")
        @Override
        public KeyRegistration<V, E> addValueProcessor(ValueProcessor<V, E> valueProcessor) {
            checkNotNull(valueProcessor, "valueProcessor");
            checkState(this.valueProcessor == null, "Only one valueProcessor may be added to this processor.");
            this.valueProcessor = Collections.singletonList(valueProcessor);
            return this;
        }

        public SingleProcessor<V, E> copy() {
            final SingleProcessor<V, E> copy = new SingleProcessor<>(getKey());
            copy.valueProcessor = this.valueProcessor;
            return copy;
        }
    }

    public static class MultipleProcessors<V extends BaseValue<E>, E> extends SimpleKeyRegistration<V, E> {

        private final List<ValueProcessor<V, E>> valueProcessors = new ArrayList<>();
        private final List<ValueProcessor<V, E>> unmodifiableValueProcessors = Collections.unmodifiableList(this.valueProcessors);

        MultipleProcessors(Key<? extends V> key) {
            super(key);
        }

        @Override
        public List<ValueProcessor<V, E>> getValueProcessors() {
            return this.unmodifiableValueProcessors;
        }

        @SuppressWarnings("unchecked")
        @Override
        public KeyRegistration<V, E> addValueProcessor(ValueProcessor<V, E> valueProcessor) {
            this.valueProcessors.add(checkNotNull(valueProcessor, "valueProcessor"));
            return this;
        }
    }
}
