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
package org.lanternpowered.server.data;

import org.lanternpowered.server.data.key.KeyEventListener;
import org.lanternpowered.server.data.key.LanternKey;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.event.RegisteredListener;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

@SuppressWarnings("unchecked")
public final class CompositeValueStoreHelper {

    protected static Set<Key<?>> getKeys(DataTransactionResult result) {
        final Set<Key<?>> keys = new HashSet<>();
        result.getReplacedData().forEach(value -> keys.add(value.getKey()));
        result.getSuccessfulData().forEach(value -> keys.add(value.getKey()));
        // We don't need the rejected keys, they didn't modify any values
        return keys;
    }

    protected static boolean hasListeners(ICompositeValueStore store, Key<?> key) {
        return hasListeners(store, Collections.singleton(key));
    }

    protected static boolean hasListeners(ICompositeValueStore store, Iterable<Key<?>> keys) {
        if (!(store instanceof DataHolder)) {
            return false;
        }
        for (Key<?> key : keys) {
            final DataHolder dataHolder = (DataHolder) store;
            final List<RegisteredListener<ChangeDataHolderEvent.ValueChange>> listeners = ((LanternKey) key).getListeners();
            for (RegisteredListener<ChangeDataHolderEvent.ValueChange> listener : listeners) {
                if (((KeyEventListener) listener.getHandler()).getDataHolderPredicate().test(dataHolder)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static DataTransactionResult processDataTransactionResult(ICompositeValueStore store,
            DataTransactionResult result, BooleanSupplier hasListeners) {
        if (!(store instanceof DataHolder) || !result.isSuccessful() || !hasListeners.getAsBoolean()) {
            return result;
        }
        final Cause cause = CauseStack.currentOrEmpty().getCurrentCause();
        final ChangeDataHolderEvent.ValueChange event = SpongeEventFactory.createChangeDataHolderEventValueChange(cause, result, (DataHolder) store);
        Lantern.getGame().getEventManager().post(event);
        // Nothing is allowed to change, revert everything fast
        if (event.isCancelled()) {
            store.undoFastNoEvents(result);
            return DataTransactionResult.failNoData();
        }
        final DataTransactionResult original = result;
        result = event.getEndResult();
        // Check if something actually changed
        if (result != original) {
            final Map<Key<?>, ImmutableValue<?>> success = new HashMap<>();
            for (ImmutableValue<?> value : original.getSuccessfulData()) {
                success.put(value.getKey(), value);
            }
            for (ImmutableValue<?> value : result.getSuccessfulData()) {
                final ImmutableValue<?> value1 = success.remove(value.getKey());
                if (value1 == null || value1.get() != value.get()) {
                    store.offerNoEvents(value);
                }
            }
            // A previously successful offering got removed, revert this
            if (!success.isEmpty()) {
                for (ImmutableValue<?> value : original.getReplacedData()) {
                    if (success.containsKey(value.getKey())) {
                        store.offerNoEvents(value);
                    }
                }
            }
        }
        return event.getEndResult();
    }

    protected static <E, H extends ValueContainer<?>> boolean offerFast(ICompositeValueStore<?, H> store, Key<? extends BaseValue<E>> key, E element) {
        final boolean hasListeners = hasListeners(store, key);
        if (hasListeners) {
            return offer(store, key, element, () -> true).isSuccessful();
        }
        return store.offerFastNoEvents(key, element);
    }

    protected static <E, H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            Key<? extends BaseValue<E>> key, E element) {
        return offer(store, key, element, () -> hasListeners(store, key));
    }

    protected static <E, H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            Key<? extends BaseValue<E>> key, E element, BooleanSupplier hasListeners) {
        return processDataTransactionResult(store, store.offerNoEvents(key, element), hasListeners);
    }

    protected static <E, H extends ValueContainer<?>> boolean offerFast(ICompositeValueStore<?, H> store, BaseValue<E> value) {
        final boolean hasListeners = hasListeners(store, value.getKey());
        if (hasListeners) {
            return offer(store, value, () -> true).isSuccessful();
        }
        return store.offerFastNoEvents(value);
    }

    protected static <E, H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store, BaseValue<E> value) {
        return offer(store, value, () -> hasListeners(store, value.getKey()));
    }

    protected static <E, H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            BaseValue<E> value, BooleanSupplier hasListeners) {
        return processDataTransactionResult(store, store.offerNoEvents(value), hasListeners);
    }

