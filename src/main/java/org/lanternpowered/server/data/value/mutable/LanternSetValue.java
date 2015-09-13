package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternSetValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableSetValue;
import org.spongepowered.api.data.value.mutable.SetValue;

import java.util.Set;

public class LanternSetValue<E> extends LanternCollectionValue<E, Set<E>, SetValue<E>, ImmutableSetValue<E>> implements SetValue<E> {

    public LanternSetValue(Key<? extends BaseValue<Set<E>>> key) {
        this(key, Sets.newHashSet());
    }

    public LanternSetValue(Key<? extends BaseValue<Set<E>>> key, Set<E> actualValue) {
        this(key, Sets.newHashSet(), actualValue);
    }

    public LanternSetValue(Key<? extends BaseValue<Set<E>>> key, Set<E> defaultSet, Set<E> actualValue) {
        super(key, Sets.newHashSet(defaultSet), Sets.newHashSet(actualValue));
    }

    @Override
    public SetValue<E> transform(Function<Set<E>, Set<E>> function) {
        this.actualValue = Sets.newHashSet(checkNotNull(checkNotNull(function).apply(this.actualValue)));
        return this;
    }

    @Override
    public SetValue<E> filter(Predicate<? super E> predicate) {
        final Set<E> set = Sets.newHashSet();
        for (E element : this.actualValue) {
            if (checkNotNull(predicate).apply(element)) {
                set.add(element);
            }
        }
        return new LanternSetValue<E>(getKey(), set);
    }

    @Override
    public Set<E> getAll() {
        return Sets.newHashSet(this.actualValue);
    }

    @Override
    public ImmutableSetValue<E> asImmutable() {
        return new ImmutableLanternSetValue<E>(getKey(), ImmutableSet.copyOf(this.actualValue));
    }
}
