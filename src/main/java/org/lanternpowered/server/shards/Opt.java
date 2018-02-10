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
package org.lanternpowered.server.shards;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * A {@link Supplier} that optionally gets a value of the type {@link T}.
 * <p>
 * This interface should be inside a {@link Shard} for the injection
 * of optional values ({@link Shard}s) and being able to retrieve them.
 * For example:
 * <pre>
 * {@code
 * class FooComponent extends AbstractComponent {
 *
 *     @Inject Opt<BarComponent> optBarComponent;
 *
 *     // This @Holder will be provided if the holder this component
 *     // is attached to this class extends. The type of the {@link ShardHolder }
 *     @Inject Opt<FooComponentHolder> optFooComponentHolder;
 *
 *     public void pulse() {
 *         optBarComponent.ifPresent(bar -> {
 *             bar.doStuff();
 *         });
 *     }
 * }
 * }
 * </pre>
 * This can also be applied to {@link ShardHolder} injection to allow
 * injection if the holder the target class implements.
 * For example:
 * <pre>
 * {@code
 * class FooComponent extends AbstractComponent {
 *
 *     @Inject @Holder Opt<FooComponentHolder> optFooComponentHolder;
 *
 *     public void pulse() {
 *         optFooComponentHolder.ifPresent(holder -> {
 *             holder.doStuff();
 *         });
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> The component type
 */
@SuppressWarnings("unchecked")
@FunctionalInterface
public interface Opt<T> extends Supplier<T> {

    /**
     * Returns an empty {@link Opt} instance, no value is present.
     *
     * @return An empty {@link Opt}
     */
    static <T> Opt<T> empty() {
        return OptImpl.EMPTY;
    }

    /**
     * Returns an {@link Opt} with the specified non-null value.
     *
     * @param value The value
     * @return An {@link Opt} with the value present
     */
    static <T> Opt<T> of(T value) {
        requireNonNull(value);
        return () -> value;
    }

    /**
     * Returns an {@link Opt} with the specified value if non-null,
     * otherwise no value is present.
     *
     * @param value The value
     * @return An {@link Opt} with the value if present, otherwise {@link #empty()}
     */
    static <T> Opt<T> ofNullable(@Nullable T value) {
        return value == null ? empty() : of(value);
    }

    @Nullable
    T getOrNull();

    /**
     * Gets the value if present, otherwise throws {@link NoSuchElementException}.
     *
     * @return The value
     * @throws NoSuchElementException If there is no value present
     */
    @Override
    default T get() throws NoSuchElementException {
        final T value = getOrNull();
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Gets whether the returned value (through {@link #get()})
     * is present (isn't {@code null}).
     *
     * @return Is present
     */
    default boolean isPresent() {
        return getOrNull() != null;
    }

    /**
     * Gets the value as a {@link Optional}.
     *
     * @return The optional
     */
    default Optional<T> asOptional() {
        return Optional.ofNullable(getOrNull());
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer The consumer to execute
     */
    default void ifPresent(Consumer<? super T> consumer) {
        final T value = getOrNull();
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other The value to be returned if there is no value present
     * @return The value if present, otherwise the other value
     */
    default T orElse(T other) {
        final T value = getOrNull();
        return value != null ? value : other;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other A supplier whose result is returned if there is no value present
     * @return The value if present, otherwise the result of the supplier
     */
    default T orElseGet(Supplier<? extends T> other) {
        final T value = getOrNull();
        return value != null ? value : other.get();
    }

    /**
     * Return the value if present, otherwise throw the
     * {@link Throwable} that is provided by the {@link Supplier}.
     *
     * @param exceptionSupplier The exception supplier
     * @param <X> The exception type
     * @return The value
     * @throws X The exception
     */
    default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        final T value = getOrNull();
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }
}
