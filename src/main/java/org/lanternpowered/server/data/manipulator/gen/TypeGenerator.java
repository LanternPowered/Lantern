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
package org.lanternpowered.server.data.manipulator.gen;

import static java.lang.String.format;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.SIPUSH;

import com.google.common.reflect.TypeToken;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.Value;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public abstract class TypeGenerator {

    static final String KEYS = "keys";

    static final String nKey = Type.getInternalName(Key.class);
    static final String dKey = Type.getDescriptor(Key.class);

    static final String nValue = Type.getInternalName(Value.Mutable.class);

    private static final class Counter {
        private int value;
    }

    private static final Map<String, Counter> counters = new HashMap<>();

    static String newName(String name) {
        final Counter counter = counters.computeIfAbsent(name, aClass -> new Counter());
        final int index = name.lastIndexOf('.');
        final int index1 = name.substring(0, index).lastIndexOf('.');
        final int value = counter.value++;
        return name.substring(index1 + 1, index) + ".implementation." + name.substring(index + 1) + "Impl" + (value == 0 ? "" : value);
    }

    static String newName(Class<?> manipulatorType) {
        return newName(manipulatorType.getName());
    }

    static String newInternalName(String name) {
        return newName(name.replace('/', '.')).replace('.', '/');
    }

    static String newInternalName(Class<?> manipulatorType) {
        return newName(manipulatorType).replace('.', '/');
    }

    abstract <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> void generateClasses(
            ClassWriter cwM, ClassWriter cwI,
            String mutableClassName, String immutableClassName,
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion,
            @Nullable Class<? extends I> immutableExpansion,
            @Nullable List<Method> mutableMethods,
            @Nullable List<Method> immutableMethods);


    static void visitMethods(String className, ClassWriter cw, String methodName, @Nullable List<Method> methods) {
        if (methods != null) {
            final FieldVisitor fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, KEYS, format("[%s", dKey), null, null);
            fv.visitEnd();
            for (int i = 0; i < methods.size(); i++) {
                final Method method = methods.get(i);
                final TypeToken<?> typeToken = TypeToken.of(method.getGenericReturnType());

                final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method),
                        format("()%s", getDescriptor(typeToken)), null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETSTATIC, className, KEYS, format("[%s", dKey));
                visitIntInsn(mv, i);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, className, methodName,
                        format("(%s)Ljava/util/Optional;", dKey), false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "get", "()Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(method.getReturnType()));
                mv.visitInsn(ARETURN);
                mv.visitMaxs(3, 1);
                mv.visitEnd();
            }
        }
    }

    private static void visitIntInsn(MethodVisitor mv, int value) {
        if (value == 0) {
            mv.visitInsn(ICONST_0);
        } else if (value == 1) {
            mv.visitInsn(ICONST_1);
        } else if (value == 2) {
            mv.visitInsn(ICONST_2);
        } else if (value == 3) {
            mv.visitInsn(ICONST_3);
        } else if (value == 4) {
            mv.visitInsn(ICONST_4);
        } else if (value == 5) {
            mv.visitInsn(ICONST_5);
        } else if (value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value <= Short.MAX_VALUE) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static String getDescriptor(TypeToken<?> typeToken) {
        final String value = typeToken.toString();
        return 'L' + value.replace('.', '/').replaceAll("<", "<L").replaceAll(">", ">;");
    }
}
