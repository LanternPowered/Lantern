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
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.util.Tristate;

import java.lang.reflect.Method;

public class CancellationEventFilterDelegate implements FilterDelegate {

    private final Tristate state;

    public CancellationEventFilterDelegate(Tristate state) {
        this.state = state;
    }

    @Override
    public int write(String name, ClassWriter cw, MethodVisitor mv, Method method, int locals) {
        if (this.state == Tristate.UNDEFINED) {
            return locals;
        }
        if (!Cancellable.class.isAssignableFrom(method.getParameters()[0].getType())) {
            throw new IllegalStateException("Attempted to filter a non-cancellable event type by its cancellation status");
        }
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Cancellable.class));
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Cancellable.class), "isCancelled", "()Z", true);
        Label success = new Label();
        if (this.state == Tristate.TRUE) {
            mv.visitJumpInsn(IFNE, success);
        } else {
            mv.visitJumpInsn(IFEQ, success);
        }
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(success);
        return locals;
    }

}
