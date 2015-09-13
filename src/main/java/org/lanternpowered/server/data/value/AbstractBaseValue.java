package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;

public abstract class AbstractBaseValue<E> implements BaseValue<E> {

    private final Key<? extends BaseValue<E>> key;
    private final E defaultValue;

    protected E actualValue;

    public AbstractBaseValue(Key<? extends BaseValue<E>> key, E defaultValue) {
        this.key = checkNotNull(key);
        this.defaultValue = checkNotNull(defaultValue);
        this.actualValue = defaultValue;
    }

    public AbstractBaseValue(Key<? extends BaseValue<E>> key, E defaultValue, E actualValue) {
        this.key = checkNotNull(key);
        this.defaultValue = checkNotNull(defaultValue);
        this.actualValue = checkNotNull(actualValue);
    }

    @Override
    public E get() {
        return this.actualValue == null ? this.defaultValue : this.actualValue;
    }

    @Override
    public boolean exists() {
        return this.actualValue != null;
    }

    @Override
    public E getDefault() {
        return this.defaultValue;
    }

    @Override
    public Optional<E> getDirect() {
        return Optional.fromNullable(this.actualValue);
    }

    @Override
    public Key<? extends BaseValue<E>> getKey() {
        return this.key;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.key, this.defaultValue, this.actualValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractBaseValue<?> other = (AbstractBaseValue<?>) obj;
        return Objects.equal(this.key, other.key)
               && Objects.equal(this.defaultValue, other.defaultValue)
               && Objects.equal(this.actualValue, other.actualValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("key", this.key)
            .add("defaultValue", this.defaultValue)
            .add("actualValue", this.actualValue)
            .toString();
    }
}
