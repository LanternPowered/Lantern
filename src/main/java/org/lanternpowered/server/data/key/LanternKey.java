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
package org.lanternpowered.server.data.key;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.event.RegisteredListener;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternKey<V extends Value<?>> implements Key<V>, CatalogType {

    private static final TypeVariable<Class<Optional>> optionalType = Optional.class.getTypeParameters()[0];
    private static final TypeVariable<Class<Value>> elementType = Value.class.getTypeParameters()[0];

    private static final TypeToken<ChangeDataHolderEvent.ValueChange> valueChangeEventTypeToken =
            TypeToken.of(ChangeDataHolderEvent.ValueChange.class);

    private final TypeToken<V> valueToken;
    private final CatalogKey key;
    private final Translation name;
    private final DataQuery query;
    private final TypeToken<?> elementToken;
    private final List<RegisteredListener<ChangeDataHolderEvent.ValueChange>> listeners = new ArrayList<>();
    private final List<RegisteredListener<ChangeDataHolderEvent.ValueChange>> unmodifiableListeners = Collections.unmodifiableList(this.listeners);

    // Optional key related

    // This field will be set if this key has a Optional element,
    // then this will be the key with the unwrapped version
    @Nullable private LanternKey optionalUnwrappedKey;

    // This field will be set if this key has originally a Optional element,
    // then this will be the key with the original wrapped version
    @Nullable private LanternKey optionalWrappedKey;

    private final int hashCode;

    LanternKey(CatalogKey key, Translation name, DataQuery query, TypeToken<V> valueToken) {
        this.valueToken = valueToken;
        this.name = name;
        this.query = query;
        this.elementToken = this.valueToken.resolveType(Value.class.getTypeParameters()[0]);
        this.key = key;
        this.hashCode = Objects.hash(this.valueToken, this.key, this.name, this.query, this.elementToken);
        TypeToken<?> elementToken = valueToken.resolveType(elementType);
        if (Optional.class.isAssignableFrom(elementToken.getRawType())) {
            elementToken = elementToken.resolveType(optionalType);
            // Generate the unwrapped version
            final LanternKeyBuilder unwrappedBuilder = new LanternKeyBuilder();
            if (Value.Immutable.class.isAssignableFrom(valueToken.getRawType())) {
                unwrappedBuilder.valueToken = createImmutableValueToken(elementToken);
            } else {
                unwrappedBuilder.valueToken = createValueToken(elementToken);
            }
            unwrappedBuilder.key(CatalogKey.of(key.getNamespace(), key.getValue() + "_non_optional"));
            unwrappedBuilder.name(name.get() + "NonOptional");
            final List<String> parts = new ArrayList<>(query.getParts());
            final int index = parts.size() - 1;
            parts.set(index, parts.get(index) + "NonOptional");
            unwrappedBuilder.query = DataQuery.of(parts);

            this.optionalUnwrappedKey = (LanternKey) unwrappedBuilder.build();
            this.optionalUnwrappedKey.optionalWrappedKey = this;
        }
    }

    private static <E> TypeToken<Value.Mutable<E>> createValueToken(TypeToken<E> elementToken) {
        return new TypeToken<Value.Mutable<E>>() {}.where(new TypeParameter<E>() {}, elementToken);
    }

    private static <E> TypeToken<Value.Immutable<E>> createImmutableValueToken(TypeToken<E> elementToken) {
        return new TypeToken<Value.Immutable<E>>() {}.where(new TypeParameter<E>() {}, elementToken);
    }

    @Nullable
    public LanternKey getOptionalUnwrappedKey() {
        return this.optionalUnwrappedKey;
    }

    @Nullable
    public LanternKey getOptionalWrappedKey() {
        return this.optionalWrappedKey;
    }

    @Override
    public TypeToken<V> getValueToken() {
        return this.valueToken;
    }

    @Override
    public TypeToken<?> getElementToken() {
        return this.elementToken;
    }

    @Override
    public DataQuery getQuery() {
        return this.query;
    }

    @Override
    public <E extends DataHolder> void registerEvent(Class<E> holderFilter,
            EventListener<ChangeDataHolderEvent.ValueChange> listener) {
        checkNotNull(holderFilter, "holderFilter");
        checkNotNull(listener, "listener");
        final KeyEventListener keyEventListener = new KeyEventListener(listener, holderFilter::isInstance, this);
        final PluginContainer plugin = CauseStack.current().first(PluginContainer.class).get();
        final RegisteredListener<ChangeDataHolderEvent.ValueChange> registeredListener = Lantern.getGame().getEventManager().register(
                plugin, valueChangeEventTypeToken, Order.DEFAULT, keyEventListener);
        this.listeners.add(registeredListener);
    }

    /**
     * Gets all the {@link KeyEventListener}s.
     *
     * @return The listener entries
     */
    public List<RegisteredListener<ChangeDataHolderEvent.ValueChange>> getListeners() {
        return this.unmodifiableListeners;
    }

    @Override
    public CatalogKey getKey() {
        return this.key;
    }

    @Override
    public String getName() {
        return this.name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LanternKey<?> key = (LanternKey<?>) o;
        return Objects.equals(this.valueToken, key.valueToken) &&
                Objects.equals(this.key, key.key) &&
                Objects.equals(this.name, key.name) &&
                Objects.equals(this.query, key.query) &&
                Objects.equals(this.elementToken, key.elementToken);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("id", this.key)
                .add("valueToken", this.valueToken)
                .add("elementToken", this.elementToken)
                .add("query", this.query)
                .toString();
    }
}
