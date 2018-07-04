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

import static com.google.common.base.Preconditions.checkState;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class LambdaFactory {

    private static final FunctionalInterface<Supplier> supplierInterface = FunctionalInterface.of(Supplier.class);
    private static final FunctionalInterface<Consumer> consumerInterface = FunctionalInterface.of(Consumer.class);
    private static final FunctionalInterface<BiConsumer> biConsumerInterface = FunctionalInterface.of(BiConsumer.class);

    /**
     * Creates a {@link Supplier} to create objects of the type
     * {@link T}. The target class must have a empty constructor.
     *
     * @param objectType The object class
     * @param <T> The object type
     * @return The supplier
     */
    public static <T> Supplier<T> createSupplier(Class<T> objectType) {
        final int modifier = objectType.getModifiers();
        checkState(!Modifier.isAbstract(modifier),
                "The object class \"%s\" cannot be abstract.", objectType.getName());
        checkState(Modifier.isStatic(modifier) || objectType.getEnclosingClass() == null,
                "The object class \"%s\" cannot be inner and non static", objectType.getName());
        final Constructor<?> constructor;
        try {
            constructor = objectType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The object class \"" + objectType.getName() + "\" doesn't have a empty constructor.");
        }
        return create(supplierInterface, constructor);
    }

    /**
     * Creates a {@link Supplier} to create objects of the type
     * {@link T}. The target method must be static and have zero
     * parameters.
     *
     * @param method The method
     * @param <T> The object type
     * @return The supplier
     */
    public static <T> Supplier<T> createSupplier(Method method) {
        checkMethodArgument(Modifier.isStatic(method.getModifiers()),
                "The method \"%s\" must be static.", method);
        checkMethodArgument(method.getParameterCount() == 0,
                "The method \"%s\" may not have any parameters.", method);
        checkMethodArgument(method.getReturnType().equals(void.class),
                "The method \"%s\" return type must not be void.", method);
        return create(supplierInterface, method);
    }

    /**
     * Creates a {@link BiConsumer} for the given {@link Method}. If the
     * method is static, two parameters are expected. Of it's non static,
     * only one parameter is expected which will be the second parameter
     * in the {@link BiConsumer}, the first one is the target object.
     *
     * @param method The method
     * @param <A> The parameter type
     * @return The bi consumer
     */
    public static <A> Consumer<A> createConsumer(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            checkMethodArgument(method.getParameterCount() == 1,
                    "The method \"%s\" doesn't have exactly one parameter, this must be the case for static methods.", method);
        } else {
            checkMethodArgument(method.getParameterCount() == 0,
                    "The method \"%s\" doesn't have exactly zero parameters, this must be the case for non static methods.", method);
        }
        checkMethodArgument(method.getReturnType().equals(void.class),
                "The method \"%s\" return type must be void.", method);
        return create(consumerInterface, method);
    }

    /**
     * Creates a {@link BiConsumer} for the given {@link Method}. If the
     * method is static, two parameters are expected. Of it's non static,
     * only one parameter is expected which will be the second parameter
     * in the {@link BiConsumer}, the first one is the target object.
     *
     * @param method The method
     * @param <A> The first parameter type
     * @param <B> The second parameter type
     * @return The bi consumer
     */
    public static <A, B> BiConsumer<A, B> createBiConsumer(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            checkMethodArgument(method.getParameterCount() == 2,
                    "The method \"%s\" doesn't have exactly two parameters, this must be the case for static methods.", method);
        } else {
            checkMethodArgument(method.getParameterCount() == 1,
                    "The method \"%s\" doesn't have exactly one parameter, this must be the case for non static methods.", method);
        }
        checkMethodArgument(method.getReturnType().equals(void.class),
                "The method \"%s\" return type must be void.", method);
        return create(biConsumerInterface, method);
    }

    /**
     * Attempts to create a lambda for the given {@link Executable}
     * implementing the {@link FunctionalInterface}.
     *
     * @param functionalInterface The functional interface to implement
     * @param executable The executable to call
     * @param <T> The functional interface type
     * @param <F> The function type
     * @return The function
     */
    public static <T, F extends T> F create(FunctionalInterface<T> functionalInterface, Executable executable) {
        try {
            final MethodHandles.Lookup lookup = MethodHandleMagic.trustedLookup().in(executable.getDeclaringClass());
            final MethodHandle methodHandle;
            if (executable instanceof Constructor) {
                methodHandle = lookup.unreflectConstructor((Constructor<?>) executable);
            } else {
                methodHandle = lookup.unreflect((Method) executable);
            }

            // Generate the lambda class
            final CallSite callSite = LambdaMetafactory.metafactory(lookup, functionalInterface.methodName,
                    functionalInterface.classType, functionalInterface.methodType, methodHandle, methodHandle.type());

            // Create the function
            // DON'T USE invokeExact, this acts really weird in this case,
            // for some reason is WrongMethodTypeException being thrown when
            // it shouldn't be, anyway, don't bother
            return (F) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new IllegalStateException("Couldn't create lambda for: \"" + executable + "\". "
                    + "Failed to implement: " + functionalInterface, e);
        }
    }

    private static void checkMethodArgument(boolean state, String text, Method method, Object... args) {
        if (!state) {
            throw new IllegalArgumentException(String.format(text, ArrayUtils.add(args, 0, formatMethod(method))));
        }
    }

    /**
     * Formats the {@link Method} to a nice string.
     *
     * @param method The method
     * @return The formatted method string
     */
    private static String formatMethod(Method method) {
        return String.format("%s;%s%s", method.getDeclaringClass().getName(), method.getName(), Type.getMethodDescriptor(method));
    }

    private LambdaFactory() {
    }
}
