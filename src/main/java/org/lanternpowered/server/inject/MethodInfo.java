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

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class MethodInfo<T> {

    private final Class<? extends T> returnType;
    private final List<Class<?>> parameterTypes;

    // A list with all the annotation types
    private final List<Class<? extends Annotation>> annotationTypes;

    public MethodInfo(Class<? extends T> returnType, List<Class<?>> parameterTypes,
            List<Class<? extends Annotation>> annotations) {
        this.parameterTypes = ImmutableList.copyOf(parameterTypes);
        this.annotationTypes = ImmutableList.copyOf(annotations);
        this.returnType = returnType;
    }

    public Class<? extends T> getReturnType() {
        return this.returnType;
    }

    public List<Class<?>> getParameterTypes() {
        return this.parameterTypes;
    }

    public List<Class<? extends Annotation>> getAnnotationTypes() {
        return this.annotationTypes;
    }
}
