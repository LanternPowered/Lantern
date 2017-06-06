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
package org.lanternpowered.server.data.processor;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.data.IValueContainer;
import org.lanternpowered.server.data.value.LanternValueFactory;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

final class SimpleProcessorKeyRegistration<V extends BaseValue<E>, E> implements ValueProcessorKeyRegistration<V, E>, Processor<V, E> {

    private final Key<? extends V> key;
    private final List<ValueProcessor<V, E>> processors = new ArrayList<>();
    private final List<ValueProcessor<V, E>> unmodifiableProcessors = Collections.unmodifiableList(this.processors);

    SimpleProcessorKeyRegistration(Key<? extends V> key) {
        checkNotNull(key, "key");
        this.key = key;
    }

    @Override
    public ValueProcessorKeyRegistration<V, E> add(ValueProcessor<V, E> processor) {
        checkNotNull(processor, "processor");
        this.processors.add(processor);
        return this;
    }

    @Override
    public ValueProcessorKeyRegistration<V, E> add(Consumer<ValueProcessorBuilder<V, E>> consumer) {
        checkNotNull(consumer, "consumer");
        final ValueProcessorBuilder<V, E> builder = ValueProcessorBuilder.create(this.key);
        consumer.accept(builder);
        this.processors.add(builder.build());
        return this;
    }

    @Override
    public List<ValueProcessor<V, E>> getAll() {
        return this.unmodifiableProcessors;
    }

    @Override
    public Key<? extends V> getKey() {
        return this.key;
    }

    @Override
    public boolean isApplicableTo(IValueContainer<?> valueContainer) {
        for (Processor<V, E> processor : this.processors) {
            if (processor.isApplicableTo(valueContainer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DataTransactionResult removeFrom(IValueContainer<?> valueContainer) {
        DataTransactionResult result = null;
        for (Processor<V, E> processor : this.processors) {
            result = processor.removeFrom(valueContainer);
            if (result.isSuccessful()) {
                return result;
            }
        }
        return result == null ? DataTransactionResult.failNoData() : result;
    }

    @Override
    public boolean removeFastFrom(IValueContainer<?> valueContainer) {
        for (Processor<V, E> processor : this.processors) {
            if (processor.removeFastFrom(valueContainer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DataTransactionResult offerTo(IValueContainer<?> valueContainer, E element) {
        DataTransactionResult result = null;
        for (Processor<V, E> processor : this.processors) {
            result = processor.offerTo(valueContainer, element);
            if (result.isSuccessful()) {
                return result;
            }
        }
        return result == null ? DataTransactionResult.failNoData() : result;
    }

    @Override
    public boolean offerFastTo(IValueContainer<?> valueContainer, E element) {
        for (Processor<V, E> processor : this.processors) {
            if (processor.offerFastTo(valueContainer, element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DataTransactionResult offerTo(IValueContainer<?> valueContainer, V value) {
        DataTransactionResult result = null;
        for (Processor<V, E> processor : this.processors) {
            result = processor.offerTo(valueContainer, value);
            if (result.isSuccessful()) {
                return result;
            }
        }
        return result == null ? DataTransactionResult.failNoData() : result;
    }

    @Override
    public boolean offerFastTo(IValueContainer<?> valueContainer, V value) {
        for (Processor<V, E> processor : this.processors) {
            if (processor.offerFastTo(valueContainer, value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<E> getFrom(IValueContainer<?> valueContainer) {
        for (Processor<V, E> processor : this.processors) {
            final Optional<E> opt = processor.getFrom(valueContainer);
            if (opt.isPresent()) {
                return opt;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<V> getValueFrom(IValueContainer<?> valueContainer) {
        for (Processor<V, E> processor : this.processors) {
            final Optional<V> opt = processor.getValueFrom(valueContainer);
            if (opt.isPresent()) {
                return opt;
            }
        }
        return Optional.empty();
    }

    @Override
    public V createValueFor(IValueContainer<?> valueContainer, E element) {
        if (this.processors.isEmpty()) {
            return LanternValueFactory.get().createValueForKey(getKey(), element);
        }
        return this.processors.get(0).createValueFor(valueContainer, element);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", this.key)
                .toString();
    }
}
