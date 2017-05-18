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
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
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
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.V1_8;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.AbstractImmutableData;
import org.lanternpowered.server.data.manipulator.mutable.AbstractData;
import org.lanternpowered.server.data.value.IValueContainer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

final class AbstractDataTypeGenerator extends TypeGenerator {

    static final String REGISTRATION_CONSUMER = "registrationConsumer";

    private static final String nAbstractImmutableData = Type.getInternalName(AbstractImmutableData.class);
    private static final String nAbstractData = Type.getInternalName(AbstractData.class);
    private static final String nIDataManipulatorBase = Type.getInternalName(IDataManipulatorBase.class);
    private static final String nIValueContainer = Type.getInternalName(IValueContainer.class);
    private static final String dDataManipulator = Type.getDescriptor(DataManipulator.class);
    private static final String dImmutableDataManipulator = Type.getDescriptor(ImmutableDataManipulator.class);

    @Override
    <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> void generateClasses(
            ClassWriter cwM, ClassWriter cwI,
            String mutableClassName, String immutableClassName,
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion,
            @Nullable Class<? extends I> immutableExpansion,
            @Nullable List<Method> mutableMethods,
            @Nullable List<Method> immutableMethods) {
        FieldVisitor fv;
        MethodVisitor mv;

        final String dManipulatorType = Type.getDescriptor(manipulatorType);
        final String dImmutableManipulatorType = Type.getDescriptor(immutableManipulatorType);
        final String dMutableExpansion = mutableExpansion == null ? null : Type.getDescriptor(mutableExpansion);
        final String dImmutableExpansion = immutableExpansion == null ? null : Type.getDescriptor(immutableExpansion);

        final String nManipulatorType = Type.getInternalName(manipulatorType);
        final String nImmutableManipulatorType = Type.getInternalName(immutableManipulatorType);
        final String nMutableExpansion = mutableExpansion == null ? null : Type.getInternalName(mutableExpansion);
        final String nImmutableExpansion = immutableExpansion == null ? null : Type.getInternalName(immutableExpansion);

        // Mutable class
        {
            final String[] interfaces = new String[mutableExpansion != null ? 2 : 1];
            final StringBuilder signBuilder = new StringBuilder();
            interfaces[0] = nManipulatorType;
            signBuilder.append(dManipulatorType);
            if (mutableExpansion != null) {
                interfaces[1] = nMutableExpansion;
                signBuilder.append(dMutableExpansion);
            }

            cwM.visit(V1_8, ACC_PUBLIC + ACC_SUPER, mutableClassName,
                    format("L%s<%s%s>;",
                            nAbstractData, dManipulatorType, dImmutableManipulatorType) + signBuilder.toString(),
                    nAbstractData, interfaces);

            {
                fv = cwM.visitField(ACC_PUBLIC + ACC_STATIC, REGISTRATION_CONSUMER, "Ljava/util/function/Consumer;",
                        format("Ljava/util/function/Consumer<L%s<*>;>;", nIValueContainer), null);
                fv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PROTECTED, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn(Type.getType(manipulatorType));
                mv.visitLdcInsn(Type.getType(immutableManipulatorType));
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractData, "<init>",
                        "(Ljava/lang/Class;Ljava/lang/Class;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 1);
                mv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PROTECTED, "<init>",
                        format("(%s)V", dImmutableManipulatorType), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractData, "<init>", format("(%s)V", dImmutableDataManipulator), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PROTECTED, "<init>",
                        format("(%s)V", dManipulatorType), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractData, "<init>", format("(%s)V", dDataManipulator), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PROTECTED, "<init>",
                        format("(L%s;)V", nIDataManipulatorBase),
                        format("(L%s<%s%s>;)V", nIDataManipulatorBase, dManipulatorType, dImmutableManipulatorType), null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractData, "<init>",
                        format("(L%s;)V", nIDataManipulatorBase), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PUBLIC, "registerKeys", "()V", null, null);
                mv.visitCode();
                mv.visitFieldInsn(GETSTATIC, mutableClassName, REGISTRATION_CONSUMER, "Ljava/util/function/Consumer;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 1);
                mv.visitEnd();
            }
        }
        // Immutable class
        {
            final String[] interfaces = new String[mutableExpansion != null ? 2 : 1];
            final StringBuilder signBuilder = new StringBuilder();
            interfaces[0] = nImmutableManipulatorType;
            signBuilder.append(dImmutableManipulatorType);
            if (mutableExpansion != null) {
                interfaces[1] = nImmutableExpansion;
                signBuilder.append(dImmutableExpansion);
            }

            cwI.visit(V1_8, ACC_PUBLIC + ACC_SUPER, immutableClassName,
                    format("L%s<%s%s>;",
                            nAbstractImmutableData, dImmutableManipulatorType, dManipulatorType) + signBuilder.toString(),
                    nAbstractImmutableData, interfaces);

            {
                fv = cwI.visitField(ACC_PUBLIC + ACC_STATIC, REGISTRATION_CONSUMER, "Ljava/util/function/Consumer;",
                        format("Ljava/util/function/Consumer<L%s<*>;>;", nIValueContainer), null);
                fv.visitEnd();
            }
            {
                mv = cwI.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn(Type.getType(immutableManipulatorType));
                mv.visitLdcInsn(Type.getType(manipulatorType));
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractImmutableData, "<init>",
                        "(Ljava/lang/Class;Ljava/lang/Class;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 1);
                mv.visitEnd();
            }
            {
                mv = cwI.visitMethod(ACC_PUBLIC, "<init>", format("(%s)V", dManipulatorType), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractImmutableData, "<init>",
                        format("(%s)V", dDataManipulator), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                mv = cwI.visitMethod(ACC_PUBLIC, "registerKeys", "()V", null, null);
                mv.visitCode();
                mv.visitFieldInsn(GETSTATIC, immutableClassName, REGISTRATION_CONSUMER, "Ljava/util/function/Consumer;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 1);
                mv.visitEnd();
            }
        }

        visitMethods(mutableClassName, cwM, "getValue", mutableMethods);
        visitMethods(immutableClassName, cwI, "getImmutableValue", immutableMethods);
    }

}
