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

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.ClassUtils.isAssignable;

import com.google.common.base.Throwables;
import org.lanternpowered.server.game.Lantern;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public final class ReflectionHelper {

    private final static Field MODIFIERS_FIELD;

    static {
        try {
            MODIFIERS_FIELD = Field.class.getDeclaredField("modifiers");
            MODIFIERS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, @Nullable Object target, @Nullable Object object) throws Exception {
        final int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            MODIFIERS_FIELD.set(field, modifiers & ~Modifier.FINAL);
        }
        field.setAccessible(true);
        field.set(target, object);
    }

    public static <T> T createUnsafeInstance(final Class<T> objectClass, @Nullable Object... args)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (args == null) {
            args = new Object[] { null };
        }
        final Constructor<T>tConstructor = findConstructor(objectClass, args);
        try {
            return tConstructor.newInstance(args);
        } catch (Exception e) {
            final Object[] deconstructedArgs = deconstructArray(args).toArray();
            return tConstructor.newInstance(deconstructedArgs);
        }
    }

    public static <T> T createInstance(final Class<T> objectClass, @Nullable Object... args) {
        checkArgument(!Modifier.isAbstract(objectClass.getModifiers()), "Cannot construct an instance of an abstract class!");
        checkArgument(!Modifier.isInterface(objectClass.getModifiers()), "Cannot construct an instance of an interface!");
        if (args == null) {
            args = new Object[] { null };
        }
        final Constructor<T> ctor = findConstructor(objectClass, args);
        try {
            return ctor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Lantern.getLogger().error("Couldn't find an appropriate constructor for " + objectClass.getCanonicalName()
                    + "with the args: " + Arrays.toString(args), e);
        }
        throw new IllegalArgumentException("Couldn't find an appropriate constructor for " + objectClass.getCanonicalName()
                + "the args: " + Arrays.toString(args));
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> findConstructor(final Class<T> objectClass, @Nullable Object... args) {
        final Constructor<?>[] ctors = objectClass.getConstructors();
        if (args == null) {
            args = new Object[] {null};
        }
        // labeled loops
        dance:
        for (final Constructor<?> ctor : ctors) {
            final Class<?>[] paramTypes = ctor.getParameterTypes();
            if (paramTypes.length != args.length) {
                for (Object object : args) {
                    if (object != null) { // hahahah
                        if (object.getClass().isArray()) {
                            final Object[] objects = deconstructArray(args).toArray();
                            return findConstructor(objectClass, objects);
                        }
                    }
                }
                continue; // we haven't found the right constructor
            }
            for (int i = 0; i < paramTypes.length; i++) {
                final Class<?> parameter = paramTypes[i];
                if (!isAssignable(args[i] == null ? null : args[i].getClass(), parameter, true)) {
                    continue dance; // continue the outer loop since we didn't find the right one
                }
            }
            // We've found the right constructor, now to actually construct it!
            return (Constructor<T>) ctor;
        }
        throw new IllegalArgumentException("Applicable constructor not found for class: " + objectClass.getCanonicalName() +
                " with args: " + Arrays.toString(args));
    }

    private static List<Object> deconstructArray(Object[] objects) {
        final List<Object> list = new ArrayList<>();
        for (Object object : objects) {
            if (object == null) {
                list.add(null);
                continue;
            }
            if (object.getClass().isArray()) {
                list.addAll(deconstructArray((Object[]) object));
            } else {
                list.add(object);
            }
        }
        return list;
    }

    private ReflectionHelper() {
    }

}
