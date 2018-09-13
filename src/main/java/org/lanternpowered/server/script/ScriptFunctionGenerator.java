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
package org.lanternpowered.server.script;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import org.lanternpowered.api.script.Script;
import org.lanternpowered.server.util.DefineableClassLoader;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

final class ScriptFunctionGenerator {

    interface Builder<F> {

        F get(Script<F> function);
    }

    private final Map<Class<?>, Builder> functionProviders = new ConcurrentHashMap<>();
    private final DefineableClassLoader classLoader = new DefineableClassLoader();

    public <F> Builder<F> get(ScriptFunctionMethod<F> functionMethod) {
        //noinspection unchecked
        return this.functionProviders.computeIfAbsent(
                functionMethod.getFunctionClass(), functionClass -> getBuilder(functionMethod));
    }

    private <F> Builder<F> getBuilder(ScriptFunctionMethod<F> functionMethod) {
        //noinspection unchecked
        final Class<F> functionClass = (Class<F>) generateScriptFunctionClass(functionMethod);
        final Constructor<F> constructor;
        try {
            constructor = functionClass.getConstructor(LanternScript.class);
        } catch (NoSuchMethodException e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }
        return function -> {
            try {
                return constructor.newInstance(function);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw UncheckedThrowables.throwUnchecked(e);
            }
        };
    }

    /**
     * Generates a {@link ScriptFunction} class for the specified {@link ScriptFunctionMethod}.
     *
     * @param functionMethod The script function method
     * @return The script function class
     */
    private Class<? extends ScriptFunction> generateScriptFunctionClass(ScriptFunctionMethod<?> functionMethod) {
        final Class<?> functionClass = functionMethod.getFunctionClass();

        final String name = functionClass.getName() + ScriptFunction.class.getSimpleName() + "Impl35962493";
        final String internalName = name.replace('.', '/');
        final String superClass;
        final String[] interfaces;

        if (functionMethod.getFunctionClass().isInterface()) {
            interfaces = new String[2];
            interfaces[1] = Type.getInternalName(functionClass);
            superClass = Type.getInternalName(Object.class);
        } else {
            interfaces = new String[1];
            superClass = Type.getInternalName(functionClass);
        }
        interfaces[0] = Type.getInternalName(ScriptFunction.class);

        // public class <I>ScriptReferencedImpl35962493 implements <I>, ScriptFunction {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, internalName, null, superClass, interfaces);

        // private final LanternScript script;
        FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "script", Type.getDescriptor(LanternScript.class), null, null);
        fv.visitEnd();

        // public <I>ScriptReferencedImpl35962493(LanternScript script) {
        //     this.script = script;
        // }
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + Type.getDescriptor(LanternScript.class) + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, internalName, "script", Type.getDescriptor(LanternScript.class));
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // @Override
        // public LanternScript getScript() {
        //     return this.script;
        // }
        mv = cw.visitMethod(ACC_PUBLIC, "getScript", "()" + Type.getDescriptor(LanternScript.class), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalName, "script", Type.getDescriptor(LanternScript.class));
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // The script method
        @SuppressWarnings("ConstantConditions") @Nonnull
        final Method method = functionMethod.getMethod();
        mv = cw.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, internalName, "script", Type.getDescriptor(LanternScript.class));
        mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(LanternScript.class), "getRaw", "()" + Type.getDescriptor(Object.class), false);
        mv.visitTypeInsn(CHECKCAST, Type.getInternalName(functionClass));
        // Load all the method parameters to pass them through the delegated method
        for (int i = 1; i <= method.getParameterTypes().length; i++) {
            mv.visitVarInsn(ALOAD, i);
        }
        mv.visitMethodInsn(getInvokeMethodInsnOpcode(method), Type.getInternalName(method.getDeclaringClass()), method.getName(),
                Type.getMethodDescriptor(method), true);
        final Class<?> returnType = method.getReturnType();
        mv.visitInsn(getReturnInsnOpcode(returnType));
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();

        final byte[] byteCode = cw.toByteArray();
        //noinspection unchecked
        return this.classLoader.defineClass(name, byteCode);
    }

    /**
     * Gets the opcode that should be used to invoke the specified method.
     *
     * @param method The method
     * @return The opcode
     */
    private static int getInvokeMethodInsnOpcode(Method method) {
        if (method.getDeclaringClass().isInterface()) {
            return INVOKEINTERFACE;
        } else {
            return INVOKEVIRTUAL;
        }
    }

    /**
     * Gets the opcode that should be used to return in a method
     * with the specified return type.
     *
     * @param returnType The return type
     * @return The opcode
     */
    private static int getReturnInsnOpcode(Class<?> returnType) {
        if (void.class.isAssignableFrom(returnType)) {
            return RETURN;
        } else if (returnType.isPrimitive()) {
            if (long.class.isAssignableFrom(returnType)) {
                return LRETURN;
            } else if (double.class.isAssignableFrom(returnType)) {
                return DRETURN;
            } else if (float.class.isAssignableFrom(returnType)) {
                return FRETURN;
            }
            return IRETURN;
        }
        return ARETURN;
    }
}
