/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
