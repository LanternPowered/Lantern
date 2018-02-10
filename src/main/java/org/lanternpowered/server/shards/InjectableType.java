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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.shards.internal.inject.LanternInjectableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

import javax.annotation.Nullable;

public interface InjectableType<T> extends AnnotatedElement {

    /**
     * Creates a new {@link InjectableType} with the
     * given {@link TypeToken}.
     *
     * @param type The type token
     * @param <T> The type
     * @return The injectable type
     */
    static <T> InjectableType<T> of(TypeToken<T> type) {
        checkNotNull(type, "type");
        return new LanternInjectableType<>(type);
    }

    /**
     * Creates a new {@link InjectableType} with the
     * given {@link TypeToken} and {@link Annotation}s.
     *
     * @param type The type token
     * @param annotations The annotations
     * @param <T> The type
     * @return The injectable type
     */
    static <T> InjectableType<T> of(TypeToken<T> type, Annotation[] annotations) {
        checkNotNull(type, "type");
        checkNotNull(annotations, "annotations");
        return new LanternInjectableType<>(type, Arrays.copyOf(annotations, annotations.length));
    }

    /**
     * Gets the {@link TypeToken}.
     *
     * @return The type token
     */
    TypeToken<T> getType();

    @Nullable
    @Override
    <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    /**
     * {@inheritDoc}
     *
     * @deprecated Declared annotations are not supported by {@link InjectableType}s.
     */
    @Nullable
    @Override
    @Deprecated
    <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass);
}
