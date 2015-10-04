package org.lanternpowered.server.data.value.mutable;

import com.google.common.collect.ImmutableList;

import org.lanternpowered.server.data.meta.LanternPatternLayer;
import org.lanternpowered.server.data.value.immutable.ImmutableLanternPatternListValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutablePatternListValue;
import org.spongepowered.api.data.value.mutable.PatternListValue;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class LanternPatternListValue extends LanternListValue<PatternLayer> implements PatternListValue {

    public LanternPatternListValue(Key<? extends BaseValue<List<PatternLayer>>> key) {
        super(key);
    }

    public LanternPatternListValue(Key<? extends BaseValue<List<PatternLayer>>> key, List<PatternLayer> actualValue) {
        super(key, actualValue);
    }

    @Override
    public PatternListValue set(List<PatternLayer> value) {
        super.set(value);
        return this;
    }

    @Override
    public PatternListValue filter(Predicate<? super PatternLayer> predicate) {
        super.filter(predicate);
        return this;
    }

    @Override
    public PatternListValue add(BannerPatternShape patternShape, DyeColor color) {
        super.add(new LanternPatternLayer(patternShape, color));
        return this;
    }

    @Override
    public PatternListValue add(int index, BannerPatternShape patternShape, DyeColor color) {
        return add(index, new LanternPatternLayer(patternShape, color));
    }

    @Override
    public PatternListValue add(int index, PatternLayer value) {
        super.add(index, value);
        return this;
    }

    @Override
    public PatternListValue add(int index, Iterable<PatternLayer> values) {
        super.add(index, values);
        return this;
    }

    @Override
    public PatternListValue remove(int index) {
        super.remove(index);
        return this;
    }

    @Override
    public PatternListValue set(int index, PatternLayer element) {
        super.set(index, element);
        return this;
    }

    @Override
    public PatternListValue transform(Function<List<PatternLayer>, List<PatternLayer>> function) {
        super.transform(function);
        return this;
    }

    @Override
    public PatternListValue add(PatternLayer element) {
        super.add(element);
        return this;
    }

    @Override
    public PatternListValue addAll(Iterable<PatternLayer> elements) {
        super.addAll(elements);
        return this;
    }

    @Override
    public PatternListValue remove(PatternLayer element) {
        super.remove(element);
        return this;
    }

    @Override
    public PatternListValue removeAll(Iterable<PatternLayer> elements) {
        super.removeAll(elements);
        return this;
    }

    @Override
    public PatternListValue removeAll(Predicate<PatternLayer> predicate) {
        super.removeAll(predicate);
        return this;
    }

    @Override
    public ImmutablePatternListValue asImmutable() {
        return new ImmutableLanternPatternListValue(getKey(), ImmutableList.copyOf(this.actualValue));
    }
}