    protected static <H extends ValueContainer<?>> boolean removeFast(ICompositeValueStore<?, H> store, Key<?> key) {
        final boolean hasListeners = hasListeners(store, key);
        if (hasListeners) {
            return remove(store, key, () -> true).isSuccessful();
        }
        return store.removeFastNoEvents(key);
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult remove(ICompositeValueStore<?, H> store, Key<?> key) {
        return remove(store, key, () -> hasListeners(store, key));
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult remove(ICompositeValueStore<?, H> store,
            Key<?> key, BooleanSupplier hasListeners) {
        return processDataTransactionResult(store, store.removeNoEvents(key), hasListeners);
    }

    protected static <H extends ValueContainer<?>> boolean undoFast(ICompositeValueStore<?, H> store, DataTransactionResult result) {
        final boolean hasListeners = hasListeners(store, getKeys(result));
        if (hasListeners) {
            return undo(store, result, () -> true).isSuccessful();
        }
        return store.undoFastNoEvents(result);
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult undo(ICompositeValueStore<?, H> store, DataTransactionResult result) {
        return undo(store, result, () -> hasListeners(store, getKeys(result)));
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult undo(ICompositeValueStore<?, H> store,
            DataTransactionResult result, BooleanSupplier hasListeners) {
        return processDataTransactionResult(store, store.undoNoEvents(result), hasListeners);
    }

    protected static <H extends ValueContainer<?>> boolean offerFast(ICompositeValueStore<?, H> store, H valueContainer, MergeFunction function) {
        final CompositeValueStore store1 = store; // Leave this, the compiler complains
        if (store1 instanceof DataHolder) {
            final boolean hasListeners = hasListeners(store, store.getKeys());
            if (hasListeners) {
                return offer(store, valueContainer, function, () -> true).isSuccessful();
            }
        }
        return store.offerFastNoEvents(valueContainer, function);
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            H valueContainer, MergeFunction function) {
        return offer(store, valueContainer, function, () -> hasListeners(store, valueContainer.getKeys()));
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            H valueContainer, MergeFunction function, BooleanSupplier hasListeners) {
        return processDataTransactionResult(store, store.offerNoEvents(valueContainer, function), hasListeners);
    }

    protected static <H extends ValueContainer<?>> boolean offerFast(ICompositeValueStore<?, H> store,
            Iterable<H> valueContainers, MergeFunction function) {
        final CompositeValueStore store1 = store; // Leave this, the compiler complains
        if (store1 instanceof DataHolder) {
            final Set<Key<?>> keys = new HashSet<>();
            for (H valueContainer : valueContainers) {
                keys.addAll(valueContainer.getKeys());
            }
            final boolean hasListeners = hasListeners(store, keys);
            if (hasListeners) {
                return offer(store, valueContainers, function, () -> true).isSuccessful();
            }
        }
        return store.offerFastNoEvents(valueContainers, function);
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            Iterable<H> valueContainers, MergeFunction function) {
        return offer(store, valueContainers, function, () -> {
            final Set<Key<?>> keys = new HashSet<>();
            for (H valueContainer : valueContainers) {
                keys.addAll(valueContainer.getKeys());
            }
            return hasListeners(store, keys);
        });
    }

    protected static <H extends ValueContainer<?>> DataTransactionResult offer(ICompositeValueStore<?, H> store,
            Iterable<H> valueContainers, MergeFunction function, BooleanSupplier hasListeners) {
        return processDataTransactionResult(store, store.offerNoEvents(valueContainers, function), hasListeners);
    }

    private CompositeValueStoreHelper() {
    }
}
