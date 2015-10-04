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

    @Override
    public ImmutablePatternListValue with(List<PatternLayer> value) {
        return new ImmutableLanternPatternListValue(getKey(), ImmutableList.copyOf(checkNotNull(value)));
    }

    @Override
    public ImmutablePatternListValue transform(Function<List<PatternLayer>, List<PatternLayer>> function) {
        return new ImmutableLanternPatternListValue(getKey(), ImmutableList.copyOf(checkNotNull(checkNotNull(function).apply(this.actualValue))));
    }

    @Override
    public PatternListValue asMutable() {
        final List<PatternLayer> list = Lists.newArrayList();
        list.addAll(this.actualValue);
        return new LanternPatternListValue(getKey(), list);
    }

    @Override
    public ImmutablePatternListValue with(PatternLayer... elements) {
        return new ImmutableLanternPatternListValue(getKey(), ImmutableList.<PatternLayer>builder().addAll(this.actualValue).add(elements).build());
    }

    @Override
    public ImmutablePatternListValue withAll(Iterable<PatternLayer> elements) {
        return new ImmutableLanternPatternListValue(getKey(), ImmutableList.<PatternLayer>builder().addAll(this.actualValue).addAll(elements).build());
    }

    @Override
    public ImmutablePatternListValue without(PatternLayer element) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (PatternLayer existingElement : this.actualValue) {
            if (!existingElement.equals(element)) {
                builder.add(existingElement);
            }
        }
        return new ImmutableLanternPatternListValue(getKey(), builder.build());

    }

    @Override
    public ImmutablePatternListValue withoutAll(Iterable<PatternLayer> elements) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (PatternLayer existingElement : this.actualValue) {
            if (!Iterables.contains(elements, existingElement)) {
                builder.add(existingElement);
            }
        }
        return new ImmutableLanternPatternListValue(getKey(), builder.build());
    }

    @Override
    public ImmutablePatternListValue withoutAll(Predicate<PatternLayer> predicate) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (PatternLayer existing : this.actualValue) {
            if (checkNotNull(predicate).test(existing)) {
                builder.add(existing);
            }
        }
        return new ImmutableLanternPatternListValue(getKey(), builder.build());
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
        return new ImmutableLanternPatternListValue(getKey(), builder.build());
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
        return new ImmutableLanternPatternListValue(getKey(), builder.build());
    }

    @Override
    public ImmutablePatternListValue without(int index) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (final ListIterator<PatternLayer> iterator = this.actualValue.listIterator(); iterator.hasNext(); ) {
            if (iterator.nextIndex() - 1 != index) {
                builder.add(iterator.next());
            }
        }
        return new ImmutableLanternPatternListValue(getKey(), builder.build());
    }

    @Override
    public ImmutablePatternListValue set(int index, PatternLayer element) {
        final ImmutableList.Builder<PatternLayer> builder = ImmutableList.builder();
        for (final ListIterator<PatternLayer> iterator = this.actualValue.listIterator(); iterator.hasNext(); ) {
            if (iterator.nextIndex() -1 == index) {
                builder.add(checkNotNull(element));
                iterator.next();
            } else {
                builder.add(iterator.next());
            }
        }
        return new ImmutableLanternPatternListValue(getKey(), builder.build());
   }

    @Override
    public ImmutablePatternListValue with(BannerPatternShape patternShape, DyeColor color) {
        return with(new LanternPatternLayer(patternShape, color));
    }

    @Override
    public ImmutablePatternListValue with(int index, BannerPatternShape patternShape, DyeColor color) {
        return with(index, new LanternPatternLayer(patternShape, color));
    }

    @Override
    public ImmutablePatternListValue set(int index, BannerPatternShape patternShape, DyeColor color) {
        return set(index, new LanternPatternLayer(patternShape, color));
    }
}
