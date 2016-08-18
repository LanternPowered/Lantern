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
package org.lanternpowered.server.script;

import com.google.common.base.MoreObjects;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptFunctionMethod<F> {

    private static final Map<Class<?>, ScriptFunctionMethod> scriptFunctionMethods = new ConcurrentHashMap<>();

    /**
     * Gets or attempts to construct a {@link ScriptFunctionMethod} for
     * the specified function class.
     *
     * @param functionClass The function class
     * @return The script type
     */
    public static <F> ScriptFunctionMethod<F> of(Class<F> functionClass) {
        //noinspection unchecked
        return scriptFunctionMethods.computeIfAbsent(functionClass, ScriptFunctionMethod::getFunctionMethod);
    }

    private static ScriptFunctionMethod getFunctionMethod(Class<?> functionClass) {
        // Find the method that will represent the function
        final List<Method> methods = new ArrayList<>();
        // The amount of abstract methods
        int nonDefaultCount = 0;

        final boolean iface = functionClass.isInterface();
        for (Method method : functionClass.getMethods()) {
            final int mod = method.getModifiers();
            if (Modifier.isStatic(mod)) {
                continue;
            }
            if ((iface && method.isDefault()) || (!iface && !Modifier.isAbstract(mod))) {
                methods.add(method);
            } else if (++nonDefaultCount > 1) {
                throw new IllegalArgumentException("The interface/abstract class may only contain one abstract method.");
            } else {
                methods.add(0, method);
            }
        }
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("The interface/abstract class doesn't contain any public methods.");
        }
        //noinspection unchecked
        return new ScriptFunctionMethod(functionClass, methods.get(0));
    }

    private final Class<F> functionClass;
    private final Method method;

    private ScriptFunctionMethod(Class<F> functionClass, Method method) {
        this.functionClass = functionClass;
        this.method = method;
    }

    public Method getMethod() {
        return this.method;
    }

    public Class<F> getFunctionClass() {
        return this.functionClass;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("functionClass", this.functionClass)
                .add("method", this.method)
                .toString();
    }
}
