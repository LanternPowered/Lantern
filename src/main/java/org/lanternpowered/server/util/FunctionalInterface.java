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
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Represents a {@link java.lang.FunctionalInterface}
 * that can be be implemented by a lambda.
 */
public final class FunctionalInterface<T> {

    /**
     * Attempts to find a function method in the given functional interface class.
     * <p>A functional interface doesn't need to be annotated with
     * {@link FunctionalInterface}, but only one non implemented method may
     * be present.
     *
     * @param functionalInterface The functional interface
     * @param <T> The type of the functional interface
     * @return The functional method
     * @throws IllegalArgumentException If no valid functional method could be found
     */
    public static <T> FunctionalInterface<T> of(Class<T> functionalInterface) {
        checkNotNull(functionalInterface, "functionalInterface");
        checkState(functionalInterface.isInterface(), "functionalInterface must be a interface");
        Method validMethod = null;
        for (Method method : functionalInterface.getMethods()) {
            // Ignore default and static methods
            if (method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            // Only one non implemented method may be present
            if (validMethod != null) {
                throw new IllegalStateException("Found multiple non-default methods in: " +
                        functionalInterface.getClass().getName());
            }
            validMethod = method;
        }
        if (validMethod == null) {
            throw new IllegalStateException("Couldn't find a non-default method in: " +
                    functionalInterface.getClass().getName());
        }
        final MethodType classType = MethodType.methodType(functionalInterface);
        final MethodType methodType = MethodType.methodType(validMethod.getReturnType(), validMethod.getParameterTypes());
        return new FunctionalInterface<>(functionalInterface, validMethod.getName(), classType, methodType);
    }

    private final Class<T> functionClass;
    final String methodName;

    final MethodType classType;
    final MethodType methodType;

    /**
     * Constructs a new {@link FunctionalInterface} from the given arguments.
     *
     * @param functionClass The function class
     * @param name The methodName of the function method
     * @param classType The function interface that will be implemented
     * @param methodType The function method signature that will be implemented
     */
    private FunctionalInterface(Class<T> functionClass, String name, MethodType classType, MethodType methodType) {
        this.functionClass = functionClass;
        this.methodName = name;
        this.classType = classType;
        this.methodType = methodType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("class", this.functionClass.getName())
                .add("method", this.methodName + this.methodType)
                .toString();
    }
}
