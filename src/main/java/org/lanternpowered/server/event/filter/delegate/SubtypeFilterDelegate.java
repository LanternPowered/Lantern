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

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import com.google.common.collect.Sets;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public abstract class SubtypeFilterDelegate implements FilterDelegate {

    private final Class<?>[] classes;

    protected SubtypeFilterDelegate(Class<?>[] classes) {
        this.classes = classes;
    }

    public void createFields(ClassWriter cw) {
        FieldVisitor fv = cw.visitField(0, "classes", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/Class<*>;>;", null);
        fv.visitEnd();
    }

    public void writeCtor(String name, ClassWriter cw, MethodVisitor mv) {
        // Initialize the class set
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Sets.class), "newHashSet", "()Ljava/util/HashSet;",
                false);
        mv.visitFieldInsn(PUTFIELD, name, "classes", "Ljava/util/Set;");

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name, "classes", "Ljava/util/Set;");
        for (Class<?> cls : this.classes) {
            // dup the field
            mv.visitInsn(DUP);
            // ldc the type
            mv.visitLdcInsn(Type.getType(cls));
            // add it to the set
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true);
            // pop the boolean result leaving just the original field
            mv.visitInsn(POP);
        }
        // pop the original field ref
        mv.visitInsn(POP);
    }
}
