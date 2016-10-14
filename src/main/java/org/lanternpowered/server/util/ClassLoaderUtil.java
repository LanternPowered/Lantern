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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class ClassLoaderUtil {

    private static final Method DEFINE_CLASS_METHOD;
    private static final Method ADD_URL_METHOD;

    static {
        try {
            DEFINE_CLASS_METHOD = ClassLoader.class.getDeclaredMethod("defineClass",
                    String.class, byte[].class, int.class, int.class);
            DEFINE_CLASS_METHOD.setAccessible(true);
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> defineClass(ClassLoader classLoader, String name, byte[] byteCode) {
        try {
            return (Class<?>) DEFINE_CLASS_METHOD.invoke(classLoader, name, byteCode, 0, byteCode.length);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void addURL(URLClassLoader classLoader, URL url) {
        try {
            ADD_URL_METHOD.invoke(classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassLoaderUtil() {
    }
}
