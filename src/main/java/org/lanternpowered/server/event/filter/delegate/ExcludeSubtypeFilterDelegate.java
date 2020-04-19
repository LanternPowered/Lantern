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
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.api.event.filter.type.Exclude;

import java.lang.reflect.Method;

public class ExcludeSubtypeFilterDelegate extends SubtypeFilterDelegate {

    public ExcludeSubtypeFilterDelegate(Exclude anno) {
        super(anno.value());
    }

    @Override
    public int write(String name, ClassWriter cw, MethodVisitor mv, Method method, int locals) {
        // TODO could do an optimization here to inline a single instanceof if
        // the set would contain only a single member
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name, "classes", "Ljava/util/Set;");
        // Loop through the classes set's members
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;", true);
        mv.visitVarInsn(ASTORE, locals);
        Label continueLabel = new Label();
        mv.visitJumpInsn(GOTO, continueLabel);
        Label loopStart = new Label();
        mv.visitLabel(loopStart);
        mv.visitVarInsn(ALOAD, locals);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(CHECKCAST, "java/lang/Class");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
        mv.visitJumpInsn(Opcodes.IFEQ, continueLabel);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(continueLabel);
        mv.visitVarInsn(ALOAD, locals);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
        mv.visitJumpInsn(IFNE, loopStart);
        return locals + 1;
    }
}
