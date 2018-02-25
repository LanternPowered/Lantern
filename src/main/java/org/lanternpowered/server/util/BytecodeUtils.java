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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DCONST_1;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FCONST_1;
import static org.objectweb.asm.Opcodes.FCONST_2;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public final class BytecodeUtils {

    public static void visitEmptyConstructor(ClassVisitor cv) {
        visitEmptyConstructor(cv, Object.class);
    }

    public static void visitEmptyConstructor(ClassVisitor cv, Class<?> superClass) {
        final MethodVisitor mv = cv.visitMethod(ACC_PUBLIC,
                "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(superClass),
                "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    /**
     * Visits the {@link MethodVisitor} to push a
     * constant integer value to the stack.
     *
     * @param mv The method visitor
     * @param value The integer
     */
    public static void visitPushInt(MethodVisitor mv, int value) {
        if (value == -1) {
            mv.visitInsn(ICONST_M1);
        } else if (value == 0) {
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
        } else if (value >= -128 && value <= 127) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value >= -32768 && value <= 32767) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * Visits the {@link MethodVisitor} to push a
     * constant long value to the stack.
     *
     * @param mv The method visitor
     * @param value The long
     */
    public static void visitPushLong(MethodVisitor mv, long value) {
        if (value == 0) {
            mv.visitInsn(LCONST_0);
        } else if (value == 1) {
            mv.visitInsn(LCONST_1);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * Visits the {@link MethodVisitor} to push a
     * constant float value to the stack.
     *
     * @param mv The method visitor
     * @param value The float
     */
    public static void visitPushFloat(MethodVisitor mv, float value) {
        if (value == 0.0f) {
            mv.visitInsn(FCONST_0);
        } else if (value == 1.0f) {
            mv.visitInsn(FCONST_1);
        } else if (value == 2.0f) {
            mv.visitInsn(FCONST_2);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * Visits the {@link MethodVisitor} to push a
     * constant double value to the stack.
     *
     * @param mv The method visitor
     * @param value The double
     */
    public static void visitPushDouble(MethodVisitor mv, double value) {
        if (value == 0.0) {
            mv.visitInsn(DCONST_0);
        } else if (value == 1.0) {
            mv.visitInsn(DCONST_1);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * Visits the {@link MethodVisitor} to apply the load
     * operation for the given return {@link Type}
     * and parameter index.
     *
     * @param mv The method visitor
     * @param type The return type
     */
    public static void visitLoad(MethodVisitor mv, Type type, int parameter) {
        final int sort = type.getSort();
        if (sort == Type.BYTE ||
                sort == Type.BOOLEAN ||
                sort == Type.SHORT ||
                sort == Type.CHAR ||
                sort == Type.INT) {
            mv.visitVarInsn(ILOAD, parameter);
        } else if (sort == Type.DOUBLE) {
            mv.visitVarInsn(DLOAD, parameter);
        } else if (sort == Type.FLOAT) {
            mv.visitVarInsn(FLOAD, parameter);
        } else if (sort == Type.LONG) {
            mv.visitVarInsn(LLOAD, parameter);
        } else if (sort == Type.VOID) {
            throw new IllegalStateException("Cannot load void parameter");
        } else {
            mv.visitVarInsn(ALOAD, parameter);
        }
    }

    /**
     * Visits the {@link MethodVisitor} to apply the return
     * operation for the given return {@link Type}.
     *
     * @param mv The method visitor
     * @param type The return type
     */
    public static void visitReturn(MethodVisitor mv, Type type) {
        final int sort = type.getSort();
        if (sort == Type.BYTE ||
                sort == Type.BOOLEAN ||
                sort == Type.SHORT ||
                sort == Type.CHAR ||
                sort == Type.INT) {
            mv.visitInsn(IRETURN);
        } else if (sort == Type.DOUBLE) {
            mv.visitInsn(DRETURN);
        } else if (sort == Type.FLOAT) {
            mv.visitInsn(FRETURN);
        } else if (sort == Type.LONG) {
            mv.visitInsn(LRETURN);
        } else if (sort == Type.VOID) {
            mv.visitInsn(RETURN);
        } else {
            mv.visitInsn(ARETURN);
        }
    }

    private BytecodeUtils() {
    }
}
