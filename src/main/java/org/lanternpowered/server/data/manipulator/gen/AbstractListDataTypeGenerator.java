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
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.manipulator.immutable.AbstractImmutableListData;
import org.lanternpowered.server.data.manipulator.mutable.AbstractListData;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

final class AbstractListDataTypeGenerator extends TypeGenerator {

    static final String KEY = "key";
    static final String LIST_SUPPLIER = "listSupplier";

    private static final String nKey = Type.getInternalName(Key.class);
    private static final String dKey = Type.getDescriptor(Key.class);

    private static final String nListValue = Type.getInternalName(ListValue.class);

    private static final String nAbstractListData = Type.getInternalName(AbstractListData.class);
    private static final String nAbstractImmutableListData = Type.getInternalName(AbstractImmutableListData.class);

    private static final String dListData = Type.getDescriptor(ListData.class);

    private static final String dImmutableListData = Type.getDescriptor(ImmutableListData.class);

    @Override
    <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> void generateClasses(
            ClassWriter cwM, ClassWriter cwI,
            String mutableClassName, String immutableClassName,
            Class<M> manipulatorType, Class<I> immutableManipulatorType,
            @Nullable Class<? extends M> mutableExpansion, @Nullable Class<? extends I> immutableExpansion,
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

        final Class<?> elementType = TypeToken.of(manipulatorType).resolveType(ListData.class.getTypeParameters()[0]).getRawType();
        final String dElementType = Type.getDescriptor(elementType);

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
                    format("L%s<%s%s%s>;",
                            nAbstractListData, dElementType, dManipulatorType, dImmutableManipulatorType) + signBuilder.toString(),
                    nAbstractListData, interfaces);

            {
                fv = cwM.visitField(ACC_PUBLIC + ACC_STATIC, KEY, dKey,
                        format("L%s<L%s<%s>;>;", nKey, nListValue, dElementType), null);
                fv.visitEnd();
            }
            {
                fv = cwM.visitField(ACC_PUBLIC + ACC_STATIC, LIST_SUPPLIER, "Ljava/util/function/Supplier;",
                        format("Ljava/util/function/Supplier<Ljava/util/List<%s>;>;", dElementType), null);
                fv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn(Type.getType(manipulatorType));
                mv.visitLdcInsn(Type.getType(immutableManipulatorType));
                mv.visitFieldInsn(GETSTATIC, mutableClassName, KEY, dKey);
                mv.visitFieldInsn(GETSTATIC, mutableClassName, LIST_SUPPLIER, "Ljava/util/function/Supplier;");
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Supplier", "get", "()Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/util/List");
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractListData, "<init>",
                        format("(Ljava/lang/Class;Ljava/lang/Class;%sLjava/util/List;)V", dKey), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(5, 1);
                mv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PUBLIC, "<init>", format("(%s)V", dImmutableManipulatorType), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractListData, "<init>", format("(%s)V", dImmutableListData), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            {
                mv = cwM.visitMethod(ACC_PUBLIC, "<init>", format("(%s)V", dManipulatorType), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractListData, "<init>",
                        format("(%s)V", dListData), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            cwM.visitEnd();
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
                    format("L%s<%s%s%s>;",
                            nAbstractImmutableListData, dElementType, dImmutableManipulatorType, dManipulatorType) + signBuilder.toString(),
                    nAbstractImmutableListData, interfaces);

            {
                fv = cwI.visitField(ACC_PUBLIC + ACC_STATIC, KEY, dKey,
                        format("L%s<L%s<%s>;>;", nKey, nListValue, dElementType), null);
                fv.visitEnd();
            }
            {
                fv = cwI.visitField(ACC_PUBLIC + ACC_STATIC, LIST_SUPPLIER, "Ljava/util/function/Supplier;",
                        format("Ljava/util/function/Supplier<Ljava/util/List<%s>;>;", dElementType), null);
                fv.visitEnd();
            }
            {
                mv = cwI.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn(Type.getType(immutableManipulatorType));
                mv.visitLdcInsn(Type.getType(manipulatorType));
                mv.visitFieldInsn(GETSTATIC, immutableClassName, "key", dKey);
                mv.visitFieldInsn(GETSTATIC, immutableClassName, LIST_SUPPLIER, "Ljava/util/function/Supplier;");
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Supplier", "get", "()Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/util/List");
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractImmutableListData, "<init>",
                        format("(Ljava/lang/Class;Ljava/lang/Class;%sLjava/util/List;)V", dKey), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(5, 1);
                mv.visitEnd();
            }
            {
                mv = cwI.visitMethod(ACC_PUBLIC, "<init>", format("(%s)V", dManipulatorType), null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, nAbstractImmutableListData, "<init>",
                        format("(%s)V", dListData), false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            cwI.visitEnd();
        }
    }
}
