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
package org.lanternpowered.server.shards.internal.inject;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.shards.InjectableType;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public final class LanternInjectableType<T> implements InjectableType<T> {

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    private final TypeToken<T> type;
    private final Annotation[] annotations;

    private int hashCode;

    public LanternInjectableType(TypeToken<T> type) {
        this(type, EMPTY_ANNOTATIONS);
    }

    public LanternInjectableType(TypeToken<T> type, Annotation[] annotations) {
        this.annotations = annotations;
        this.type = type;
    }

    @Override
    public TypeToken<T> getType() {
        return this.type;
    }

    @Nullable
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return (A) Arrays.stream(this.annotations).filter(annotationClass::isInstance).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        return null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations.length == 0 ? EMPTY_ANNOTATIONS : Arrays.copyOf(this.annotations, this.annotations.length);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return EMPTY_ANNOTATIONS;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LanternInjectableType)) {
            return false;
        }
        final LanternInjectableType o = (LanternInjectableType) obj;
        return o.type.equals(this.type) && Arrays.equals(o.annotations, this.annotations);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Objects.hash(this.type, this.annotations);
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.type)
                .add("annotations", Arrays.toString(this.annotations))
                .toString();
    }
}
