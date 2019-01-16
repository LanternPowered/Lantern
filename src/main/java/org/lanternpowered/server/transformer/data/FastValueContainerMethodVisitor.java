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
package org.lanternpowered.server.transformer.data;

import static org.lanternpowered.server.transformer.data.FastValueContainerChecker.STATE_NOT_STORE;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;

import org.lanternpowered.server.data.FastCompositeValueStoreHelper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
final class FastValueContainerMethodVisitor extends MethodVisitor {

    private static final Map<String, String[]> entries = new HashMap<>();

    static {
        for (Method method : FastCompositeValueStoreHelper.class.getDeclaredMethods()) {
            final int modifiers = method.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                continue;
            }
            final String desc1 = Type.getMethodDescriptor(method);

            // Replace the first argument, this was originally not present because
            // it was a non static method directly targeting the loaded target object
            String oldDesc = desc1.replaceFirst("Lorg/spongepowered/api/data/value/mutable/CompositeValueStore;", "");
            final String desc2 = oldDesc;
            // Replace the boolean return type with the DataTransactionResult
            oldDesc = oldDesc.substring(0, oldDesc.length() - 1) + "Lorg/spongepowered/api/data/DataTransactionResult;";

            entries.put(method.getName() + ';' + oldDesc, new String[] { desc1, desc2 });
        }
    }

    private int store;
    private int opcode;
    private String[] entry;
    private String owner;
    private String name;
    private String desc;
    private boolean itf;

    FastValueContainerMethodVisitor(MethodVisitor mv) {
        super(ASM5, mv);
    }

    private void reset() {
        if (this.entry != null) {
            super.visitMethodInsn(this.opcode, this.owner, this.name, this.desc, this.itf);
            this.entry = null;
        }
    }

    @Override
    public void visitParameter(String name, int access) {
        reset();
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        reset();
        return super.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        reset();
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        reset();
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        reset();
        return super.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        reset();
        super.visitAttribute(attr);
    }

    @Override
    public void visitCode() {
        reset();
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        reset();
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    private void writeMethod() {
        if (this.store == 1) {
            // The store extends CompositeValueStore, we don't know about about ICompositeValueStore,
            // so we forward this to a helper method
            super.visitMethodInsn(INVOKESTATIC, "org/lanternpowered/server/data/FastCompositeValueStoreHelper",
                    this.name, this.entry[0], false);
        } else if (this.store == 2) {
            // The store extends ICompositeValueStore, we can use a direct method,
            // Fast methods names should always end with Fast
            super.visitMethodInsn(INVOKEVIRTUAL, this.owner, this.name + "Fast", this.entry[1], this.itf);
        } else {
            throw new IllegalStateException(this.store + "");
        }
        this.entry = null;
    }

    @Override
    public void visitInsn(int opcode) {
        // We have a winner, if the next operation is a POP, which discards the DataTransactionResult,
        // then we can replace this with a fast method.
        // Example code:
        /*
        final LanternEntity entity = new LanternItem(UUID.randomUUID());
        entity.offer(Keys.VELOCITY, Vector3d.ZERO);
         */
        // to
        /*
        final LanternEntity entity = new LanternItem(UUID.randomUUID());
        CompositeValueStoreHelper.offer(entity, Keys.VELOCITY, Vector3d.ZERO);
         */
        // or
        /*
        final LanternEntity entity = new LanternItem(UUID.randomUUID());
        entity.offerFast(Keys.VELOCITY, Vector3d.ZERO);
         */
        if (opcode == POP && this.entry != null) {
            writeMethod();
        } else {
            reset();
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        reset();
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        reset();
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        reset();
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        reset();
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        reset();
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        // If a isSuccessful method is called directly after retrieving the DataTransactionResult,
        // then we can also replace this with one method call
        // For example:
        /*
        final LanternEntity entity = new LanternItem(UUID.randomUUID());
        boolean test = entity.offer(Keys.VELOCITY, Vector3d.ZERO).isSuccessful();
         */
        // to
        /*
        final LanternEntity entity = new LanternItem(UUID.randomUUID());
        boolean test = CompositeValueStoreHelper.offer(entity, Keys.VELOCITY, Vector3d.ZERO);
         */
        // or
        /*
        final LanternEntity entity = new LanternItem(UUID.randomUUID());
        boolean test = entity.offerFast(Keys.VELOCITY, Vector3d.ZERO);
         */
        if (this.entry != null && opcode == INVOKEVIRTUAL && owner.equals("org/spongepowered/api/data/DataTransactionResult") &&
                name.equals("isSuccessful") && desc.equals("()Z")) {
            writeMethod();
            return;
        }

        reset();

        // Catch every method call that can possibly be replaced by a fast method,
        // the next opcodes will define if the method will actually be replaced.
        // If this is not the case then will the original method be written.
        String[] entry;
        int store;

        final String key = name + ';' + desc;
        if (opcode == INVOKEVIRTUAL && (entry = entries.get(key)) != null &&
                (store = FastValueContainerChecker.isCompositeValueStore(owner)) != STATE_NOT_STORE) {
            this.store = store;
            this.entry = entry;
            this.opcode = opcode;
            this.owner = owner;
            this.name = name;
            this.desc = desc;
            this.itf = itf;
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        reset();
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        reset();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        reset();
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        reset();
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        reset();
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        reset();
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        reset();
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        reset();
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        reset();
        return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        reset();
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        reset();
        return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        reset();
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        reset();
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        reset();
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        reset();
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        reset();
        super.visitEnd();
    }
}
