/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.lanternpowered.server.data.value.mutable.LanternSetValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableSetValue;
import org.spongepowered.api.data.value.mutable.SetValue;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class ImmutableLanternSetValue<E> extends ImmutableLanternCollectionValue<E, Set<E>, ImmutableSetValue<E>, SetValue<E>>
    implements ImmutableSetValue<E> {

    public ImmutableLanternSetValue(Key<? extends BaseValue<Set<E>>> key) {
        super(key, ImmutableSet.<E>of());
    }

    public ImmutableLanternSetValue(Key<? extends BaseValue<Set<E>>> key, Set<E> actualValue) {
        super(key, ImmutableSet.<E>of(), actualValue);
    }

    @Override

    public ImmutableSetValue<E> with(Set<E> value) {
        return new ImmutableLanternSetValue<E>(getKey(), ImmutableSet.copyOf(value));
    }

    @Override
    public ImmutableSetValue<E> transform(Function<Set<E>, Set<E>> function) {
        return new ImmutableLanternSetValue<E>(getKey(), checkNotNull(checkNotNull(function).apply(this.actualValue)));
    }

    @Override
    public ImmutableSetValue<E> withElement(E element) {
        return new ImmutableLanternSetValue<E>(getKey(), ImmutableSet.<E>builder().addAll(this.actualValue).add(element).build());
    }

    @Override
    public ImmutableSetValue<E> withAll(Iterable<E> elements) {
        return new ImmutableLanternSetValue<E>(getKey(), ImmutableSet.<E>builder().addAll(this.actualValue).addAll(elements).build());
    }

    @Override
    public ImmutableSetValue<E> without(E element) {
        final ImmutableSet.Builder<E> builder = ImmutableSet.builder();
        for (E existing : this.actualValue) {
            if (!existing.equals(element)) {
                builder.add(existing);
            }
        }
        return new ImmutableLanternSetValue<E>(getKey(), builder.build());
    }

    @Override
    public ImmutableSetValue<E> withoutAll(Iterable<E> elements) {
        final ImmutableSet.Builder<E> builder = ImmutableSet.builder();
        for (E existingElement : this.actualValue) {
            if (!Iterables.contains(elements, existingElement)) {
                builder.add(existingElement);
            }
        }
        return new ImmutableLanternSetValue<E>(getKey(), builder.build());
    }

    @Override
    public ImmutableSetValue<E> withoutAll(Predicate<E> predicate) {
        final ImmutableSet.Builder<E> builder = ImmutableSet.builder();
        for (E existingElement : this.actualValue) {
            if (checkNotNull(predicate).test(existingElement)) {
                builder.add(existingElement);
            }
        }
        return new ImmutableLanternSetValue<E>(getKey(), builder.build());
    }

    @Override
    public Set<E> getAll() {
        final Set<E> set = Sets.newHashSet();
        set.addAll(this.actualValue);
        return set;
    }

    @Override
    public SetValue<E> asMutable() {
        final Set<E> set = Sets.newHashSet();
        set.addAll(this.actualValue);
        return new LanternSetValue<E>(getKey(), set);
    }
}
