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
package org.lanternpowered.server.transformer.data;

import static java.util.Objects.requireNonNull;

import org.lanternpowered.launch.LanternClassLoader;
import org.lanternpowered.server.data.ICompositeValueStore;
import org.objectweb.asm.ClassReader;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class FastValueContainerChecker {

    private static final String COMPOSITE_VALUE_STORE_NAME = "org/spongepowered/api/data/value/mutable/CompositeValueStore";
    private static final String I_COMPOSITE_VALUE_STORE_NAME = "org/lanternpowered/server/data/ICompositeValueStore";

    private static final int STATE_UNKNOWN = -1;
    static final int STATE_NOT_STORE = 0;
    static final int STATE_IS_STORE = 1;
    static final int STATE_IS_I_STORE = 2;

    private static final Map<String, Integer> processed = new ConcurrentHashMap<>();

    /**
     * Gets whether the target class name {@link CompositeValueStore} implements.
     * A return value of {@code 0} means false, {@code 1} means that {@link CompositeValueStore}
     * is implemented and {@code 2} means that {@link ICompositeValueStore} is implemented.
     *
     * @param className The class name
     * @return Is composite value store
     */
    static int isCompositeValueStore(String className) {
        requireNonNull(className, "className");
        final Integer result = processed.get(className);
        if (result != null) {
            return result;
        }
        final int result1 = isCompositeValueStore0(className);
        processed.put(className, result1);
        return result1;
    }

    private static int isCompositeValueStore0(String className) {
        // One of the interfaces directly implements the CompositeValueStore,
        // directly return true
        if (className.equals(COMPOSITE_VALUE_STORE_NAME)) {
            return STATE_IS_STORE;
        } else if (className.equals(I_COMPOSITE_VALUE_STORE_NAME)) {
            return STATE_IS_I_STORE;
            // Don't process java packages
        } else if (className.startsWith("java.")) {
            return STATE_NOT_STORE;
        }
        final LanternClassLoader classLoader = LanternClassLoader.get();
        // If the class is already loaded, we might as well use it
        final Class<?> c = classLoader.getLoadedClass(className.replace('/', '.')).orElse(null);
        if (c != null) {
            return ICompositeValueStore.class.isAssignableFrom(c) ? STATE_IS_I_STORE :
                    CompositeValueStore.class.isAssignableFrom(c) ? STATE_IS_STORE : STATE_NOT_STORE;
        }

        try {
            // Read the bytecode of the class we need to analyze
            final byte[] byteCode = classLoader.readByteCode(className);

            final ClassReader classReader = new ClassReader(byteCode);

            final String superName = classReader.getSuperName();
            final String[] interfaces = classReader.getInterfaces();

            return getCompositeValueStoreType(className, superName, interfaces, false);
        } catch (ClassNotFoundException e) {
            // Let's assume this is not a value store, since the bytecode
            // wasn't available from our class loader
            return STATE_NOT_STORE;
        }
    }

    static int getCompositeValueStoreType(String className, String superName, String[] interfaces, boolean lazy) {
        // Check for super class and interfaces
        final String[] namesToCheck = new String[1 + interfaces.length];
        namesToCheck[0] = superName;
        System.arraycopy(interfaces, 0, namesToCheck, 1, interfaces.length);

        int finalResult = STATE_UNKNOWN;

        // Process the super class and every interface
        if (lazy) {
            int count = 0;
            int result = 0;
            for (String nameToCheck : namesToCheck) {
                final Integer result1 = processed.get(nameToCheck);
                if (result1 == null) {
                    break;
                }
                if (result1 > result) {
                    result = result1;
                    if (result1 == STATE_IS_I_STORE) {
                        break;
                    }
                }
                count++;
            }
            if (result == STATE_IS_I_STORE || count == namesToCheck.length) {
                finalResult = result;
            }
        } else {
            finalResult = STATE_NOT_STORE;
            for (String nameToCheck : namesToCheck) {
                final int result = isCompositeValueStore(nameToCheck);
                if (result != STATE_NOT_STORE) {
                    finalResult = result;
                    // Don't stop until it may be a ICompositeValueStore
                    if (result == STATE_IS_I_STORE) {
                        break;
                    }
                }
            }
        }

        if (finalResult != STATE_UNKNOWN) {
            processed.put(className, finalResult);
        }

        return finalResult;
    }
}
