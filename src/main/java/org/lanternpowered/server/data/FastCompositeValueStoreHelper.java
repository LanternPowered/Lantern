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

import org.lanternpowered.server.transformer.data.FastValueContainerClassTransformer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.CompositeValueStore;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.data.value.ValueContainer;

import java.util.function.Function;

/**
 * This helper class will attempt to use fast {@link CompositeValueStore} methods
 * if they are available for the given {@link CompositeValueStore}.
 * <p>
 * WARNING: DO NOT MODIFY THE METHOD SIGNATURES UNLESS YOU KNOW WHAT YOU ARE DOING,
 * they are closely linked to the {@link FastValueContainerClassTransformer}, modifying
 * these may break the class transformer.
 */
@SuppressWarnings("unchecked")
public final class FastCompositeValueStoreHelper {

    public static <E> boolean transform(CompositeValueStore<?,?> store, Key<? extends Value<E>> key, Function<E, E> function) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).transformFast(key, function) :
                store.transform(key, function).isSuccessful();
    }

    public static <E> boolean offer(CompositeValueStore<?,?> store, Key<? extends Value<E>> key, E element) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).offerFast(key, element) :
                store.offer(key, element).isSuccessful();
    }

    public static <E> boolean offer(CompositeValueStore<?,?> store, Value<E> value) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).offerFast(value) :
                store.offer(value).isSuccessful();
    }

    public static <E> boolean tryOffer(CompositeValueStore<?,?> store, Key<? extends Value<E>> key, E value) throws IllegalArgumentException {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).tryOfferFast(key, value) :
                store.tryOffer(key, value).isSuccessful();
    }

    public static <E> boolean tryOffer(CompositeValueStore<?,?> store, Value<E> value) throws IllegalArgumentException {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).tryOfferFast(value) :
                store.tryOffer(value).isSuccessful();
    }

    public static boolean remove(CompositeValueStore<?,?> store, Key<?> key) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).removeFast(key) :
                store.remove(key).isSuccessful();
    }

    public static boolean remove(CompositeValueStore<?,?> store, Value<?> value) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).removeFast(value) :
                store.remove(value).isSuccessful();
    }

    public static boolean undo(CompositeValueStore<?,?> store, DataTransactionResult result) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?,?>) store).undoFast(result) :
                store.undo(result).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean offer(CompositeValueStore<?, H> store, H valueContainer, MergeFunction function) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).offerFast(valueContainer, function) :
                store.offer(valueContainer, function).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean offer(CompositeValueStore<?, H> store, H valueContainer) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).offerFast(valueContainer) :
                store.offer(valueContainer).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean offer(CompositeValueStore<?, H> store, Iterable<H> valueContainers) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).offerFast(valueContainers) :
                store.offer(valueContainers).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean offer(CompositeValueStore<?, H> store, Iterable<H> valueContainers,
            MergeFunction function) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).offerFast(valueContainers, function) :
                store.offer(valueContainers, function).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean tryOffer(CompositeValueStore<?, H> store, H valueContainer) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).tryOfferFast(valueContainer) :
                store.tryOffer(valueContainer).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean tryOffer(CompositeValueStore<?, H> store, H valueContainer,
            MergeFunction function) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).tryOfferFast(valueContainer, function) :
                store.tryOffer(valueContainer, function).isSuccessful();
    }

    public static <H extends ValueContainer<?>> boolean remove(CompositeValueStore<?, H> store, Class<? extends H> containerClass) {
        return store instanceof ICompositeValueStore ? ((ICompositeValueStore<?, H>) store).removeFast(containerClass) :
                store.remove(containerClass).isSuccessful();
    }

    private FastCompositeValueStoreHelper() {
    }
}
