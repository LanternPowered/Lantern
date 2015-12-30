/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.inject;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public final class MethodSpec<T> {

    private final Class<? extends T> returnType;
    private final List<Class<?>> parameterTypes;
    private final List<Class<? extends Annotation>> annotationTypes;

    private volatile Integer hashCode;

    private MethodSpec(Class<? extends T> returnType, List<Class<?>> parameterTypes,
            List<Class<? extends Annotation>> annotations) {
        this.parameterTypes = parameterTypes;
        this.annotationTypes = annotations;
        this.returnType = returnType;
    }

    /**
     * Gets the return type of the method spec.
     * 
     * @return the return type
     */
    public Class<? extends T> getReturnType() {
        return this.returnType;
    }

    /**
     * Gets the parameter types of the method spec.
     * 
     * @return the parameter types
     */
    public List<Class<?>> getParameterTypes() {
        return this.parameterTypes;
    }

    /**
     * Gets the annotation types of the method spec.
     * 
     * @return the annotation types
     */
    public List<Class<? extends Annotation>> getAnnotationTypes() {
        return this.annotationTypes;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        MethodSpec<?> o = (MethodSpec<?>) other;
        return o.returnType == this.returnType && this.parameterTypes.equals(o.parameterTypes) &&
                this.annotationTypes.equals(o.annotationTypes);
    }

    @Override
    public int hashCode() {
        if (this.hashCode != null) {
            return this.hashCode;
        }
        return this.hashCode = new HashCodeBuilder(17, 37)
                .append(this.returnType)
                .append(this.parameterTypes)
                .append(this.annotationTypes)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(this.returnType)
                .append(this.parameterTypes)
                .append(this.annotationTypes)
                .build();
    }

    @SuppressWarnings("unchecked")
    public static <T> MethodSpec<T> of(Class<? extends T> returnType) {
        return ofAnnotated(returnType, new Class[0], new Class[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T> MethodSpec<T> of(Class<? extends T> returnType, Class<?>[] parameterTypes) {
        return ofAnnotated(returnType, parameterTypes, new Class[0]);
    }
            
    public static <T> MethodSpec<T> ofAnnotated(Class<? extends T> returnType, Class<?>[] parameterTypes,
            Class<? extends Annotation>[] annotationTypes) {
        checkNotNull(returnType, "returnType");
        checkNotNull(parameterTypes, "parameterTypes");
        checkNotNull(annotationTypes, "annotationTypes");
        List<Class<?>> params = Lists.newArrayList(parameterTypes);
        params.forEach(param -> checkNotNull(param, "parameterType"));
        List<Class<? extends Annotation>> annos = Lists.newArrayList(annotationTypes);
        annos.forEach(anno -> checkNotNull(anno, "annotationType"));
        Collections.sort(annos, (o1, o2) -> o1.hashCode() - o2.hashCode());
        return new MethodSpec<>(returnType, ImmutableList.copyOf(params), ImmutableList.copyOf(annos));
    }

    @SafeVarargs
    public static <T> MethodSpec<T> ofAnnotated(Class<? extends T> returnType,
            Class<? extends Annotation>... annotationTypes) {
        return ofAnnotated(returnType, new Class[0], annotationTypes);
    }
}
