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

    @SuppressWarnings("unchecked")
    @Override
    public ImmutableSetValue<E> with(E... elements) {
        return new ImmutableLanternSetValue<E>(getKey(), ImmutableSet.<E>builder().addAll(this.actualValue).add(elements).build());
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
