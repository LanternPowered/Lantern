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
package org.lanternpowered.server.util

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

object BytecodeUtils {

    @JvmStatic
    @JvmOverloads
    fun visitEmptyConstructor(cv: ClassVisitor, superClass: Class<*>? = Object::class.java) {
        val mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>", "()V", null, null)
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superClass),
                "<init>", "()V", false)
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    /**
     * Visits the [MethodVisitor] to push a
     * constant integer value to the stack.
     *
     * @param mv The method visitor
     * @param value The integer
     */
    fun visitPushInt(mv: MethodVisitor, value: Int) {
        when {
            value == -1 -> mv.visitInsn(Opcodes.ICONST_M1)
            value == 0 -> mv.visitInsn(Opcodes.ICONST_0)
            value == 1 -> mv.visitInsn(Opcodes.ICONST_1)
            value == 2 -> mv.visitInsn(Opcodes.ICONST_2)
            value == 3 -> mv.visitInsn(Opcodes.ICONST_3)
            value == 4 -> mv.visitInsn(Opcodes.ICONST_4)
            value == 5 -> mv.visitInsn(Opcodes.ICONST_5)
            value >= -128 && value <= 127 -> mv.visitIntInsn(Opcodes.BIPUSH, value)
            value >= -32768 && value <= 32767 -> mv.visitIntInsn(Opcodes.SIPUSH, value)
            else -> mv.visitLdcInsn(value)
        }
    }

    /**
     * Visits the [MethodVisitor] to push a
     * constant long value to the stack.
     *
     * @param mv The method visitor
     * @param value The long
     */
    fun visitPushLong(mv: MethodVisitor, value: Long) {
        when (value) {
            0L -> mv.visitInsn(Opcodes.LCONST_0)
            1L -> mv.visitInsn(Opcodes.LCONST_1)
            else -> mv.visitLdcInsn(value)
        }
    }

    /**
     * Visits the [MethodVisitor] to push a
     * constant float value to the stack.
     *
     * @param mv The method visitor
     * @param value The float
     */
    fun visitPushFloat(mv: MethodVisitor, value: Float) {
        when (value) {
            0.0f -> mv.visitInsn(Opcodes.FCONST_0)
            1.0f -> mv.visitInsn(Opcodes.FCONST_1)
            2.0f -> mv.visitInsn(Opcodes.FCONST_2)
            else -> mv.visitLdcInsn(value)
        }
    }

    /**
     * Visits the [MethodVisitor] to push a
     * constant double value to the stack.
     *
     * @param mv The method visitor
     * @param value The double
     */
    fun visitPushDouble(mv: MethodVisitor, value: Double) {
        when (value) {
            0.0 -> mv.visitInsn(Opcodes.DCONST_0)
            1.0 -> mv.visitInsn(Opcodes.DCONST_1)
            else -> mv.visitLdcInsn(value)
        }
    }

    /**
     * Visits the [MethodVisitor] to apply the load
     * operation for the given return [Type]
     * and parameter index.
     *
     * @param mv The method visitor
     * @param type The return type
     */
    fun visitLoad(mv: MethodVisitor, type: Type, parameter: Int) {
        when (type.sort) {
            Type.BYTE, Type.BOOLEAN, Type.SHORT, Type.CHAR, Type.INT -> mv.visitVarInsn(Opcodes.ILOAD, parameter)
            Type.DOUBLE -> mv.visitVarInsn(Opcodes.DLOAD, parameter)
            Type.FLOAT -> mv.visitVarInsn(Opcodes.FLOAD, parameter)
            Type.LONG -> mv.visitVarInsn(Opcodes.LLOAD, parameter)
            Type.VOID -> error("Cannot load void parameter")
            else -> mv.visitVarInsn(Opcodes.ALOAD, parameter)
        }
    }

    /**
     * Visits the [MethodVisitor] to apply the return
     * operation for the given return [Type].
     *
     * @param mv The method visitor
     * @param type The return type
     */
    fun visitReturn(mv: MethodVisitor, type: Type) {
        when (type.sort) {
            Type.BYTE, Type.BOOLEAN, Type.SHORT, Type.CHAR, Type.INT -> mv.visitInsn(Opcodes.IRETURN)
            Type.DOUBLE -> mv.visitInsn(Opcodes.DRETURN)
            Type.FLOAT -> mv.visitInsn(Opcodes.FRETURN)
            Type.LONG -> mv.visitInsn(Opcodes.LRETURN)
            Type.VOID -> mv.visitInsn(Opcodes.RETURN)
            else -> mv.visitInsn(Opcodes.ARETURN)
        }
    }
}
