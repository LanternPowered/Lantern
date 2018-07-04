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
package org.lanternpowered.server.behavior.neww;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.util.FunctionalInterface;
import org.lanternpowered.server.util.LambdaFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a {@link Behavior} which is usable for multiple {@link LanternBehaviorType}s.
 * <p>In all extended classes will there be searched for {@link BehaviorMethod} annotations
 * on the methods, these methods will be called when a specific {@link BehaviorType} type
 * is getting executed, multiple {@link BehaviorMethod} annotations are allowed.
 * <p>The signature of the method must be:
 * <pre>
 * {@code
 * @BehaviorMethod(BehaviorTypes.Block.BREAK)
 * public boolean handleBlockBreak(BehaviorType type, BehaviorContext ctx) {
 *     return false;
 * }}
 * </pre>
 * OR (the behavior type parameter is optional)
 * <pre>
 * {@code
 * @BehaviorMethod(BehaviorTypes.Block.BREAK)
 * public boolean handleBlockBreak(BehaviorContext ctx) {
 *     return false;
 * }}
 * </pre>
 * Multiple methods with the same behavior type are allowed and
 * will be executed sequentially, one of them needs to be successful
 * in order to return {@code true}.
 */
public abstract class MultiBehavior implements Behavior {

    private static final FunctionalInterface<InternalBehavior> internalBehaviorInterface =
            FunctionalInterface.of(InternalBehavior.class);
    private static final FunctionalInterface<SimpleInternalBehavior> simpleInternalBehaviorInterface =
            FunctionalInterface.of(SimpleInternalBehavior.class);

    private final Multimap<BehaviorType, InternalBehavior> behaviors = LinkedListMultimap.create();

    /**
     * Constructs this multi behavior.
     */
    protected MultiBehavior() {
        Class<?> theClass = getClass();
        final Set<String> added = new HashSet<>();
        do {
            final Method[] methods = theClass.getDeclaredMethods();
            for (Method method : methods) {
                final BehaviorMethods behaviorMethods = method.getAnnotation(BehaviorMethods.class);
                if (behaviorMethods == null) {
                    continue;
                }
                final Class<?>[] params = method.getParameterTypes();
                if (!Modifier.isPrivate(method.getModifiers())) {
                    final String name = method.getName() + "$$" + params.length;
                    if (!added.add(name)) { // Check if the method was overridden
                        continue;
                    }
                }
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalStateException(String.format("A behavior method cannot be static. "
                            + "Found invalid method %s in class %s", method.getName(), theClass.getName()));
                }
                if (params.length != 1 && params.length != 2) {
                    throw new IllegalStateException(String.format("The parameter count of a behavior method must be"
                            + "one or two. Found invalid method %s in class %s", method.getName(), theClass.getName()));
                }
                if (params.length == 2 && params[0].equals(BehaviorType.class)) {
                    throw new IllegalStateException(String.format("The first parameter must be BehaviorType when using"
                            + "the two parameter signature. Found invalid method %s in class %s", method.getName(), theClass.getName()));
                }
                if (params[params.length == 2 ? 1 : 0].equals(BehaviorType.class)) {
                    throw new IllegalStateException(String.format("The second (or first) parameter must be BehaviorContext. "
                            + "Found invalid method %s in class %s", method.getName(), theClass.getName()));
                }
                final InternalBehavior internalBehavior;
                if (params.length == 1) {
                    internalBehavior = LambdaFactory.create(simpleInternalBehaviorInterface, method);
                } else {
                    internalBehavior = LambdaFactory.create(internalBehaviorInterface, method);
                }
                for (BehaviorMethod behaviorMethod : behaviorMethods.value()) {
                    this.behaviors.put(BehaviorType.of(behaviorMethod.value()), internalBehavior);
                }
            }
        // Loop through all the superclasses until we reach this class
        } while ((theClass = theClass.getSuperclass()) != MultiBehavior.class);
    }

    /**
     * A interface that will be implemented to call behavior methods. INTERNAL use only.
     */
    protected interface InternalBehavior {

        boolean apply(MultiBehavior target, BehaviorType type, BehaviorContext ctx);
    }

    protected interface SimpleInternalBehavior extends InternalBehavior {

        @Override
        default boolean apply(MultiBehavior target, BehaviorType type, BehaviorContext ctx) {
            return apply(target, ctx);
        }

        boolean apply(MultiBehavior target, BehaviorContext ctx);
    }

    @Override
    public boolean apply(BehaviorType type, BehaviorContext ctx) {
        final Collection<InternalBehavior> behaviors = this.behaviors.get(type);
        if (behaviors.isEmpty()) {
            return true;
        }
        boolean success = false;
        for (InternalBehavior behavior : behaviors) {
            success = behavior.apply(this, type, ctx);
        }
        return success;
    }

    /**
     * Represents a multi behavior method
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(BehaviorMethods.class)
    protected @interface BehaviorMethod {

        /**
         * The behavior type that this method represents.
         *
         * @return The behavior type
         */
        String value();
    }

    /**
     * Represents a collection of {@link BehaviorMethod} annotations.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface BehaviorMethods {

        BehaviorMethod[] value();
    }
}
