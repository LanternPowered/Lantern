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
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Equivalence;
import com.google.common.collect.ForwardingQueue;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

@NonnullByDefault
public final class UniqueEvictingQueue<E> extends ForwardingQueue<E> {

    /**
     * Creates a new {@link UniqueEvictingQueue} with the specified maximum size (capacity) of
     * the queue. The {@link Equivalence} will default in this method to {@link Equivalence#identity}.
     *
     * @param maxSize the maximum size
     * @param <E> the type of the element
     * @return the unique evicting queue
     */
    public static <E> UniqueEvictingQueue<E> create(int maxSize) {
        return new UniqueEvictingQueue<>(new ArrayDeque<>(maxSize), (Equivalence) Equivalence.identity(), maxSize);
    }

    /**
     * Creates a new {@link UniqueEvictingQueue} with the specified {@link Equivalence} to match
     * duplicate entries and the maximum size (capacity) of the queue.
     *
     * @param equivalence the equivalence
     * @param maxSize the maximum size
     * @param <E> the type of the element
     * @return the unique evicting queue
     */
    public static <E> UniqueEvictingQueue<E> create(Equivalence<E> equivalence, int maxSize) {
        return new UniqueEvictingQueue<>(new LinkedBlockingDeque<>(maxSize), equivalence, maxSize);
    }

    /**
     * Creates a new concurrent {@link UniqueEvictingQueue} with the specified maximum size (capacity) of
     * the queue. The {@link Equivalence} will default in this method to {@link Equivalence#identity}.
     *
     * @param maxSize the maximum size
     * @param <E> the type of the element
     * @return the unique evicting queue
     */
    public static <E> UniqueEvictingQueue<E> createConcurrent(int maxSize) {
        return new UniqueEvictingQueue<>(new LinkedBlockingDeque<>(maxSize), (Equivalence) Equivalence.identity(), maxSize);
    }

    /**
     * Creates a new concurrent {@link UniqueEvictingQueue} with the specified {@link Equivalence} to match
     * duplicate entries and the maximum size (capacity) of the queue.
     *
     * @param equivalence the equivalence
     * @param maxSize the maximum size
     * @param <T> the type of the element
     * @return the unique evicting queue
     */
    public static <E> UniqueEvictingQueue<E> createConcurrent(Equivalence<E> equivalence, int maxSize) {
        return new UniqueEvictingQueue<>(new ArrayDeque<>(maxSize), equivalence, maxSize);
    }

    private final Queue<E> delegate;
    private final Equivalence<E> equivalence;
    private final int maxSize;

    private UniqueEvictingQueue(Queue<E> delegate, Equivalence<E> equivalence, int maxSize) {
        this.equivalence = equivalence;
        this.delegate = delegate;
        this.maxSize = maxSize;
    }

    @Override
    protected Queue<E> delegate() {
        return this.delegate;
    }

    @Override
    public boolean offer(E element) {
        return this.add(element);
    }

    @Override
    public boolean add(E element) {
        checkNotNull(element, "element");
        if (this.maxSize == 0) {
            return true;
        }
        // Make sure that there is no duplicate entry
        this.delegate.removeIf(other -> this.equivalence.equivalent(element, other));
        // Remove the oldest entry if the capacity limit is exceeded
        if (this.size() == this.maxSize) {
            this.delegate.remove();
        }
        // Add the new element
        this.delegate.add(element);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return this.standardAddAll(collection);
    }

    @Override
    public boolean contains(Object element) {
        return delegate().contains(checkNotNull(element, "element"));
    }

    @Override
    public boolean remove(Object element) {
        return delegate().remove(checkNotNull(element, "element"));
    }

}
