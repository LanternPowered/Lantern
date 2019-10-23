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
package org.lanternpowered.server.transformer;

import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ASM5;

import org.lanternpowered.launch.transformer.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link ClassTransformer} to make final fields non final.
 */
public final class FinalFieldClassTransformer implements ClassTransformer {

    /**
     * All the inject annotations that should be tracked, if one of
     * these is found will the field be made non-final.
     */
    private static final List<String> annotations = Arrays.asList(
            "Ljavax/inject/Inject;",
            "Lcom/google/inject/Inject;",
            "Lninja/leaping/configurate/objectmapping/Setting;"
    );

    @Override
    public byte[] transform(ClassLoader loader, String className, byte[] byteCode) {
        final ClassReader classReader = new ClassReader(byteCode);

        // Fail fast in case we don't need transformation
        final int access = classReader.getAccess();
        if ((access & ACC_ANNOTATION) != 0 ||
                (access & ACC_INTERFACE) != 0) {
            return byteCode;
        }

        final ClassWriter classWriter = new ClassWriter(0);
        classReader.accept(new FinalFieldClassVisitor(classWriter), 0);
        return classWriter.toByteArray();
    }

    static final class FinalFieldClassVisitor extends ClassVisitor {

        FinalFieldClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if ((access & ACC_FINAL) == 0) {
                return super.visitField(access, name, desc, signature, value);
            }
            return new FieldNode(ASM5, access, name, desc, signature, value) {
                @Override
                public void visitEnd() {
                    super.visitEnd();

                    if (this.visibleAnnotations != null) {
                        // Depending on the collected annotations, transform the access
                        for (Object annotationNode : this.visibleAnnotations) {
                            // Check if a public field annotations is present
                            if (annotations.contains(((AnnotationNode) annotationNode).desc)) {
                                this.access &= ~ACC_FINAL; // Reset final state
                                break;
                            }
                        }
                    }

                    // Write the field
                    accept(FinalFieldClassVisitor.this.cv);
                }
            };
        }
    }
}
