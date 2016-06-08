/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.gradle.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public final class ClassDependencyCollector {

    private static final AnnotationVisitor EMPTY_ANNOTATION_VISITOR = new AnnotationVisitor(Opcodes.ASM5) {};
    private static final FieldVisitor EMPTY_FIELD_VISITOR = new FieldVisitor(Opcodes.ASM5) {

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath,
                String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }
    };
    private static final MethodVisitor EMPTY_METHOD_VISITOR = new MethodVisitor(Opcodes.ASM5) {

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath,
                String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc,
                boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath,
                String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath,
                String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath,
                Label[] start, Label[] end, int[] index, String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }
    };
    private static final ClassVisitor EMPTY_CLASS_VISITOR = new ClassVisitor(Opcodes.ASM5) {

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return EMPTY_ANNOTATION_VISITOR;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return EMPTY_METHOD_VISITOR;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return EMPTY_FIELD_VISITOR;
        }
    };

    /**
     * Reads the class file from the {@link InputStream} and collects
     * all the dependencies that are used by the class.
     *
     * @param is The input stream
     * @return A set with all the dependencies
     * @throws IOException
     */
    public static Set<String> collect(InputStream is) throws IOException {
        final ClassReader reader = new ClassReader(is);
        final Set<String> classNames = new HashSet<>();
        final RemappingClassAdapter visitor = new RemappingClassAdapter(EMPTY_CLASS_VISITOR, new Remapper() {
            @Override
            public String map(String typeName) {
                classNames.add(typeName + ".class");
                return super.map(typeName);
            }
        });
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return classNames;
    }

    private ClassDependencyCollector() {
    }
}
