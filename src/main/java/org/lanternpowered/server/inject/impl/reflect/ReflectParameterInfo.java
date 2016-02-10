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
package org.lanternpowered.server.inject.impl.reflect;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.inject.ParameterInfo;
import org.lanternpowered.server.inject.ParameterSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

final class ReflectParameterInfo<T, O> implements ParameterInfo<T> {

    final ParameterSpec<T> spec;
    private final List<Annotation> annotations;
    private final AnnotatedElement element;
    private final Class<T> type;
    final O accessor;

    @SuppressWarnings("unchecked")
    public ReflectParameterInfo(AnnotatedElement element,
            O accessor, Class<T> type) {
        this.annotations = ImmutableList.copyOf(element.getAnnotations());
        this.spec = ParameterSpec.of(type, Collections2.transform(
                this.annotations, anno -> anno.getClass()).toArray(new Class[0]));
        this.accessor = accessor;
        this.element = element;
        this.type = type;
    }

    @Override
    public Class<? extends T> getType() {
        return this.type;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        return this.element.getAnnotation(type);
    }

    @Override
    public <A extends Annotation> boolean hasAnnotation(Class<A> type) {
        return this.element.isAnnotationPresent(type);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        return ((ReflectParameterInfo<?,?>) other).accessor.equals(this.accessor);
    }

    @Override
    public int hashCode() {
        return this.accessor.hashCode();
    }
}
