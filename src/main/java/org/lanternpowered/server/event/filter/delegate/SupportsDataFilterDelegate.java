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
package org.lanternpowered.server.event.filter.delegate;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.event.filter.data.Supports;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SupportsDataFilterDelegate implements ParameterFilterDelegate {

    private final Supports anno;

    public SupportsDataFilterDelegate(Supports anno) {
        this.anno = anno;
    }

    @Override
    public void write(ClassWriter cw, MethodVisitor mv, Method method, Parameter param, int localParam) {
        if (!DataHolder.class.isAssignableFrom(param.getType())) {
            throw new IllegalStateException("Annotated type for data filter is not a DataHolder");
        }

        mv.visitVarInsn(ALOAD, localParam);
        mv.visitTypeInsn(CHECKCAST, Type.getInternalName(DataHolder.class));
        mv.visitLdcInsn(Type.getType(this.anno.value()));
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(DataHolder.class), "supports", "(Ljava/lang/Class;)Z", true);
        Label success = new Label();
        if (this.anno.inverse()) {
            mv.visitJumpInsn(IFEQ, success);
        } else {
            mv.visitJumpInsn(IFNE, success);
        }
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(success);
    }

}
