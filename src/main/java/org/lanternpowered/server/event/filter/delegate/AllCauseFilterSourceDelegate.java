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
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.filter.cause.All;

import java.lang.reflect.Parameter;

public class AllCauseFilterSourceDelegate extends CauseFilterSourceDelegate {

    private final All anno;

    public AllCauseFilterSourceDelegate(All anno) {
        this.anno = anno;
    }

    @Override
    protected void insertCauseCall(MethodVisitor mv, Parameter param, Class<?> targetType) {
        if (targetType.isArray()) {
            mv.visitLdcInsn(Type.getType(targetType.getComponentType()));
        } else {
            throw new IllegalStateException(
                    "Parameter " + param.getName() + " is marked with @All but is not an array type");
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Cause.class), "allOf",
                "(Ljava/lang/Class;)Ljava/util/List;", false);
    }

    @Override
    protected void insertTransform(MethodVisitor mv, Parameter param, Class<?> targetType, int local) {
        if (this.anno.ignoreEmpty()) {
            // Check that the list is not empty
            mv.visitVarInsn(ALOAD, local);
            Label success = new Label();
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "isEmpty", "()Z", true);
            mv.visitJumpInsn(IFEQ, success);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitLabel(success);
        }

        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ICONST_0);
        mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(targetType.getComponentType()));
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", true);
        mv.visitVarInsn(ASTORE, local);
    }

}
