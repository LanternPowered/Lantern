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
package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.lanternpowered.server.data.meta.LanternPatternLayer;
import org.lanternpowered.server.data.value.mutable.LanternPatternListValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutablePatternListValue;
import org.spongepowered.api.data.value.mutable.PatternListValue;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ImmutableLanternPatternListValue extends ImmutableLanternListValue<PatternLayer> implements ImmutablePatternListValue {

    public ImmutableLanternPatternListValue(Key<? extends BaseValue<List<PatternLayer>>> key, List<PatternLayer> actualValue) {
        super(key, ImmutableList.copyOf(actualValue));
    }

    public ImmutableLanternPatternListValue(Key<? extends BaseValue<List<PatternLayer>>> key, List<PatternLayer> defaultValue,
            List<PatternLayer> actualValue) {
        super(key, ImmutableList.copyOf(defaultValue), ImmutableList.copyOf(actualValue));
    }

    @Override
    public ImmutablePatternListValue with(List<PatternLayer> value) {
        return new ImmutableLanternPatternListValue(this.getKey(), checkNotNull(value));
    }

    @Override
    public ImmutablePatternListValue transform(Function<List<PatternLayer>, List<PatternLayer>> function) {
        return new ImmutableLanternPatternListValue(this.getKey(), checkNotNull(checkNotNull(function).apply(this.actualValue)));
    }

    @Override
    public PatternListValue asMutable() {
        return new LanternPatternListValue(this.getKey(), this.getDefault(), this.actualValue);
    }

    @Override
    public ImmutablePatternListValue withElement(PatternLayer element) {
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(),
                ImmutableList.<PatternLayer>builder().addAll(this.actualValue).add(element).build());
    }

    @Override
    public ImmutablePatternListValue withAll(Iterable<PatternLayer> elements) {
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(),
                ImmutableList.<PatternLayer>builder().addAll(this.actualValue).addAll(elements).build());
    }

    @Override
    public ImmutablePatternListValue without(PatternLayer element) {
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), this.actualValue.stream()
                .filter(existingElement -> !existingElement.equals(element))
                .collect(ImmutableList.toImmutableList()));
    }

    @Override
    public ImmutablePatternListValue withoutAll(Iterable<PatternLayer> elements) {
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), this.actualValue.stream()
                .filter(existingElement -> !Iterables.contains(elements, existingElement))
                .collect(ImmutableList.toImmutableList()));
    }

    @Override
    public ImmutablePatternListValue withoutAll(Predicate<PatternLayer> predicate) {
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), this.actualValue.stream()
                .filter(existing -> checkNotNull(predicate).test(existing))
                .collect(ImmutableList.toImmutableList()));
    }

    @Override
    public ImmutablePatternListValue with(int index, PatternLayer value) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (final ListIterator<PatternLayer> iterator = this.actualValue.listIterator(); iterator.hasNext(); ) {
            if (iterator.nextIndex() - 1 == index) {
                builder.add(checkNotNull(value));
                iterator.next();
            } else {
                builder.add(iterator.next());
            }
        }
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), builder.build());
    }

    @Override
    public ImmutablePatternListValue with(int index, Iterable<PatternLayer> values) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (final ListIterator<PatternLayer> iterator = this.actualValue.listIterator(); iterator.hasNext(); ) {
            if (iterator.nextIndex() -1 == index) {
                builder.addAll(values);
            }
            builder.add(iterator.next());
        }
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), builder.build());
    }

    @Override
    public ImmutablePatternListValue without(int index) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (final ListIterator<PatternLayer> iterator = this.actualValue.listIterator(); iterator.hasNext(); ) {
            if (iterator.nextIndex() - 1 != index) {
                builder.add(iterator.next());
            }
        }
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), builder.build());
    }

    @Override
    public ImmutablePatternListValue set(int index, PatternLayer element) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (final ListIterator<PatternLayer> iterator = this.actualValue.listIterator(); iterator.hasNext(); ) {
            if (iterator.nextIndex() - 1 == index) {
                builder.add(checkNotNull(element));
                iterator.next();
            } else {
                builder.add(iterator.next());
            }
        }
        return new ImmutableLanternPatternListValue(this.getKey(), this.getDefault(), builder.build());
   }

    @Override
    public ImmutablePatternListValue with(BannerPatternShape patternShape, DyeColor color) {
        return this.withElement(new LanternPatternLayer(patternShape, color));
    }

    @Override
    public ImmutablePatternListValue with(int index, BannerPatternShape patternShape, DyeColor color) {
        return this.with(index, new LanternPatternLayer(patternShape, color));
    }

    @Override
    public ImmutablePatternListValue set(int index, BannerPatternShape patternShape, DyeColor color) {
        return this.set(index, new LanternPatternLayer(patternShape, color));
    }
}
