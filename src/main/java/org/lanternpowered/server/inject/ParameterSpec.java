/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class ParameterSpec<T> {

    private final Class<T> type;
    private final List<Class<? extends Annotation>> annotationTypes;

    private volatile Integer hashCode;

    private ParameterSpec(Class<T> type, List<Class<? extends Annotation>> annotationTypes) {
        this.annotationTypes = annotationTypes;
        this.type = type;
    }

    /**
     * Gets the parameter type.
     * 
     * @return the parameter type
     */
    public Class<? extends T> getType() {
        return this.type;
    }

    /**
     * Gets the annotation types.
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
        ParameterSpec<?> o = (ParameterSpec<?>) other;
        return o.type == this.type && this.annotationTypes.equals(o.annotationTypes);
    }

    @Override
    public int hashCode() {
        if (this.hashCode != null) {
            return this.hashCode;
        }
        return this.hashCode = new HashCodeBuilder(17, 37)
                .append(this.type)
                .append(this.annotationTypes)
                .build();
    }

    /**
     * Creates a new {@link ParameterSpec} for the specified parameter
     * type and the specified annotation types.
     * 
     * @param type the parameter type
     * @param annotationTypes the annotation types
     * @return the parameter spec
     */
    @SafeVarargs
    public static <T> ParameterSpec<T> of(Class<T> type, Class<? extends Annotation>... annotationTypes) {
        checkNotNull(type, "type");
        checkNotNull(annotationTypes, "annotationTypes");
        List<Class<? extends Annotation>> annos = Lists.newArrayList(annotationTypes);
        annos.forEach(anno -> checkNotNull(anno, "annotationType"));
        Collections.sort(annos, (o1, o2) -> o1.hashCode() - o2.hashCode());
        return new ParameterSpec<>(type, ImmutableList.copyOf(annos));
    }
}
