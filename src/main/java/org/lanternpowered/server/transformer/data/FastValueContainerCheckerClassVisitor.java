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
import org.lanternpowered.server.util.UncheckedThrowables;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

class FastValueContainerCheckerClassVisitor extends ClassVisitor {

    private static final String COMPOSITE_VALUE_STORE_NAME = "org/spongepowered/api/data/value/mutable/CompositeValueStore";
    private static final String I_COMPOSITE_VALUE_STORE_NAME = "org/lanternpowered/server/data/ICompositeValueStore";

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
            return 1;
        } else if (className.equals(I_COMPOSITE_VALUE_STORE_NAME)) {
            return 2;
            // Don't process java packages
        } else if (className.startsWith("java.")) {
            return 0;
        }
        final LanternClassLoader classLoader = LanternClassLoader.get();
        // If the class is already loaded, we might as well use it
        final Class<?> c = classLoader.getLoadedClass(className.replace('/', '.')).orElse(null);
        if (c != null) {
            return ICompositeValueStore.class.isAssignableFrom(c) ? 2 :
                    CompositeValueStore.class.isAssignableFrom(c) ? 1 : 0;
        }

        try {
            // Read the bytecode of the class we need to analyze
            final byte[] byteCode = classLoader.readByteCode(className);

            // Also check for interfaces, super classes, etc.
            final ClassReader classReader = new ClassReader(byteCode);
            final FastValueContainerCheckerClassVisitor classVisitor = new FastValueContainerCheckerClassVisitor(null, false);
            classReader.accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

            return classVisitor.result;
        } catch (ClassNotFoundException e) {
            throw UncheckedThrowables.thrOw(e);
        }
    }

    private static final Map<String, Integer> processed = new ConcurrentHashMap<>();

    // A lazy option that doesn't force the all the super classes and interfaces
    // to be loaded to check for the CompositeValueStore interface
    // If it is already processed, great, take that result, otherwise
    // ignore it until it is required
    private final boolean lazy;
    private Integer result;

    FastValueContainerCheckerClassVisitor(@Nullable ClassVisitor cv, boolean lazy) {
        super(Opcodes.ASM5, cv);
        this.lazy = lazy;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        // Check for super class and interfaces
        final String[] namesToCheck = new String[1 + interfaces.length];
        namesToCheck[0] = superName;
        System.arraycopy(interfaces, 0, namesToCheck, 1, interfaces.length);

        // Process the super class and every interface
        if (this.lazy) {
            int count = 0;
            int result = 0;
            for (String nameToCheck : namesToCheck) {
                final Integer result1 = processed.get(nameToCheck);
                if (result1 == null) {
                    break;
                }
                if (result1 > result) {
                    result = result1;
                    if (result1 == 2) {
                        break;
                    }
                }
                count++;
            }
            if (result == 2 || count == namesToCheck.length) {
                this.result = result;
            }
        } else {
            this.result = 0;
            for (String nameToCheck : namesToCheck) {
                final int result = isCompositeValueStore(nameToCheck);
                if (result > 0) {
                    this.result = result;
                    // Don't stop until it may be a ICompositeValueStore
                    if (result == 2) {
                        break;
                    }
                }
            }
        }

        if (this.result != null) {
            processed.put(name, this.result);
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }
}
